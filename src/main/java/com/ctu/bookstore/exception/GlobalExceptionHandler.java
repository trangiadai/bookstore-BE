package com.ctu.bookstore.exception;

import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {
    //bắt tất cả các exception còn lại
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponeDTO> handlingException(Exception exception){
        ApiResponeDTO apiResponeDTO = new ApiResponeDTO();
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        apiResponeDTO.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponeDTO.setMessage(exception.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(apiResponeDTO);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponeDTO> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponeDTO.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponeDTO> handlingAppException(AppException exception){
        ApiResponeDTO apiResponeDTO = new ApiResponeDTO();
        apiResponeDTO.setCode(exception.getErrorCode().getCode());
        apiResponeDTO.setMessage(exception.getErrorCode().getMessage());
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponeDTO.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponeDTO> handlingValidation(MethodArgumentNotValidException exception){
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        String enumKey = exception.getFieldError().getDefaultMessage();
        try {
             errorCode = ErrorCode.valueOf(enumKey);
        }catch (IllegalArgumentException e){

        }
        ApiResponeDTO apiResponeDTO = new ApiResponeDTO();
        apiResponeDTO.setCode(errorCode.getCode());
        apiResponeDTO.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(apiResponeDTO);
    }
}
