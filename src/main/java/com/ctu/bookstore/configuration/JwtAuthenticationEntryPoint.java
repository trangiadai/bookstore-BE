package com.ctu.bookstore.configuration;

import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Dùng để sử lý Exception UNAUTHORIZED vì nó xảy ra ở filter chứ không xuống tới đc hệ thống
    // nên ta phải có cách sử lý riêng chứ không xử lý chung trong GlobalException đc
    // commence chỉ xảy ra khi trong quá trình Authentication có một Exception xảy ra dẫn đến Authentication không thành công
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponseDTO<?> apiResponseDTO = ApiResponseDTO.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponseDTO));

        response.flushBuffer();
    }
}
