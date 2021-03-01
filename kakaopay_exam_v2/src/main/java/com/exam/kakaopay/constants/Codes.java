package com.exam.kakaopay.constants;

public enum Codes {

    S0000("0000", "정상 처리"),
    E0100("0100", "호출 값 이상[에러코드100]"),
    E0200("0200", "잘못된 요청[에러코드200]"),
    E0300("0300", "뿌린 사람만 조회를 할 수 있습니다.[에러코드300]"),
    E0400("0400", "찾을 수 없음[에러코드400]"),
    E0500("0500", "뿌린 건에 대해서는 7일동안 조회할 수 있습니다[에러코드500]");

    public final String code;
    public final String description;

    Codes(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
