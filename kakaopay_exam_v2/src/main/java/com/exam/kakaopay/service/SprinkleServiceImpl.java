package com.exam.kakaopay.service;

import com.exam.kakaopay.exception.*;
import com.exam.kakaopay.jpa.entity.Recv;
import com.exam.kakaopay.jpa.entity.Sprinkle;
import com.exam.kakaopay.jpa.repository.SprinkleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.exam.kakaopay.feature.TokenGenerator;
import com.exam.kakaopay.jpa.repository.RecvRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SprinkleServiceImpl implements SprinkleService {
    private final TokenGenerator tokenGenerator;
    private final SprinkleRepository sprinkleRepository;
    private final RecvRepository recvRepository;

    @Transactional
    @Override
    /**
     * 금액 뿌리기
     *
     * @param roomId 대화방 식별값
     * @param userId 사용자 식별값
     * @param amount 뿌릴 금액
     * @param count  받을 인원수
     * @return {@link Sprinkle} 뿌리기 엔티티
     */
    public Sprinkle sprinkle(String roomId, long userId, long amount, int count) {
        String token = tokenGenerator.generate();
        Sprinkle sprinkle = new Sprinkle(token, roomId, userId, amount, count);
        sprinkleRepository.save(sprinkle);
        long[] divide = divide(sprinkle.getAmount(), sprinkle.getCount());
        for (int i = 0; i < divide.length; i++) {
            Recv recv = new Recv(sprinkle, i + 1, divide[i]);
            recvRepository.save(recv);
        }
        return sprinkle;
    }

    @Transactional
    @Override
    public long recvAmount(String roomId, long userId, String token) {
        Sprinkle sprinkle = sprinkleRepository.findByToken(token);

        if (Objects.isNull(sprinkle)) { // 조회된 뿌리기 없음
            throw new SprinkleNotFoundException();
        }
        if (sprinkle.isExpired(10)) { // 만료됨 (종료됨)
            throw new SprinkleCompletedException();
        }
        if (!sprinkle.getRoomId().equals(roomId)) { // 다른 대화방 사용자
            throw new NotBelongRoomException();
        }
        if (sprinkle.getUserId() == userId) { // 뿌린 사용자가 주울려고 함
            throw new SelfRecvException();
        }
        if (sprinkle.getRecvs().stream().noneMatch(Recv::isNotReceived)) { // 모두 주워감
            throw new SprinkleCompletedException();
        }

        if (sprinkle.getRecvs().stream()
                .filter(Recv::isReceived)
                .anyMatch(it -> it.getUserId() == userId)) { // 한번만 받을 수 있다.
            throw new DuplicatedPickupException();
        }

        List<Recv> notPickedUpYet = sprinkle.getRecvs().stream() // 아직 주워가지 않은 건만 필터
                .filter(Recv::isNotReceived)
                .collect(Collectors.toList());
        // 첫번째 받기
        Recv recv = notPickedUpYet.get(0);
        // 주웠다!
        recv.recvAmt(userId);
        // 주운 금액 리턴
        return recv.getAmount();
    }

    @Transactional(readOnly = true)
    @Override
    public Sprinkle retreiveSprinkleList(long userId, String token) {
        LocalDateTime createdAt = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusDays(7);

        Sprinkle sprinkle = sprinkleRepository.findByTokenAndCreatedAtGreaterThan(token, createdAt);
        log.debug("sprinkle = {}", sprinkle);
        if (Objects.isNull(sprinkle)) {
            throw new SprinkleNotFoundException();
        }
        if (sprinkle.getUserId() != userId) {
            throw new AccessDeniedException();
        }

        return sprinkle;
    }

    /**
     * 금액을 나눈다.
     */
    private long[] divide(long amount, int count) {
        long[] array = new long[count];
        long max = RandomUtils.nextLong(amount / count, amount / count * 2);
        for (int i = 0; i < count - 1; i++) {
            array[i] = RandomUtils.nextLong(1, Math.min(max, amount));
            amount -= array[i];
        }
        array[count - 1] = amount;
        return array;
    }
}
