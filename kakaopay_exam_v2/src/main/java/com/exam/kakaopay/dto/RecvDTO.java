package com.exam.kakaopay.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class RecvDTO {
    private final long userId;
    //private final long roomId;
    private final long amount;
}
