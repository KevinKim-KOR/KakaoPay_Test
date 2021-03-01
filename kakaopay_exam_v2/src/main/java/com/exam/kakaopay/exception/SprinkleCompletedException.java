package com.exam.kakaopay.exception;

public class SprinkleCompletedException extends ValidationException {

    public SprinkleCompletedException() {
        super("종료된 뿌리기 입니다.");
    }

}
