package com.exam.kakaopay.service;

import com.exam.kakaopay.jpa.entity.Sprinkle;

import java.util.List;

public interface SprinkleService {

    /**
     * 뿌리기
     *
     * @param roomId 대화방 식별값
     * @param userId 사용자 식별값
     * @param amount 뿌릴 금액
     * @param count  받을 인원수
     * @return {@link Sprinkle}
     */
    Sprinkle sprinkle(String roomId, long userId, long amount, int count);

    /**
     * 줍기 (받기)
     *
     * @param roomId 대화방 식별값
     * @param userId 사용자 식별값
     * @param token  뿌리기 토큰
     * @return 받은 금액
     */
    long recvAmount(String roomId, long userId, String token);

    /**
     * 조회
     *
     * @param userId 사용자 식별값
     * @param token  뿌리기 토큰
     * @return {@link Sprinkle} 뿌리기 엔티티
     */
    Sprinkle retreiveSprinkleList(long userId, String token);

    /**
     * 조회
     *
     * @param userId 사용자 식별값
     * @param token  뿌리기 토큰
     * @return {@link Sprinkle} 뿌리기 엔티티
     */
    List<Sprinkle> retreiveSprinkleList2(long userId, String token);
}
