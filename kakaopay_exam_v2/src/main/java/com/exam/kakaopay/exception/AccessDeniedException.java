package com.exam.kakaopay.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("뿌린 사람만 조회할 수 있습니다.");
    }
}
