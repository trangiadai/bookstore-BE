package com.ctu.bookstore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // để ẩn các trường có giá trị null của json
public class ApiResponseDTO<T> {
    @Builder.Default
    private int code = 1000;
    private String message;
    private T result;
}
