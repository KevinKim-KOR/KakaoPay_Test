package com.exam.kakaopay.exception;

public class SprinkleNotFoundException extends NotFoundException {

    public SprinkleNotFoundException() {
        super("뿌리기를 찾을 수 없습니다.");
    }
}
