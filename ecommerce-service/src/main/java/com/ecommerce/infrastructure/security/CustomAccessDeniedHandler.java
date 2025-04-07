package com.ecommerce.infrastructure.security;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAcessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {}

     response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

    Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", 403);
        responseBody.put("error", "Access Denied");
        responseBody.put("message", "Você não tem permissão para acessar este recurso.");
        responseBody.put("path", request.getRequestURI());

    ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), responseBody);
}
