package com.fanmes.production.infrastructure;

import com.fanmes.production.domain.sop.SopAttachment;
import com.fanmes.production.domain.sop.SopBinding;
import com.fanmes.production.domain.sop.SopDocument;
import com.fanmes.production.domain.sop.SopExecutionRecord;
import com.fanmes.production.domain.sop.SopMatchCandidate;
import com.fanmes.production.domain.sop.SopModel;
import com.fanmes.production.domain.sop.SopModelVersion;
import com.fanmes.production.domain.sop.SopStep;
import com.fanmes.production.domain.sop.SopStepExecutionRecord;
import com.fanmes.production.domain.sop.SopTaskContext;
import com.fanmes.production.domain.sop.SopTaskSnapshot;
import com.fanmes.production.domain.sop.SopVersion;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SopRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public SopRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<SopDocument> listDocuments(String keyword, String category, String status) {
        StringBuilder sql = new StringBuilder("""
                select id, sop_code, sop_name, category, owner_id, status,
                       current_version_id, created_at, updated_at
                from sop_document
                where 1 = 1
                """);
        MapSqlParameterSource params = params();
        appendKeyword(sql, params, keyword, "sop_code", "sop_name");
        appendEquals(sql, params, "category", category);
        appendEquals(sql, params, "status", status);
        sql.append(" order by updated_at desc, id desc");
        return jdbc.query(sql.toString(), params, documentMapper());
    }

    public Optional<SopDocument> findDocumentById(Long id) {
        return findOne("""
                select id, sop_code, sop_name, category, owner_id, status,
                       current_version_id, created_at, updated_at
                from sop_document
                where id = :id
                """, Map.of("id", id), documentMapper());
    }

    public Optional<SopDocument> findDocumentByCode(String sopCode) {
        if (!StringUtils.hasText(sopCode)) {
            return Optional.empty();
        }
        return findOne("""
                select id, sop_code, sop_name, category, owner_id, status,
                       current_version_id, created_at, updated_at
                from sop_document
                where sop_code = :sopCode
                """, Map.of("sopCode", sopCode.trim()), documentMapper());
    }

    public void insertDocument(SopDocument document) {
        jdbc.update("""
                insert into sop_document (
                    id, sop_code, sop_name, category, owner_id, status,
                    current_version_id, created_at, updated_at
                )
                values (
                    :id, :sopCode, :sopName, :category, :ownerId, :status,
                    :currentVersionId, :createdAt, :updatedAt
                )
                """, documentParams(document));
    }

    public int updateDocument(SopDocument document) {
        return jdbc.update("""
                update sop_document
                set sop_code = :sopCode,
                    sop_name = :sopName,
                    category = :category,
                    owner_id = :ownerId,
                    status = :status,
                    current_version_id = :currentVersionId,
                    updated_at = :updatedAt
                where id = :id
                """, documentParams(document));
    }

    public int updateDocumentCurrentVersion(Long sopId, Long versionId) {
        return jdbc.update("""
                update sop_document
                set current_version_id = :versionId,
                    updated_at = :updatedAt
                where id = :sopId
                """, params()
                .addValue("sopId", sopId)
                .addValue("versionId", versionId)
                .addValue("updatedAt", LocalDateTime.now()));
    }

    public List<SopVersion> listVersions(Long sopId) {
        return jdbc.query("""
                select id, sop_id, version_no, revision_type, status, effective_from,
                       effective_to, submit_by, submit_at, review_by, review_at,
                       approve_by, approve_at, publish_by, publish_at, model_version_id,
                       remark, created_at, updated_at
                from sop_version
                where sop_id = :sopId
                order by created_at desc, id desc
                """, Map.of("sopId", sopId), versionMapper());
    }

    public Optional<SopVersion> findVersionById(Long id) {
        return findOne("""
                select id, sop_id, version_no, revision_type, status, effective_from,
                       effective_to, submit_by, submit_at, review_by, review_at,
                       approve_by, approve_at, publish_by, publish_at, model_version_id,
                       remark, created_at, updated_at
                from sop_version
                where id = :id
                """, Map.of("id", id), versionMapper());
    }

    public Optional<SopVersion> findVersionByNo(Long sopId, String versionNo) {
        return findOne("""
                select id, sop_id, version_no, revision_type, status, effective_from,
                       effective_to, submit_by, submit_at, review_by, review_at,
                       approve_by, approve_at, publish_by, publish_at, model_version_id,
                       remark, created_at, updated_at
                from sop_version
                where sop_id = :sopId and version_no = :versionNo
                """, Map.of("sopId", sopId, "versionNo", versionNo), versionMapper());
    }

    public void insertVersion(SopVersion version) {
        jdbc.update("""
                insert into sop_version (
                    id, sop_id, version_no, revision_type, status, effective_from,
                    effective_to, submit_by, submit_at, review_by, review_at,
                    approve_by, approve_at, publish_by, publish_at, model_version_id,
                    remark, created_at, updated_at
                )
                values (
                    :id, :sopId, :versionNo, :revisionType, :status, :effectiveFrom,
                    :effectiveTo, :submitBy, :submitAt, :reviewBy, :reviewAt,
                    :approveBy, :approveAt, :publishBy, :publishAt, :modelVersionId,
                    :remark, :createdAt, :updatedAt
                )
                """, versionParams(version));
    }

    public int updateVersion(SopVersion version) {
        return jdbc.update("""
                update sop_version
                set version_no = :versionNo,
                    revision_type = :revisionType,
                    status = :status,
                    effective_from = :effectiveFrom,
                    effective_to = :effectiveTo,
                    submit_by = :submitBy,
                    submit_at = :submitAt,
                    review_by = :reviewBy,
                    review_at = :reviewAt,
                    approve_by = :approveBy,
                    approve_at = :approveAt,
                    publish_by = :publishBy,
                    publish_at = :publishAt,
                    model_version_id = :modelVersionId,
                    remark = :remark,
                    updated_at = :updatedAt
                where id = :id
                """, versionParams(version));
    }

    public int updateVersionModelVersion(Long versionId, Long modelVersionId) {
        return jdbc.update("""
                update sop_version
                set model_version_id = :modelVersionId,
                    updated_at = :updatedAt
                where id = :versionId
                """, params()
                .addValue("versionId", versionId)
                .addValue("modelVersionId", modelVersionId)
                .addValue("updatedAt", LocalDateTime.now()));
    }

    public List<SopStep> listSteps(Long versionId) {
        return jdbc.query("""
                select id, version_id, step_no, step_name, instruction, content_type,
                       standard_duration, key_step, min_stay_seconds, confirm_required,
                       parameter_required, photo_required, skip_allowed, abnormal_handling,
                       quality_item_id, andon_type_id, enabled, created_at, updated_at
                from sop_step
                where version_id = :versionId
                order by step_no asc, id asc
                """, Map.of("versionId", versionId), stepMapper());
    }

    public Optional<SopStep> findStepById(Long id) {
        return findOne("""
                select id, version_id, step_no, step_name, instruction, content_type,
                       standard_duration, key_step, min_stay_seconds, confirm_required,
                       parameter_required, photo_required, skip_allowed, abnormal_handling,
                       quality_item_id, andon_type_id, enabled, created_at, updated_at
                from sop_step
                where id = :id
                """, Map.of("id", id), stepMapper());
    }

    public void insertStep(SopStep step) {
        jdbc.update("""
                insert into sop_step (
                    id, version_id, step_no, step_name, instruction, content_type,
                    standard_duration, key_step, min_stay_seconds, confirm_required,
                    parameter_required, photo_required, skip_allowed, abnormal_handling,
                    quality_item_id, andon_type_id, enabled, created_at, updated_at
                )
                values (
                    :id, :versionId, :stepNo, :stepName, :instruction, :contentType,
                    :standardDuration, :keyStep, :minStaySeconds, :confirmRequired,
                    :parameterRequired, :photoRequired, :skipAllowed, :abnormalHandling,
                    :qualityItemId, :andonTypeId, :enabled, :createdAt, :updatedAt
                )
                """, stepParams(step));
    }

    public int updateStep(SopStep step) {
        return jdbc.update("""
                update sop_step
                set step_no = :stepNo,
                    step_name = :stepName,
                    instruction = :instruction,
                    content_type = :contentType,
                    standard_duration = :standardDuration,
                    key_step = :keyStep,
                    min_stay_seconds = :minStaySeconds,
                    confirm_required = :confirmRequired,
                    parameter_required = :parameterRequired,
                    photo_required = :photoRequired,
                    skip_allowed = :skipAllowed,
                    abnormal_handling = :abnormalHandling,
                    quality_item_id = :qualityItemId,
                    andon_type_id = :andonTypeId,
                    enabled = :enabled,
                    updated_at = :updatedAt
                where id = :id
                """, stepParams(step));
    }

    public int deleteStep(Long id) {
        return jdbc.update("delete from sop_step where id = :id", Map.of("id", id));
    }

    public int countSteps(Long versionId) {
        Integer count = jdbc.queryForObject("""
                select count(*)
                from sop_step
                where version_id = :versionId and enabled = 1
                """, Map.of("versionId", versionId), Integer.class);
        return count == null ? 0 : count;
    }

    public List<SopAttachment> listAttachments(Long versionId) {
        return jdbc.query("""
                select id, version_id, step_id, attachment_type, file_name, content_type,
                       file_size, object_key, file_url, sha256, display_order, created_at
                from sop_attachment
                where version_id = :versionId
                order by display_order asc, id asc
                """, Map.of("versionId", versionId), attachmentMapper());
    }

    public Optional<SopAttachment> findAttachmentById(Long id) {
        return findOne("""
                select id, version_id, step_id, attachment_type, file_name, content_type,
                       file_size, object_key, file_url, sha256, display_order, created_at
                from sop_attachment
                where id = :id
                """, Map.of("id", id), attachmentMapper());
    }

    public void insertAttachment(SopAttachment attachment) {
        jdbc.update("""
                insert into sop_attachment (
                    id, version_id, step_id, attachment_type, file_name, content_type,
                    file_size, object_key, file_url, sha256, display_order, created_at
                )
                values (
                    :id, :versionId, :stepId, :attachmentType, :fileName, :contentType,
                    :fileSize, :objectKey, :fileUrl, :sha256, :displayOrder, :createdAt
                )
                """, attachmentParams(attachment));
    }

    public int deleteAttachment(Long id) {
        return jdbc.update("delete from sop_attachment where id = :id", Map.of("id", id));
    }

    public List<SopBinding> listBindings(Long versionId) {
        return jdbc.query("""
                select id, version_id, binding_type, product_id, route_id, route_step_id,
                       process_id, workstation_id, equipment_id, task_id, priority,
                       confirm_mode, effective_from, effective_to, status, created_at, updated_at
                from sop_binding
                where version_id = :versionId
                order by priority desc, id desc
                """, Map.of("versionId", versionId), bindingMapper());
    }

    public Optional<SopBinding> findBindingById(Long id) {
        return findOne("""
                select id, version_id, binding_type, product_id, route_id, route_step_id,
                       process_id, workstation_id, equipment_id, task_id, priority,
                       confirm_mode, effective_from, effective_to, status, created_at, updated_at
                from sop_binding
                where id = :id
                """, Map.of("id", id), bindingMapper());
    }

    public void insertBinding(SopBinding binding) {
        jdbc.update("""
                insert into sop_binding (
                    id, version_id, binding_type, product_id, route_id, route_step_id,
                    process_id, workstation_id, equipment_id, task_id, priority,
                    confirm_mode, effective_from, effective_to, status, created_at, updated_at
                )
                values (
                    :id, :versionId, :bindingType, :productId, :routeId, :routeStepId,
                    :processId, :workstationId, :equipmentId, :taskId, :priority,
                    :confirmMode, :effectiveFrom, :effectiveTo, :status, :createdAt, :updatedAt
                )
                """, bindingParams(binding));
    }

    public int deleteBinding(Long id) {
        return jdbc.update("delete from sop_binding where id = :id", Map.of("id", id));
    }

    public List<Long> listBindingConflictIds(Long versionId) {
        return jdbc.queryForList("""
                select distinct other.id
                from sop_binding b
                join sop_binding other on other.id <> b.id
                  and other.version_id <> b.version_id
                  and other.status = 'ACTIVE'
                  and other.binding_type = b.binding_type
                  and (other.product_id <=> b.product_id)
                  and (other.route_id <=> b.route_id)
                  and (other.route_step_id <=> b.route_step_id)
                  and (other.process_id <=> b.process_id)
                  and (other.workstation_id <=> b.workstation_id)
                  and (other.equipment_id <=> b.equipment_id)
                  and (other.task_id <=> b.task_id)
                  and (other.effective_to is null or b.effective_from is null or other.effective_to >= b.effective_from)
                  and (b.effective_to is null or other.effective_from is null or b.effective_to >= other.effective_from)
                join sop_version ov on ov.id = other.version_id
                where b.version_id = :versionId
                  and b.status = 'ACTIVE'
                  and ov.status in ('EFFECTIVE', 'PENDING_EFFECTIVE')
                """, Map.of("versionId", versionId), Long.class);
    }

    public Optional<SopTaskContext> findTaskContext(Long taskId) {
        return findOne("""
                select st.id as task_id, st.task_no, st.work_order_id, st.product_id,
                       st.route_id, st.line_id, d.station_id as workstation_id, st.status
                from shop_task st
                left join prod_dispatch_order d on d.id = st.dispatch_id
                where st.id = :taskId
                """, Map.of("taskId", taskId), taskContextMapper());
    }

    public List<SopMatchCandidate> listMatchCandidates(SopTaskContext task) {
        MapSqlParameterSource params = params()
                .addValue("taskId", task.taskId())
                .addValue("productId", task.productId())
                .addValue("routeId", task.routeId())
                .addValue("lineId", task.lineId())
                .addValue("workstationId", task.workstationId())
                .addValue("now", LocalDateTime.now());
        return jdbc.query("""
                select d.id as d_id, d.sop_code as d_sop_code, d.sop_name as d_sop_name,
                       d.category as d_category, d.owner_id as d_owner_id, d.status as d_status,
                       d.current_version_id as d_current_version_id, d.created_at as d_created_at,
                       d.updated_at as d_updated_at,
                       v.id as v_id, v.sop_id as v_sop_id, v.version_no as v_version_no,
                       v.revision_type as v_revision_type, v.status as v_status,
                       v.effective_from as v_effective_from, v.effective_to as v_effective_to,
                       v.submit_by as v_submit_by, v.submit_at as v_submit_at,
                       v.review_by as v_review_by, v.review_at as v_review_at,
                       v.approve_by as v_approve_by, v.approve_at as v_approve_at,
                       v.publish_by as v_publish_by, v.publish_at as v_publish_at,
                       v.model_version_id as v_model_version_id, v.remark as v_remark,
                       v.created_at as v_created_at, v.updated_at as v_updated_at,
                       b.id as b_id, b.version_id as b_version_id, b.binding_type as b_binding_type,
                       b.product_id as b_product_id, b.route_id as b_route_id,
                       b.route_step_id as b_route_step_id, b.process_id as b_process_id,
                       b.workstation_id as b_workstation_id, b.equipment_id as b_equipment_id,
                       b.task_id as b_task_id, b.priority as b_priority,
                       b.confirm_mode as b_confirm_mode, b.effective_from as b_effective_from,
                       b.effective_to as b_effective_to, b.status as b_status,
                       b.created_at as b_created_at, b.updated_at as b_updated_at
                from sop_binding b
                join sop_version v on v.id = b.version_id
                join sop_document d on d.id = v.sop_id
                where d.status = 'ENABLED'
                  and v.status = 'EFFECTIVE'
                  and b.status = 'ACTIVE'
                  and (v.effective_from is null or v.effective_from <= :now)
                  and (v.effective_to is null or v.effective_to >= :now)
                  and (b.effective_from is null or b.effective_from <= :now)
                  and (b.effective_to is null or b.effective_to >= :now)
                  and (b.task_id is null or b.task_id = :taskId)
                  and (b.product_id is null or b.product_id = :productId)
                  and (b.route_id is null or b.route_id = :routeId)
                  and (b.workstation_id is null or b.workstation_id in (:workstationId, :lineId))
                order by b.priority desc, v.publish_at desc, v.id desc
                """, params, matchCandidateMapper());
    }

    public void insertModel(SopModel model) {
        jdbc.update("""
                insert into sop_model (
                    id, model_code, model_name, status, created_at, updated_at
                )
                values (
                    :id, :modelCode, :modelName, :status, :createdAt, :updatedAt
                )
                """, params()
                .addValue("id", model.id())
                .addValue("modelCode", model.modelCode())
                .addValue("modelName", model.modelName())
                .addValue("status", model.status())
                .addValue("createdAt", model.createdAt())
                .addValue("updatedAt", model.updatedAt()));
    }

    public void insertModelVersion(SopModelVersion modelVersion) {
        jdbc.update("""
                insert into sop_model_version (
                    id, model_id, version_no, file_name, object_key, file_url,
                    sha256, file_size, status, created_at
                )
                values (
                    :id, :modelId, :versionNo, :fileName, :objectKey, :fileUrl,
                    :sha256, :fileSize, :status, :createdAt
                )
                """, modelVersionParams(modelVersion));
    }

    public Optional<SopModelVersion> findModelVersionById(Long id) {
        return findOne("""
                select id, model_id, version_no, file_name, object_key, file_url,
                       sha256, file_size, status, created_at
                from sop_model_version
                where id = :id
                """, Map.of("id", id), modelVersionMapper());
    }

    public Optional<SopTaskSnapshot> findSnapshotByTaskId(Long taskId) {
        return findOne("""
                select id, task_id, sop_id, version_id, version_no, model_id,
                       model_version_id, model_sha256, snapshot_json, locked_by,
                       locked_at, match_rule, status
                from sop_task_snapshot
                where task_id = :taskId
                """, Map.of("taskId", taskId), snapshotMapper());
    }

    public Optional<SopTaskSnapshot> findSnapshotById(Long snapshotId) {
        return findOne("""
                select id, task_id, sop_id, version_id, version_no, model_id,
                       model_version_id, model_sha256, snapshot_json, locked_by,
                       locked_at, match_rule, status
                from sop_task_snapshot
                where id = :snapshotId
                """, Map.of("snapshotId", snapshotId), snapshotMapper());
    }

    public void insertSnapshot(SopTaskSnapshot snapshot) {
        jdbc.update("""
                insert into sop_task_snapshot (
                    id, task_id, sop_id, version_id, version_no, model_id,
                    model_version_id, model_sha256, snapshot_json, locked_by,
                    locked_at, match_rule, status
                )
                values (
                    :id, :taskId, :sopId, :versionId, :versionNo, :modelId,
                    :modelVersionId, :modelSha256, :snapshotJson, :lockedBy,
                    :lockedAt, :matchRule, :status
                )
                """, snapshotParams(snapshot));
    }

    public int updateSnapshotStatus(Long snapshotId, String status) {
        return jdbc.update("""
                update sop_task_snapshot
                set status = :status
                where id = :snapshotId
                """, Map.of("snapshotId", snapshotId, "status", status));
    }

    public Optional<SopExecutionRecord> findExecutionBySnapshotId(Long snapshotId) {
        return findOne("""
                select id, snapshot_id, task_id, sop_id, version_id, operator_id,
                       opened_at, completed_at, status
                from sop_execution_record
                where snapshot_id = :snapshotId
                """, Map.of("snapshotId", snapshotId), executionMapper());
    }

    public void insertExecution(SopExecutionRecord execution) {
        jdbc.update("""
                insert into sop_execution_record (
                    id, snapshot_id, task_id, sop_id, version_id, operator_id,
                    opened_at, completed_at, status
                )
                values (
                    :id, :snapshotId, :taskId, :sopId, :versionId, :operatorId,
                    :openedAt, :completedAt, :status
                )
                """, executionParams(execution));
    }

    public int completeExecution(Long executionId) {
        return jdbc.update("""
                update sop_execution_record
                set completed_at = :completedAt,
                    status = 'COMPLETED'
                where id = :executionId
                """, Map.of("executionId", executionId, "completedAt", LocalDateTime.now()));
    }

    public List<SopStepExecutionRecord> listStepRecordsBySnapshot(Long snapshotId) {
        return jdbc.query("""
                select id, execution_id, snapshot_id, task_id, step_id, step_no,
                       view_started_at, confirmed_at, stay_seconds, parameter_json,
                       photo_attachment_id, result, operator_id, idempotency_key
                from sop_step_execution_record
                where snapshot_id = :snapshotId
                order by step_no asc, id asc
                """, Map.of("snapshotId", snapshotId), stepRecordMapper());
    }

    public Optional<SopStepExecutionRecord> findStepRecord(Long snapshotId, Long stepId) {
        return findOne("""
                select id, execution_id, snapshot_id, task_id, step_id, step_no,
                       view_started_at, confirmed_at, stay_seconds, parameter_json,
                       photo_attachment_id, result, operator_id, idempotency_key
                from sop_step_execution_record
                where snapshot_id = :snapshotId and step_id = :stepId
                """, Map.of("snapshotId", snapshotId, "stepId", stepId), stepRecordMapper());
    }

    public void upsertStepView(SopStepExecutionRecord record) {
        jdbc.update("""
                insert into sop_step_execution_record (
                    id, execution_id, snapshot_id, task_id, step_id, step_no,
                    view_started_at, confirmed_at, stay_seconds, parameter_json,
                    photo_attachment_id, result, operator_id, idempotency_key
                )
                values (
                    :id, :executionId, :snapshotId, :taskId, :stepId, :stepNo,
                    :viewStartedAt, :confirmedAt, :staySeconds, :parameterJson,
                    :photoAttachmentId, :result, :operatorId, :idempotencyKey
                )
                on duplicate key update
                    view_started_at = coalesce(view_started_at, values(view_started_at)),
                    operator_id = values(operator_id)
                """, stepRecordParams(record));
    }

    public int confirmStep(SopStepExecutionRecord record) {
        return jdbc.update("""
                update sop_step_execution_record
                set confirmed_at = :confirmedAt,
                    stay_seconds = :staySeconds,
                    parameter_json = :parameterJson,
                    photo_attachment_id = :photoAttachmentId,
                    result = :result,
                    operator_id = :operatorId,
                    idempotency_key = :idempotencyKey
                where snapshot_id = :snapshotId and step_id = :stepId
                """, stepRecordParams(record));
    }

    public int countPendingRequiredSteps(Long snapshotId, Long versionId) {
        Integer count = jdbc.queryForObject("""
                select count(*)
                from sop_step s
                left join sop_step_execution_record r
                  on r.snapshot_id = :snapshotId
                 and r.step_id = s.id
                 and r.confirmed_at is not null
                where s.version_id = :versionId
                  and s.enabled = 1
                  and (s.confirm_required = 1 or s.key_step = 1)
                  and r.id is null
                """, Map.of("snapshotId", snapshotId, "versionId", versionId), Integer.class);
        return count == null ? 0 : count;
    }

    public List<SopExecutionRecord> listExecutions(Long taskId, Long versionId) {
        StringBuilder sql = new StringBuilder("""
                select id, snapshot_id, task_id, sop_id, version_id, operator_id,
                       opened_at, completed_at, status
                from sop_execution_record
                where 1 = 1
                """);
        MapSqlParameterSource params = params();
        appendEquals(sql, params, "task_id", taskId);
        appendEquals(sql, params, "version_id", versionId);
        sql.append(" order by opened_at desc, id desc");
        return jdbc.query(sql.toString(), params, executionMapper());
    }

    private MapSqlParameterSource documentParams(SopDocument document) {
        return params()
                .addValue("id", document.id())
                .addValue("sopCode", document.sopCode())
                .addValue("sopName", document.sopName())
                .addValue("category", document.category())
                .addValue("ownerId", document.ownerId())
                .addValue("status", document.status())
                .addValue("currentVersionId", document.currentVersionId())
                .addValue("createdAt", document.createdAt())
                .addValue("updatedAt", document.updatedAt());
    }

    private MapSqlParameterSource versionParams(SopVersion version) {
        return params()
                .addValue("id", version.id())
                .addValue("sopId", version.sopId())
                .addValue("versionNo", version.versionNo())
                .addValue("revisionType", version.revisionType())
                .addValue("status", version.status())
                .addValue("effectiveFrom", version.effectiveFrom())
                .addValue("effectiveTo", version.effectiveTo())
                .addValue("submitBy", version.submitBy())
                .addValue("submitAt", version.submitAt())
                .addValue("reviewBy", version.reviewBy())
                .addValue("reviewAt", version.reviewAt())
                .addValue("approveBy", version.approveBy())
                .addValue("approveAt", version.approveAt())
                .addValue("publishBy", version.publishBy())
                .addValue("publishAt", version.publishAt())
                .addValue("modelVersionId", version.modelVersionId())
                .addValue("remark", version.remark())
                .addValue("createdAt", version.createdAt())
                .addValue("updatedAt", version.updatedAt());
    }

    private MapSqlParameterSource stepParams(SopStep step) {
        return params()
                .addValue("id", step.id())
                .addValue("versionId", step.versionId())
                .addValue("stepNo", step.stepNo())
                .addValue("stepName", step.stepName())
                .addValue("instruction", step.instruction())
                .addValue("contentType", step.contentType())
                .addValue("standardDuration", step.standardDuration())
                .addValue("keyStep", step.keyStep())
                .addValue("minStaySeconds", step.minStaySeconds())
                .addValue("confirmRequired", step.confirmRequired())
                .addValue("parameterRequired", step.parameterRequired())
                .addValue("photoRequired", step.photoRequired())
                .addValue("skipAllowed", step.skipAllowed())
                .addValue("abnormalHandling", step.abnormalHandling())
                .addValue("qualityItemId", step.qualityItemId())
                .addValue("andonTypeId", step.andonTypeId())
                .addValue("enabled", step.enabled())
                .addValue("createdAt", step.createdAt())
                .addValue("updatedAt", step.updatedAt());
    }

    private MapSqlParameterSource attachmentParams(SopAttachment attachment) {
        return params()
                .addValue("id", attachment.id())
                .addValue("versionId", attachment.versionId())
                .addValue("stepId", attachment.stepId())
                .addValue("attachmentType", attachment.attachmentType())
                .addValue("fileName", attachment.fileName())
                .addValue("contentType", attachment.contentType())
                .addValue("fileSize", attachment.fileSize())
                .addValue("objectKey", attachment.objectKey())
                .addValue("fileUrl", attachment.fileUrl())
                .addValue("sha256", attachment.sha256())
                .addValue("displayOrder", attachment.displayOrder())
                .addValue("createdAt", attachment.createdAt());
    }

    private MapSqlParameterSource bindingParams(SopBinding binding) {
        return params()
                .addValue("id", binding.id())
                .addValue("versionId", binding.versionId())
                .addValue("bindingType", binding.bindingType())
                .addValue("productId", binding.productId())
                .addValue("routeId", binding.routeId())
                .addValue("routeStepId", binding.routeStepId())
                .addValue("processId", binding.processId())
                .addValue("workstationId", binding.workstationId())
                .addValue("equipmentId", binding.equipmentId())
                .addValue("taskId", binding.taskId())
                .addValue("priority", binding.priority())
                .addValue("confirmMode", binding.confirmMode())
                .addValue("effectiveFrom", binding.effectiveFrom())
                .addValue("effectiveTo", binding.effectiveTo())
                .addValue("status", binding.status())
                .addValue("createdAt", binding.createdAt())
                .addValue("updatedAt", binding.updatedAt());
    }

    private MapSqlParameterSource modelVersionParams(SopModelVersion modelVersion) {
        return params()
                .addValue("id", modelVersion.id())
                .addValue("modelId", modelVersion.modelId())
                .addValue("versionNo", modelVersion.versionNo())
                .addValue("fileName", modelVersion.fileName())
                .addValue("objectKey", modelVersion.objectKey())
                .addValue("fileUrl", modelVersion.fileUrl())
                .addValue("sha256", modelVersion.sha256())
                .addValue("fileSize", modelVersion.fileSize())
                .addValue("status", modelVersion.status())
                .addValue("createdAt", modelVersion.createdAt());
    }

    private MapSqlParameterSource snapshotParams(SopTaskSnapshot snapshot) {
        return params()
                .addValue("id", snapshot.id())
                .addValue("taskId", snapshot.taskId())
                .addValue("sopId", snapshot.sopId())
                .addValue("versionId", snapshot.versionId())
                .addValue("versionNo", snapshot.versionNo())
                .addValue("modelId", snapshot.modelId())
                .addValue("modelVersionId", snapshot.modelVersionId())
                .addValue("modelSha256", snapshot.modelSha256())
                .addValue("snapshotJson", snapshot.snapshotJson())
                .addValue("lockedBy", snapshot.lockedBy())
                .addValue("lockedAt", snapshot.lockedAt())
                .addValue("matchRule", snapshot.matchRule())
                .addValue("status", snapshot.status());
    }

    private MapSqlParameterSource executionParams(SopExecutionRecord execution) {
        return params()
                .addValue("id", execution.id())
                .addValue("snapshotId", execution.snapshotId())
                .addValue("taskId", execution.taskId())
                .addValue("sopId", execution.sopId())
                .addValue("versionId", execution.versionId())
                .addValue("operatorId", execution.operatorId())
                .addValue("openedAt", execution.openedAt())
                .addValue("completedAt", execution.completedAt())
                .addValue("status", execution.status());
    }

    private MapSqlParameterSource stepRecordParams(SopStepExecutionRecord record) {
        return params()
                .addValue("id", record.id())
                .addValue("executionId", record.executionId())
                .addValue("snapshotId", record.snapshotId())
                .addValue("taskId", record.taskId())
                .addValue("stepId", record.stepId())
                .addValue("stepNo", record.stepNo())
                .addValue("viewStartedAt", record.viewStartedAt())
                .addValue("confirmedAt", record.confirmedAt())
                .addValue("staySeconds", record.staySeconds())
                .addValue("parameterJson", record.parameterJson())
                .addValue("photoAttachmentId", record.photoAttachmentId())
                .addValue("result", record.result())
                .addValue("operatorId", record.operatorId())
                .addValue("idempotencyKey", record.idempotencyKey());
    }

    private RowMapper<SopDocument> documentMapper() {
        return (rs, rowNum) -> new SopDocument(
                rs.getLong("id"),
                rs.getString("sop_code"),
                rs.getString("sop_name"),
                rs.getString("category"),
                getLong(rs, "owner_id"),
                rs.getString("status"),
                getLong(rs, "current_version_id"),
                getDateTime(rs, "created_at"),
                getDateTime(rs, "updated_at")
        );
    }

    private RowMapper<SopVersion> versionMapper() {
        return (rs, rowNum) -> versionFrom(rs, "");
    }

    private RowMapper<SopStep> stepMapper() {
        return (rs, rowNum) -> new SopStep(
                rs.getLong("id"),
                getLong(rs, "version_id"),
                getInteger(rs, "step_no"),
                rs.getString("step_name"),
                rs.getString("instruction"),
                rs.getString("content_type"),
                getInteger(rs, "standard_duration"),
                getBoolean(rs, "key_step"),
                getInteger(rs, "min_stay_seconds"),
                getBoolean(rs, "confirm_required"),
                getBoolean(rs, "parameter_required"),
                getBoolean(rs, "photo_required"),
                getBoolean(rs, "skip_allowed"),
                rs.getString("abnormal_handling"),
                getLong(rs, "quality_item_id"),
                getLong(rs, "andon_type_id"),
                getBoolean(rs, "enabled"),
                getDateTime(rs, "created_at"),
                getDateTime(rs, "updated_at")
        );
    }

    private RowMapper<SopAttachment> attachmentMapper() {
        return (rs, rowNum) -> new SopAttachment(
                rs.getLong("id"),
                getLong(rs, "version_id"),
                getLong(rs, "step_id"),
                rs.getString("attachment_type"),
                rs.getString("file_name"),
                rs.getString("content_type"),
                getLong(rs, "file_size"),
                rs.getString("object_key"),
                rs.getString("file_url"),
                rs.getString("sha256"),
                getInteger(rs, "display_order"),
                getDateTime(rs, "created_at")
        );
    }

    private RowMapper<SopBinding> bindingMapper() {
        return (rs, rowNum) -> bindingFrom(rs, "");
    }

    private RowMapper<SopTaskContext> taskContextMapper() {
        return (rs, rowNum) -> new SopTaskContext(
                getLong(rs, "task_id"),
                rs.getString("task_no"),
                getLong(rs, "work_order_id"),
                getLong(rs, "product_id"),
                getLong(rs, "route_id"),
                getLong(rs, "line_id"),
                getLong(rs, "workstation_id"),
                rs.getString("status")
        );
    }

    private RowMapper<SopMatchCandidate> matchCandidateMapper() {
        return (rs, rowNum) -> new SopMatchCandidate(
                new SopDocument(
                        getLong(rs, "d_id"),
                        rs.getString("d_sop_code"),
                        rs.getString("d_sop_name"),
                        rs.getString("d_category"),
                        getLong(rs, "d_owner_id"),
                        rs.getString("d_status"),
                        getLong(rs, "d_current_version_id"),
                        getDateTime(rs, "d_created_at"),
                        getDateTime(rs, "d_updated_at")
                ),
                versionFrom(rs, "v_"),
                bindingFrom(rs, "b_")
        );
    }

    private RowMapper<SopModelVersion> modelVersionMapper() {
        return (rs, rowNum) -> new SopModelVersion(
                rs.getLong("id"),
                getLong(rs, "model_id"),
                rs.getString("version_no"),
                rs.getString("file_name"),
                rs.getString("object_key"),
                rs.getString("file_url"),
                rs.getString("sha256"),
                getLong(rs, "file_size"),
                rs.getString("status"),
                getDateTime(rs, "created_at")
        );
    }

    private RowMapper<SopTaskSnapshot> snapshotMapper() {
        return (rs, rowNum) -> new SopTaskSnapshot(
                rs.getLong("id"),
                getLong(rs, "task_id"),
                getLong(rs, "sop_id"),
                getLong(rs, "version_id"),
                rs.getString("version_no"),
                getLong(rs, "model_id"),
                getLong(rs, "model_version_id"),
                rs.getString("model_sha256"),
                rs.getString("snapshot_json"),
                getLong(rs, "locked_by"),
                getDateTime(rs, "locked_at"),
                rs.getString("match_rule"),
                rs.getString("status")
        );
    }

    private RowMapper<SopExecutionRecord> executionMapper() {
        return (rs, rowNum) -> new SopExecutionRecord(
                rs.getLong("id"),
                getLong(rs, "snapshot_id"),
                getLong(rs, "task_id"),
                getLong(rs, "sop_id"),
                getLong(rs, "version_id"),
                getLong(rs, "operator_id"),
                getDateTime(rs, "opened_at"),
                getDateTime(rs, "completed_at"),
                rs.getString("status")
        );
    }

    private RowMapper<SopStepExecutionRecord> stepRecordMapper() {
        return (rs, rowNum) -> new SopStepExecutionRecord(
                rs.getLong("id"),
                getLong(rs, "execution_id"),
                getLong(rs, "snapshot_id"),
                getLong(rs, "task_id"),
                getLong(rs, "step_id"),
                getInteger(rs, "step_no"),
                getDateTime(rs, "view_started_at"),
                getDateTime(rs, "confirmed_at"),
                getInteger(rs, "stay_seconds"),
                rs.getString("parameter_json"),
                getLong(rs, "photo_attachment_id"),
                rs.getString("result"),
                getLong(rs, "operator_id"),
                rs.getString("idempotency_key")
        );
    }

    private SopVersion versionFrom(ResultSet rs, String prefix) throws SQLException {
        return new SopVersion(
                getLong(rs, prefix + "id"),
                getLong(rs, prefix + "sop_id"),
                rs.getString(prefix + "version_no"),
                rs.getString(prefix + "revision_type"),
                rs.getString(prefix + "status"),
                getDateTime(rs, prefix + "effective_from"),
                getDateTime(rs, prefix + "effective_to"),
                getLong(rs, prefix + "submit_by"),
                getDateTime(rs, prefix + "submit_at"),
                getLong(rs, prefix + "review_by"),
                getDateTime(rs, prefix + "review_at"),
                getLong(rs, prefix + "approve_by"),
                getDateTime(rs, prefix + "approve_at"),
                getLong(rs, prefix + "publish_by"),
                getDateTime(rs, prefix + "publish_at"),
                getLong(rs, prefix + "model_version_id"),
                rs.getString(prefix + "remark"),
                getDateTime(rs, prefix + "created_at"),
                getDateTime(rs, prefix + "updated_at")
        );
    }

    private SopBinding bindingFrom(ResultSet rs, String prefix) throws SQLException {
        return new SopBinding(
                getLong(rs, prefix + "id"),
                getLong(rs, prefix + "version_id"),
                rs.getString(prefix + "binding_type"),
                getLong(rs, prefix + "product_id"),
                getLong(rs, prefix + "route_id"),
                getLong(rs, prefix + "route_step_id"),
                getLong(rs, prefix + "process_id"),
                getLong(rs, prefix + "workstation_id"),
                getLong(rs, prefix + "equipment_id"),
                getLong(rs, prefix + "task_id"),
                getInteger(rs, prefix + "priority"),
                rs.getString(prefix + "confirm_mode"),
                getDateTime(rs, prefix + "effective_from"),
                getDateTime(rs, prefix + "effective_to"),
                rs.getString(prefix + "status"),
                getDateTime(rs, prefix + "created_at"),
                getDateTime(rs, prefix + "updated_at")
        );
    }

    private void appendKeyword(
            StringBuilder sql,
            MapSqlParameterSource params,
            String keyword,
            String firstColumn,
            String... otherColumns
    ) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        sql.append(" and (").append(firstColumn).append(" like :keyword");
        for (String column : otherColumns) {
            sql.append(" or ").append(column).append(" like :keyword");
        }
        sql.append(")");
        params.addValue("keyword", "%" + keyword.trim() + "%");
    }

    private void appendEquals(StringBuilder sql, MapSqlParameterSource params, String column, Object value) {
        if (value == null || value instanceof String text && !StringUtils.hasText(text)) {
            return;
        }
        String paramName = column.replace("_", "");
        sql.append(" and ").append(column).append(" = :").append(paramName);
        params.addValue(paramName, value instanceof String text ? text.trim() : value);
    }

    private MapSqlParameterSource params() {
        return new MapSqlParameterSource();
    }

    private <T> Optional<T> findOne(String sql, Map<String, ?> params, RowMapper<T> mapper) {
        List<T> rows = jdbc.query(sql, params, mapper);
        return rows.stream().findFirst();
    }

    private Long getLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private Integer getInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private Boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
        boolean value = rs.getBoolean(columnName);
        return rs.wasNull() ? null : value;
    }

    private LocalDateTime getDateTime(ResultSet rs, String columnName) throws SQLException {
        return rs.getTimestamp(columnName) == null ? null : rs.getTimestamp(columnName).toLocalDateTime();
    }
}
