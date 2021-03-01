package com.exam.kakaopay.constants;

public enum Codes {

    S0000("0000", "정상 처리"),
    E0010("0010", "호출 값 이상[에러코드010]"),
    E0020("0020", "잘못된 요청[에러코드020]"),
    E0030("0030", "뿌린 사람만 조회를 할 수 있습니다.[에러코드030]"),
    E0040("0040", "찾을 수 없음[에러코드040]");

    public final String code;
    public final String description;

    Codes(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
