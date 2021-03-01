package com.exam.kakaopay.exception;

public class SelfRecvException extends ValidationException {

    public SelfRecvException() {
        super("자신이 뿌리기한 건은 자신이 받을 수 없습니다.");
    }
}
