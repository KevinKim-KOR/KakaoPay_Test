package com.exam.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class SprinkleDTO {
    /**
     * 뿌린 시각
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss")
    private final LocalDateTime datetime;
    /**
     * 뿌린 금액
     */
    private final long totalAmount;
    /**
     * 받기 완료된 금액
     */
    private final long recvAmount;
    /**
     * 받기 완료된 정보
     */
    private final List<RecvDTO> recvDTOList;
}
