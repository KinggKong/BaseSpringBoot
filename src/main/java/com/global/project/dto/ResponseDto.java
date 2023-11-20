package com.global.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDto {
    private int code;
    private String message;
    private Object data;

    public static ResponseDto of(int code, String message,Object data) {
        return ResponseDto.builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static ResponseDto ofSuccess(Object data) {
        return ResponseDto.builder()
                .code(200)
                .data(data)
                .message("success")
                .build();
    }

    public static ResponseDto ofError(int code, String message) {
        return ResponseDto.builder()
                .code(code)
                .message(message)
                .build();
    }
}
