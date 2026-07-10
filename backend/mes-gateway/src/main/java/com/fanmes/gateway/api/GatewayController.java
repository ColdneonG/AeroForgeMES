package com.fanmes.gateway.api;

import com.fanmes.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
public class GatewayController {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private final Map<String, String> routes;
    private final Map<String, String> legacyRoutes;

    public GatewayController(
            @Value("${fanmes.gateway.routes.auth:http://localhost:8081}") String authUrl,
            @Value("${fanmes.gateway.routes.system:http://localhost:8082}") String systemUrl,
            @Value("${fanmes.gateway.routes.equipment:http://localhost:8083}") String equipmentUrl,
            @Value("${fanmes.gateway.routes.report:http://localhost:8086}") String reportUrl,
            @Value("${fanmes.gateway.routes.integration:http://localhost:8087}") String integrationUrl,
            @Value("${fanmes.gateway.routes.production:http://localhost:9103}") String productionUrl,
            @Value("${fanmes.gateway.routes.quality:http://localhost:9104}") String qualityUrl
    ) {
        this.routes = Map.of(
                "/api/auth/", trimUrl(authUrl),
                "/api/system/", trimUrl(systemUrl),
                "/api/equipment/", trimUrl(equipmentUrl),
                "/api/report/", trimUrl(reportUrl),
                "/api/integration/", trimUrl(integrationUrl),
                "/api/openapi/", trimUrl(integrationUrl),
                "/api/production/", trimUrl(productionUrl),
                "/api/quality/", trimUrl(qualityUrl),
                "/api/andon/", trimUrl(productionUrl),
                "/internal/", trimUrl(productionUrl)
        );
        this.legacyRoutes = Map.ofEntries(
                Map.entry("func-20", trimUrl(equipmentUrl)),
                Map.entry("func-21", trimUrl(equipmentUrl)),
                Map.entry("func-22", trimUrl(equipmentUrl)),
                Map.entry("func-23", trimUrl(equipmentUrl)),
                Map.entry("func-24", trimUrl(equipmentUrl)),
                Map.entry("func-25", trimUrl(equipmentUrl)),
                Map.entry("func-26", trimUrl(equipmentUrl)),
                Map.entry("func-27", trimUrl(equipmentUrl)),
                Map.entry("oee", trimUrl(equipmentUrl)),
                Map.entry("func-29", trimUrl(equipmentUrl)),
                Map.entry("func-30", trimUrl(equipmentUrl)),
                Map.entry("func-59", trimUrl(integrationUrl)),
                Map.entry("func-60", trimUrl(integrationUrl)),
                Map.entry("func-61", trimUrl(integrationUrl)),
                Map.entry("func-62", trimUrl(integrationUrl)),
                Map.entry("func-63", trimUrl(integrationUrl)),
                Map.entry("func-64", trimUrl(integrationUrl)),
                Map.entry("func-65", trimUrl(integrationUrl))
        );
    }

    @GetMapping("/api/gateway/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of("service", "mes-gateway", "status", "UP"));
    }

    @RequestMapping("/api/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException, InterruptedException {
        String requestUri = request.getRequestURI();
        String targetBase = route(requestUri);
        String query = request.getQueryString();
        URI target = URI.create(targetBase + requestUri + (query == null ? "" : "?" + query));

        byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
        HttpRequest.Builder builder = HttpRequest.newBuilder(target)
                .timeout(Duration.ofSeconds(20));
        copyRequestHeaders(request, builder);
        builder.method(request.getMethod(), body.length == 0
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body));

        HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        HttpHeaders headers = new HttpHeaders();
        response.headers().map().forEach((name, values) -> {
            if (!hopByHopHeader(name)) {
                headers.put(name, values);
            }
        });
        return new ResponseEntity<>(response.body(), headers, HttpStatus.valueOf(response.statusCode()));
    }

    private String route(String path) {
        // exact prefix match first
        for (Map.Entry<String, String> entry : routes.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        // legacy /api/mes/{func} routes
        if (path.startsWith("/api/mes/")) {
            String func = path.substring("/api/mes/".length()).split("/")[0];
            String target = legacyRoutes.get(func);
            if (target != null) {
                return target;
            }
        }
        throw new IllegalArgumentException("no gateway route for path: " + path);
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpRequest.Builder builder) {
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            if (!hopByHopHeader(name)) {
                request.getHeaders(name).asIterator().forEachRemaining(value -> builder.header(name, value));
            }
        });
        if (request.getContentType() == null) {
            builder.header("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8.name());
        }
    }

    private boolean hopByHopHeader(String name) {
        return List.of("connection", "host", "content-length", "transfer-encoding", "upgrade").contains(name.toLowerCase());
    }

    private String trimUrl(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
