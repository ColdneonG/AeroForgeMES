package com.fanmes.production.service;

import com.fanmes.common.audit.OperationLogService;
import com.fanmes.common.exception.BadRequestException;
import com.fanmes.common.exception.NotFoundException;
import com.fanmes.common.id.IdGenerator;
import com.fanmes.production.domain.barcode.BarcodeApplicationRule;
import com.fanmes.production.domain.barcode.BarcodeGenerationResult;
import com.fanmes.production.domain.barcode.BarcodeItemOption;
import com.fanmes.production.domain.barcode.BarcodePrintRecord;
import com.fanmes.production.domain.barcode.BarcodePrintResult;
import com.fanmes.production.domain.barcode.BarcodeRecord;
import com.fanmes.production.domain.barcode.BarcodeRule;
import com.fanmes.production.domain.barcode.BarcodeTemplate;
import com.fanmes.production.domain.barcode.BarcodeTraceEvent;
import com.fanmes.production.domain.barcode.BarcodeTraceNode;
import com.fanmes.production.domain.barcode.BarcodeTraceResult;
import com.fanmes.production.domain.barcode.BarcodeType;
import com.fanmes.production.dto.ProductionActionRequest;
import com.fanmes.production.dto.barcode.BarcodeApplicationRuleRequest;
import com.fanmes.production.dto.barcode.BarcodeBatchPrintRequest;
import com.fanmes.production.dto.barcode.BarcodeGenerateRequest;
import com.fanmes.production.dto.barcode.BarcodeParentBindRequest;
import com.fanmes.production.dto.barcode.BarcodePrintRequest;
import com.fanmes.production.dto.barcode.BarcodeRuleRequest;
import com.fanmes.production.dto.barcode.BarcodeScanRequest;
import com.fanmes.production.infrastructure.BarcodeRepository;
import com.fanmes.production.infrastructure.BarcodeRepository.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class BarcodeService {
    private static final String ENABLED = "启用";
    private static final String DISABLED = "停用";
    private static final String VOIDED = "作废";
    private static final String GENERATED = "已生成";
    private static final String PRINTED = "已打印";
    private static final String SCANNED = "已扫码";
    private static final String CLOSED = "已关闭";

    private final BarcodeRepository repository;
    private final BarcodeExpressionEngine expressionEngine;
    private final OperationLogService operationLogService;

    public BarcodeService(
            BarcodeRepository repository,
            BarcodeExpressionEngine expressionEngine,
            OperationLogService operationLogService
    ) {
        this.repository = repository;
        this.expressionEngine = expressionEngine;
        this.operationLogService = operationLogService;
    }

    public List<BarcodeType> listTypes(String status) {
        return repository.listTypes(status);
    }

    public List<BarcodeItemOption> listItems(String keyword, String status) {
        return repository.listItems(keyword, status);
    }

    public List<BarcodeTemplate> listTemplates(Long typeId, String status) {
        return repository.listTemplates(typeId, status);
    }

    public List<BarcodeRule> listRules(String keyword, Long typeId, String status) {
        return repository.listRules(keyword, typeId, status);
    }

    public BarcodeRule getRule(Long id) {
        return repository.findRuleById(id)
                .orElseThrow(() -> new NotFoundException("barcode rule not found: " + id));
    }

    @Transactional
    public BarcodeRule createRule(BarcodeRuleRequest request) {
        ensureTypeExists(request.typeId());
        BarcodeRule rule = toRule(IdGenerator.nextId(), request);
        repository.insertRule(rule);
        record("bc_rule", rule.id(), "create", null, rule.status(), null, "create barcode rule");
        return getRule(rule.id());
    }

    @Transactional
    public BarcodeRule updateRule(Long id, BarcodeRuleRequest request) {
        BarcodeRule existing = getRule(id);
        if (VOIDED.equals(existing.status())) {
            throw new BadRequestException("voided barcode rule cannot be edited");
        }
        ensureTypeExists(request.typeId());
        BarcodeRule updated = toRule(id, request);
        repository.updateRule(updated);
        record("bc_rule", id, "update", existing.status(), updated.status(), null, "update barcode rule");
        return getRule(id);
    }

    @Transactional
    public BarcodeRule enableRule(Long id, ProductionActionRequest request) {
        return changeRuleStatus(id, ENABLED, "enable", request);
    }

    @Transactional
    public BarcodeRule disableRule(Long id, ProductionActionRequest request) {
        return changeRuleStatus(id, DISABLED, "disable", request);
    }

    @Transactional
    public void deleteRule(Long id) {
        BarcodeRule existing = getRule(id);
        if (repository.countApplicationRulesByRule(id) > 0 || repository.countBarcodesByRule(id) > 0) {
            throw new BadRequestException("barcode rule is referenced; disable it instead");
        }
        repository.deleteRule(id);
        record("bc_rule", id, "delete", existing.status(), null, null, "delete barcode rule");
    }

    public List<BarcodeApplicationRule> listApplicationRules(
            String keyword,
            Long itemId,
            Long typeId,
            String status
    ) {
        return repository.listApplicationRules(keyword, itemId, typeId, status);
    }

    public BarcodeApplicationRule getApplicationRule(Long id) {
        return repository.findApplicationRuleById(id)
                .orElseThrow(() -> new NotFoundException("barcode application rule not found: " + id));
    }

    @Transactional
    public BarcodeApplicationRule createApplicationRule(BarcodeApplicationRuleRequest request) {
        validateApplicationRequest(request);
        BarcodeApplicationRule rule = toApplicationRule(IdGenerator.nextId(), request);
        repository.insertApplicationRule(rule);
        record("bc_application_rule", rule.id(), "create", null, rule.status(), null,
                "create barcode application rule");
        return getApplicationRule(rule.id());
    }

    @Transactional
    public BarcodeApplicationRule updateApplicationRule(Long id, BarcodeApplicationRuleRequest request) {
        BarcodeApplicationRule existing = getApplicationRule(id);
        if (VOIDED.equals(existing.status())) {
            throw new BadRequestException("voided barcode application rule cannot be edited");
        }
        validateApplicationRequest(request);
        BarcodeApplicationRule updated = toApplicationRule(id, request);
        repository.updateApplicationRule(updated);
        record("bc_application_rule", id, "update", existing.status(), updated.status(), null,
                "update barcode application rule");
        return getApplicationRule(id);
    }

    @Transactional
    public void deleteApplicationRule(Long id) {
        BarcodeApplicationRule existing = getApplicationRule(id);
        if (repository.countBarcodesByApplicationRule(id) > 0) {
            throw new BadRequestException("barcode application rule is referenced; disable it instead");
        }
        repository.deleteApplicationRule(id);
        record("bc_application_rule", id, "delete", existing.status(), null, null,
                "delete barcode application rule");
    }

    public List<BarcodeRecord> listBarcodes(
            String keyword,
            String status,
            Long typeId,
            Long workOrderId,
            Long taskId,
            String batchNo
    ) {
        return repository.listBarcodes(keyword, status, typeId, workOrderId, taskId, batchNo);
    }

    public BarcodeRecord getBarcode(Long id) {
        return repository.findBarcodeById(id)
                .orElseThrow(() -> new NotFoundException("barcode not found: " + id));
    }

    @Transactional
    public BarcodeGenerationResult generate(BarcodeGenerateRequest request) {
        BarcodeApplicationRule application = resolveApplicationRule(request);
        BarcodeRule rule = getRule(application.ruleId());
        if (!ENABLED.equals(application.status()) || !ENABLED.equals(rule.status())) {
            throw new BadRequestException("barcode rule and application rule must be enabled");
        }

        Long itemId = request.itemId() == null ? application.itemId() : request.itemId();
        Lookup item = requireLookup(repository.findItem(itemId), "item", itemId);
        Lookup workOrder = optionalLookup(repository.findWorkOrder(request.workOrderId()), "work order", request.workOrderId());
        Lookup task = optionalLookup(repository.findTask(request.taskId()), "task", request.taskId());
        BarcodeRecord parent = request.parentBarcodeId() == null ? null : getBarcode(request.parentBarcodeId());
        ensureActiveParent(parent);

        List<String> externalValues = normalizedExternalValues(request.externalValues());
        int quantity = externalValues.isEmpty() ? defaultQuantity(request.quantity()) : externalValues.size();
        boolean usesSequence = expressionEngine.requiresSequence(rule.ruleExpression());
        if (externalValues.isEmpty() && quantity > 1 && !usesSequence) {
            throw new BadRequestException("rule without a serial token can only generate one barcode at a time");
        }

        LocalDate today = LocalDate.now();
        long sequenceEnd = usesSequence
                ? repository.reserveSequence(rule.id(), today.format(DateTimeFormatter.BASIC_ISO_DATE), quantity)
                : quantity;
        long sequenceStart = sequenceEnd - quantity + 1;
        String batchNo = trimToNull(request.batchNo());
        String itemCode = StringUtils.hasText(request.itemCode()) ? request.itemCode().trim() : item.code();
        Map<String, String> context = Map.of(
                "itemCode", nullToEmpty(itemCode),
                "batchNo", nullToEmpty(batchNo),
                "workOrderNo", workOrder == null ? "" : nullToEmpty(workOrder.code()),
                "taskNo", task == null ? "" : nullToEmpty(task.code())
        );

        List<BarcodeRecord> created = new ArrayList<>(quantity);
        LocalDateTime now = LocalDateTime.now();
        for (int index = 0; index < quantity; index++) {
            String value = externalValues.isEmpty()
                    ? expressionEngine.render(rule.ruleExpression(), today, sequenceStart + index,
                    defaultSerialLength(rule.serialLength()), context)
                    : externalValues.get(index);
            BarcodeRecord barcode = new BarcodeRecord(
                    IdGenerator.nextId(), value, application.typeId(), application.typeCode(), application.typeName(),
                    application.id(), application.applicationRuleCode(), itemId, item.code(), item.name(), batchNo,
                    request.workOrderId(), workOrder == null ? null : workOrder.code(),
                    request.taskId(), task == null ? null : task.code(),
                    parent == null ? null : parent.id(), parent == null ? null : parent.barcodeValue(),
                    0, defaultSourceType(request.sourceType(), externalValues), GENERATED, now
            );
            repository.insertBarcode(barcode);
            repository.insertTraceEvent(newTraceEvent(
                    barcode, "生成", "BARCODE", barcode.id(), request.operatorId(), null, null, null
            ));
            created.add(getBarcode(barcode.id()));
        }
        record("bc_barcode", created.get(0).id(), "generate", null, GENERATED, request.operatorId(),
                "generated " + created.size() + " barcode(s)");
        return new BarcodeGenerationResult(created.size(), created);
    }

    @Transactional
    public BarcodePrintResult printBarcode(Long id, BarcodePrintRequest request) {
        BarcodeRecord barcode = getBarcode(id);
        ensureBarcodeOperable(barcode);
        if (barcode.printCount() != null && barcode.printCount() > 0 && !StringUtils.hasText(request.reason())) {
            throw new BadRequestException("reprint reason is required");
        }
        int copies = request.copies() == null ? 1 : request.copies();
        BarcodeTemplate template = resolveTemplate(barcode, request.templateId());
        if (!ENABLED.equals(template.status())) {
            throw new BadRequestException("barcode template must be enabled");
        }
        if (barcode.typeId() != null && template.typeId() != null && !barcode.typeId().equals(template.typeId())) {
            throw new BadRequestException("barcode template type does not match barcode type");
        }

        String eventType = barcode.printCount() != null && barcode.printCount() > 0 ? "补打" : "打印";
        int updated = repository.addPrintCount(id, copies);
        if (updated == 0) {
            throw new BadRequestException("barcode cannot be printed in current status");
        }
        BarcodePrintRecord printRecord = new BarcodePrintRecord(
                IdGenerator.nextId(), barcode.id(), barcode.barcodeValue(), template.id(), template.templateName(),
                request.operatorId(), null, LocalDateTime.now(), copies,
                StringUtils.hasText(request.printerName()) ? request.printerName().trim() : "BROWSER"
        );
        repository.insertPrintRecord(printRecord);
        repository.insertTraceEvent(newTraceEvent(
                barcode, eventType, "BARCODE_PRINT", printRecord.id(), request.operatorId(), null, null, null
        ));
        record("bc_barcode", id, eventType, barcode.status(), PRINTED, request.operatorId(), request.reason());
        BarcodeRecord refreshed = getBarcode(id);
        BarcodePrintRecord storedRecord = repository.listPrintRecords(id).stream()
                .filter(record -> record.id().equals(printRecord.id()))
                .findFirst()
                .orElse(printRecord);
        return new BarcodePrintResult(refreshed, storedRecord, template);
    }

    @Transactional
    public List<BarcodePrintResult> batchPrint(BarcodeBatchPrintRequest request) {
        List<BarcodePrintResult> results = new ArrayList<>();
        for (Long id : new LinkedHashSet<>(request.barcodeIds())) {
            results.add(printBarcode(id, request.toPrintRequest()));
        }
        return results;
    }

    public List<BarcodePrintRecord> listPrintRecords(Long barcodeId) {
        getBarcode(barcodeId);
        return repository.listPrintRecords(barcodeId);
    }

    public List<BarcodeTraceEvent> listEvents(Long barcodeId) {
        getBarcode(barcodeId);
        return repository.listTraceEvents(List.of(barcodeId));
    }

    @Transactional
    public BarcodeRecord scan(BarcodeScanRequest request) {
        BarcodeRecord barcode = repository.findBarcodeByValue(request.barcodeValue().trim())
                .orElseThrow(() -> new NotFoundException("barcode not found: " + request.barcodeValue().trim()));
        ensureBarcodeOperable(barcode);

        if (StringUtils.hasText(request.parentBarcodeValue())) {
            BarcodeRecord parent = findBarcodeByValue(request.parentBarcodeValue());
            bindParentInternal(barcode, parent);
        }
        repository.updateBarcodeStatus(barcode.id(), SCANNED);
        BarcodeRecord contextBarcode = getBarcode(barcode.id());
        repository.insertTraceEvent(new BarcodeTraceEvent(
                IdGenerator.nextId(), barcode.id(), barcode.barcodeValue(),
                StringUtils.hasText(request.eventType()) ? request.eventType().trim() : "扫码",
                StringUtils.hasText(request.bizType()) ? request.bizType().trim() : "SCAN",
                request.bizId(),
                request.workOrderId() == null ? contextBarcode.workOrderId() : request.workOrderId(), null,
                request.taskId() == null ? contextBarcode.taskId() : request.taskId(), null,
                request.processId(), null, request.stationId(), null,
                request.deviceId(), null, request.operatorId(), null, LocalDateTime.now(), "成功"
        ));
        record("bc_barcode", barcode.id(), "scan", barcode.status(), SCANNED, request.operatorId(),
                "barcode scan");
        return getBarcode(barcode.id());
    }

    @Transactional
    public BarcodeRecord bindParent(Long id, BarcodeParentBindRequest request) {
        BarcodeRecord barcode = getBarcode(id);
        ensureBarcodeOperable(barcode);
        BarcodeRecord parent = findBarcodeByValue(request.parentBarcodeValue());
        bindParentInternal(barcode, parent);
        repository.insertTraceEvent(newTraceEvent(
                barcode, "装箱", "BARCODE_PARENT", parent.id(), request.operatorId(), null, null, null
        ));
        record("bc_barcode", id, "bind-parent", barcode.status(), barcode.status(), request.operatorId(),
                request.remark());
        return getBarcode(id);
    }

    @Transactional
    public BarcodeRecord close(Long id, ProductionActionRequest request) {
        return changeBarcodeStatus(id, CLOSED, "close", request);
    }

    @Transactional
    public BarcodeRecord voidBarcode(Long id, ProductionActionRequest request) {
        return changeBarcodeStatus(id, VOIDED, "void", request);
    }

    public BarcodeTraceResult trace(String keyword, String mode) {
        if (!StringUtils.hasText(keyword)) {
            throw new BadRequestException("trace keyword is required");
        }
        String queryMode = normalizeMode(mode);
        List<BarcodeRecord> roots = repository.findBarcodesForTrace(queryMode, keyword.trim());
        if (roots.isEmpty()) {
            throw new NotFoundException("no traceability data found: " + keyword.trim());
        }

        LinkedHashMap<Long, BarcodeRecord> records = new LinkedHashMap<>();
        Map<Long, String> relations = new LinkedHashMap<>();
        Map<Long, Long> nodeParents = new LinkedHashMap<>();
        for (BarcodeRecord root : roots) {
            addRecord(records, relations, nodeParents, root, "查询对象", null);
            collectAncestors(root, records, relations, nodeParents, 0);
            collectChildren(root, records, relations, nodeParents, 0);
        }

        List<Long> rootIds = roots.stream().map(BarcodeRecord::id).toList();
        Long firstRootId = roots.get(0).id();
        for (BarcodeRecord material : repository.findMaterialBarcodes(rootIds)) {
            addRecord(records, relations, nodeParents, material, "关键物料", firstRootId);
        }
        for (BarcodeRecord product : repository.findProductBarcodes(rootIds)) {
            addRecord(records, relations, nodeParents, product, "关联产品", firstRootId);
        }

        List<BarcodeTraceNode> nodes = records.values().stream()
                .map(record -> new BarcodeTraceNode(
                        record.id().toString(),
                        nodeParents.get(record.id()) == null ? null : nodeParents.get(record.id()).toString(),
                        record.barcodeValue(), buildNodeTitle(record), relations.get(record.id()),
                        record.typeName(), record.status()
                ))
                .toList();
        List<BarcodeTraceEvent> events = repository.listTraceEvents(new ArrayList<>(records.keySet()));
        return new BarcodeTraceResult(
                keyword.trim(), queryMode, nodes, traceDetails(roots, records.size(), events.size()), events
        );
    }

    private BarcodeRule changeRuleStatus(
            Long id,
            String targetStatus,
            String action,
            ProductionActionRequest request
    ) {
        BarcodeRule existing = getRule(id);
        if (VOIDED.equals(existing.status())) {
            throw new BadRequestException("voided barcode rule status cannot change");
        }
        if (targetStatus.equals(existing.status())) {
            return existing;
        }
        if (repository.updateRuleStatus(id, existing.status(), targetStatus) == 0) {
            throw new BadRequestException("barcode rule status changed; refresh and retry");
        }
        record("bc_rule", id, action, existing.status(), targetStatus,
                request == null ? null : request.operatorId(), request == null ? null : request.remark());
        return getRule(id);
    }

    private BarcodeRecord changeBarcodeStatus(
            Long id,
            String targetStatus,
            String action,
            ProductionActionRequest request
    ) {
        BarcodeRecord existing = getBarcode(id);
        if (VOIDED.equals(existing.status())) {
            throw new BadRequestException("voided barcode status cannot change");
        }
        if (repository.updateBarcodeStatus(id, targetStatus) == 0) {
            throw new BadRequestException("barcode status changed; refresh and retry");
        }
        repository.insertTraceEvent(newTraceEvent(
                existing, targetStatus, "BARCODE", id,
                request == null ? null : request.operatorId(), null, null, null
        ));
        record("bc_barcode", id, action, existing.status(), targetStatus,
                request == null ? null : request.operatorId(), request == null ? null : request.remark());
        return getBarcode(id);
    }

    private BarcodeApplicationRule resolveApplicationRule(BarcodeGenerateRequest request) {
        if (request.applicationRuleId() != null) {
            return getApplicationRule(request.applicationRuleId());
        }
        return repository.findEnabledApplicationByRuleId(request.ruleId())
                .orElseThrow(() -> new BadRequestException("enabled application rule not found for rule: " + request.ruleId()));
    }

    private BarcodeTemplate resolveTemplate(BarcodeRecord barcode, Long requestedTemplateId) {
        Long templateId = requestedTemplateId;
        if (templateId == null && barcode.applicationRuleId() != null) {
            templateId = getApplicationRule(barcode.applicationRuleId()).templateId();
        }
        if (templateId == null) {
            throw new BadRequestException("barcode template is required");
        }
        Long resolvedTemplateId = templateId;
        return repository.findTemplateById(resolvedTemplateId)
                .orElseThrow(() -> new BadRequestException("barcode template not found: " + resolvedTemplateId));
    }

    private void validateApplicationRequest(BarcodeApplicationRuleRequest request) {
        BarcodeType type = ensureTypeExists(request.typeId());
        BarcodeRule rule = getRule(request.ruleId());
        BarcodeTemplate template = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> new BadRequestException("barcode template not found: " + request.templateId()));
        requireLookup(repository.findItem(request.itemId()), "item", request.itemId());
        if (!request.typeId().equals(rule.typeId()) || !request.typeId().equals(template.typeId())) {
            throw new BadRequestException("application rule, barcode rule and template types must match");
        }
        if (VOIDED.equals(rule.status()) || VOIDED.equals(type.status())) {
            throw new BadRequestException("voided barcode type or rule cannot be bound");
        }
    }

    private BarcodeType ensureTypeExists(Long id) {
        return repository.findTypeById(id)
                .orElseThrow(() -> new BadRequestException("barcode type not found: " + id));
    }

    private void bindParentInternal(BarcodeRecord barcode, BarcodeRecord parent) {
        ensureActiveParent(parent);
        if (barcode.id().equals(parent.id())) {
            throw new BadRequestException("barcode cannot be its own parent");
        }
        BarcodeRecord cursor = parent;
        Set<Long> visited = new LinkedHashSet<>();
        while (cursor != null && visited.add(cursor.id())) {
            if (barcode.id().equals(cursor.parentBarcodeId())) {
                throw new BadRequestException("barcode parent binding would create a cycle");
            }
            cursor = cursor.parentBarcodeId() == null ? null : getBarcode(cursor.parentBarcodeId());
        }
        repository.updateParent(barcode.id(), parent.id());
    }

    private void ensureActiveParent(BarcodeRecord parent) {
        if (parent != null && (VOIDED.equals(parent.status()) || CLOSED.equals(parent.status()))) {
            throw new BadRequestException("parent barcode must be active");
        }
    }

    private void ensureBarcodeOperable(BarcodeRecord barcode) {
        if (VOIDED.equals(barcode.status()) || CLOSED.equals(barcode.status())) {
            throw new BadRequestException("barcode cannot be operated in current status: " + barcode.status());
        }
    }

    private BarcodeRecord findBarcodeByValue(String value) {
        return repository.findBarcodeByValue(value.trim())
                .orElseThrow(() -> new NotFoundException("barcode not found: " + value.trim()));
    }

    private BarcodeTraceEvent newTraceEvent(
            BarcodeRecord barcode,
            String eventType,
            String bizType,
            Long bizId,
            Long operatorId,
            Long processId,
            Long stationId,
            Long deviceId
    ) {
        return new BarcodeTraceEvent(
                IdGenerator.nextId(), barcode.id(), barcode.barcodeValue(), eventType, bizType, bizId,
                barcode.workOrderId(), barcode.workOrderNo(), barcode.taskId(), barcode.taskNo(),
                processId, null, stationId, null, deviceId, null,
                operatorId, null, LocalDateTime.now(), "成功"
        );
    }

    private void collectAncestors(
            BarcodeRecord record,
            Map<Long, BarcodeRecord> records,
            Map<Long, String> relations,
            Map<Long, Long> nodeParents,
            int depth
    ) {
        if (record.parentBarcodeId() == null || depth >= 8) {
            return;
        }
        BarcodeRecord parent = getBarcode(record.parentBarcodeId());
        addRecord(records, relations, nodeParents, parent, "上级包装", null);
        nodeParents.put(record.id(), parent.id());
        collectAncestors(parent, records, relations, nodeParents, depth + 1);
    }

    private void collectChildren(
            BarcodeRecord record,
            Map<Long, BarcodeRecord> records,
            Map<Long, String> relations,
            Map<Long, Long> nodeParents,
            int depth
    ) {
        if (depth >= 8) {
            return;
        }
        for (BarcodeRecord child : repository.findChildren(record.id())) {
            if (records.containsKey(child.id())) {
                continue;
            }
            addRecord(records, relations, nodeParents, child, "下级条码", record.id());
            collectChildren(child, records, relations, nodeParents, depth + 1);
        }
    }

    private void addRecord(
            Map<Long, BarcodeRecord> records,
            Map<Long, String> relations,
            Map<Long, Long> nodeParents,
            BarcodeRecord record,
            String relation,
            Long parentId
    ) {
        records.putIfAbsent(record.id(), record);
        relations.putIfAbsent(record.id(), relation);
        if (parentId != null) {
            nodeParents.putIfAbsent(record.id(), parentId);
        }
    }

    private Map<String, Object> traceDetails(
            List<BarcodeRecord> roots,
            int nodeCount,
            int eventCount
    ) {
        BarcodeRecord root = roots.get(0);
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("barcode", root.barcodeValue());
        details.put("type", nullToEmpty(root.typeName()));
        details.put("item", displayItem(root));
        details.put("batchNo", nullToEmpty(root.batchNo()));
        details.put("workOrder", nullToEmpty(root.workOrderNo()));
        details.put("task", nullToEmpty(root.taskNo()));
        details.put("status", nullToEmpty(root.status()));
        details.put("printCount", root.printCount() == null ? 0 : root.printCount());
        details.put("matchedBarcodes", roots.size());
        details.put("genealogyNodes", nodeCount);
        details.put("events", eventCount);
        return details;
    }

    private String buildNodeTitle(BarcodeRecord record) {
        String type = StringUtils.hasText(record.typeName()) ? record.typeName() : "条码";
        String item = displayItem(record);
        return "[" + type + "] " + record.barcodeValue()
                + (StringUtils.hasText(item) ? " · " + item : "");
    }

    private String displayItem(BarcodeRecord record) {
        if (StringUtils.hasText(record.itemCode()) && StringUtils.hasText(record.itemName())) {
            return record.itemCode() + " / " + record.itemName();
        }
        return StringUtils.hasText(record.itemName()) ? record.itemName() : nullToEmpty(record.itemCode());
    }

    private BarcodeRule toRule(Long id, BarcodeRuleRequest request) {
        return new BarcodeRule(
                id, request.ruleCode().trim(), request.ruleName().trim(), request.typeId(),
                null, null, request.ruleExpression().trim(), request.serialLength(),
                StringUtils.hasText(request.status()) ? request.status().trim() : "草稿",
                null, null, null, null, null, null, null, null, null, null
        );
    }

    private BarcodeApplicationRule toApplicationRule(Long id, BarcodeApplicationRuleRequest request) {
        return new BarcodeApplicationRule(
                id, request.applicationRuleCode().trim(), request.itemId(), null, null,
                request.typeId(), null, null, request.ruleId(), null, null, null, null,
                request.templateId(), null, null, request.barcodeMode().trim(),
                request.sourceType().trim(), StringUtils.hasText(request.status()) ? request.status().trim() : ENABLED
        );
    }

    private String normalizeMode(String mode) {
        if (!StringUtils.hasText(mode)) {
            return "SN";
        }
        String normalized = mode.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        return switch (normalized) {
            case "WORKORDER", "WORK_ORDER", "WO" -> "WORK_ORDER";
            case "BATCH", "LOT" -> "BATCH";
            default -> "SN";
        };
    }

    private List<String> normalizedExternalValues(List<String> values) {
        if (values == null) {
            return List.of();
        }
        List<String> normalized = values.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
        if (normalized.stream().anyMatch(value -> value.length() > 128)) {
            throw new BadRequestException("external barcode exceeds 128 characters");
        }
        return normalized;
    }

    private int defaultQuantity(Integer quantity) {
        return quantity == null ? 1 : quantity;
    }

    private int defaultSerialLength(Integer length) {
        return length == null ? 4 : length;
    }

    private String defaultSourceType(String sourceType, List<String> externalValues) {
        if (StringUtils.hasText(sourceType)) {
            return sourceType.trim();
        }
        return externalValues.isEmpty() ? "生成" : "外部导入";
    }

    private Lookup requireLookup(Lookup lookup, String label, Long id) {
        if (lookup == null) {
            throw new BadRequestException(label + " not found: " + id);
        }
        return lookup;
    }

    private Lookup optionalLookup(Lookup lookup, String label, Long id) {
        return id == null ? null : requireLookup(lookup, label, id);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void record(
            String table,
            Long id,
            String action,
            String oldStatus,
            String newStatus,
            Long operatorId,
            String remark
    ) {
        operationLogService.recordStatusChange(
                "barcode", table, id, action, oldStatus, newStatus, operatorId, remark
        );
    }
}
