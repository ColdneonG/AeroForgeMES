package com.fanmes.production.service;

import com.fanmes.common.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BarcodeExpressionEngine {
    private static final Pattern SERIAL_TOKEN = Pattern.compile("\\$\\{(#+)}");
    private static final Pattern UNRESOLVED_TOKEN = Pattern.compile("\\$\\{[^}]+}");

    public boolean requiresSequence(String expression) {
        return StringUtils.hasText(expression)
                && (SERIAL_TOKEN.matcher(expression).find() || expression.contains("${serial}"));
    }

    public String render(
            String expression,
            LocalDate date,
            long sequence,
            int serialLength,
            Map<String, String> context
    ) {
        if (!StringUtils.hasText(expression)) {
            throw new BadRequestException("barcode rule expression is required");
        }
        LocalDate effectiveDate = date == null ? LocalDate.now() : date;
        String value = expression
                .replace("${yyyyMMdd}", effectiveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .replace("${yyyyMM}", effectiveDate.format(DateTimeFormatter.ofPattern("yyyyMM")))
                .replace("${yyMMdd}", effectiveDate.format(DateTimeFormatter.ofPattern("yyMMdd")))
                .replace("${yyyy}", effectiveDate.format(DateTimeFormatter.ofPattern("yyyy")));

        for (Map.Entry<String, String> entry : context.entrySet()) {
            if (entry.getValue() != null) {
                value = value.replace("${" + entry.getKey() + "}", entry.getValue());
            }
        }

        Matcher matcher = SERIAL_TOKEN.matcher(value);
        StringBuffer rendered = new StringBuffer();
        while (matcher.find()) {
            String serial = pad(sequence, matcher.group(1).length());
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(serial));
        }
        matcher.appendTail(rendered);
        value = rendered.toString().replace("${serial}", pad(sequence, serialLength));

        Matcher unresolved = UNRESOLVED_TOKEN.matcher(value);
        if (unresolved.find()) {
            throw new BadRequestException("unresolved barcode token: " + unresolved.group());
        }
        if (value.length() > 128) {
            throw new BadRequestException("generated barcode exceeds 128 characters");
        }
        return value;
    }

    private String pad(long sequence, int length) {
        String value = Long.toString(sequence);
        if (value.length() > length) {
            throw new BadRequestException("barcode sequence exceeds configured serial length");
        }
        return "0".repeat(Math.max(0, length - value.length())) + value;
    }
}
