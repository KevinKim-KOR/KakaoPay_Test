package com.exam.kakaopay.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.AccessLevel;
import com.exam.kakaopay.constants.Codes;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class ApiResponse {
    @NonNull
    private String code;
    @NonNull
    private String message;
    @Setter
    private Object body;

    public static ApiResponse of(String code, String message) {
        return new ApiResponse(code, message);
    }

    public static ApiResponse of(Codes code) {
        return new ApiResponse(code.code, code.description);
    }

    public static ApiResponse of(Codes code, Object body) {
        ApiResponse response = of(code);
        response.setBody(body);
        return response;
    }

}
