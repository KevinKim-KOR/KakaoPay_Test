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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

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
     * @return {@link Sprinkle}
     */
    public Sprinkle sprinkle(String roomId, long userId, long amount, int count) {
        String token = tokenGenerator.generate();
        String sprinkle_key = token + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        Sprinkle sprinkle = new Sprinkle(token, roomId, userId, amount, count, sprinkle_key);
        sprinkleRepository.save(sprinkle);
        long[] divide = divide(sprinkle.getAmount(), sprinkle.getCount());
        for (int i = 0; i < divide.length; i++) {
            Recv recv = new Recv(sprinkle, i + 1, divide[i], token);
            recvRepository.save(recv);
        }
        return sprinkle;
    }

    @Transactional
    @Override
    public long recvAmount(String roomId, long userId, String token) {
        //Sprinkle sprinkle = sprinkleRepository.findByToken(token);
        List<Sprinkle> sprinkleList= new ArrayList<Sprinkle>();
        sprinkleList = sprinkleRepository.findListByToken(token);
        long returnVal = 0;
        String strError = "";
        int listSize = sprinkleList.size();

        for (Sprinkle sprinkle : sprinkleList) {
            if(listSize > 0 && returnVal == 0) {
                if (Objects.isNull(sprinkle)) { // 조회된 뿌리기 없음
                    //throw new SprinkleNotFoundException();
                    strError = "A";
                } else if (sprinkle.isExpired(10)) { // 만료됨 (종료됨)
                    //throw new SprinkleCompletedException();
                    strError = "B";
                } else if (!sprinkle.getRoomId().equals(roomId)) { // 다른 대화방 사용자
                    //throw new NotBelongRoomException();
                    strError = "C";
                } else if (sprinkle.getUserId() == userId) { // 뿌린 사용자가 주울려고 함
                    //throw new SelfRecvException();
                    strError = "D";
                } else if (sprinkle.getRecvs().stream().noneMatch(Recv::isNotReceived)) { // 모두 주워감
                    //throw new SprinkleCompletedException();
                    strError = "E";
                }

                if (sprinkle.getRecvs().stream()
                        .filter(Recv::isReceived)
                        .anyMatch(it -> it.getUserId() == userId)) { // 한번만 받을 수 있다.
                    //throw new DuplicatedPickupException();
                    strError = "F";
                }

                if(strError == "") {
                    List<Recv> notPickedUpYet = sprinkle.getRecvs().stream() // 아직 받지 않은 건만 필터
                            .filter(Recv::isNotReceived)
                            .collect(Collectors.toList());
                    // 첫번째 받기
                    Recv recv = notPickedUpYet.get(0);
                    // 받은금액
                    recv.recvAmount(userId);

                    returnVal = recv.getRecvAmount();
                }
                listSize--;
            }
        }

        if (strError.equals("A")) { // 조회된 뿌리기 없음
            throw new SprinkleNotFoundException();
        } else if (strError.equals("B")) { // 만료됨 (종료됨)
            throw new SprinkleCompletedException();
        } else if (strError.equals("C")) { // 다른 대화방 사용자
            throw new NotBelongRoomException();
        } else if (strError.equals("D")) { // 뿌린 사용자가 주울려고 함
            throw new SelfRecvException();
        } else if (strError.equals("E")) { // 모두 주워감
            throw new SprinkleCompletedException();
        }

        if(returnVal == 0) {
            throw new SprinkleCompletedException();
        }

        // 받은 금액 리턴
        return returnVal;
    }

    @Transactional(readOnly = true)
    @Override
    public Sprinkle retreiveSprinkleList(long userId, String token) {
        LocalDateTime createdDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusDays(7);

        Sprinkle sprinkle = sprinkleRepository.findByTokenAndCreatedAtGreaterThan(token, createdDatetime);
        log.debug("sprinkle = {}", sprinkle);
        if (Objects.isNull(sprinkle)) {
            throw new SprinkleNotFoundException();
        }
        if (sprinkle.getUserId() != userId) {
            throw new AccessDeniedException();
        }

        return sprinkle;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sprinkle> retreiveSprinkleList2(long userId, String token) {
        LocalDateTime createdDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusDays(7);
        List<Sprinkle> list = new ArrayList<Sprinkle>();
        List<Sprinkle> returnList = new ArrayList<Sprinkle>();

        list = sprinkleRepository.findListByTokenAndCreatedAtGreaterThan(token, createdDatetime);

        for (Sprinkle sprinkle : list) {
            log.debug("sprinkle = {}", sprinkle);
            if (Objects.isNull(sprinkle)) {
               // Do Nothing
            } else if (sprinkle.getUserId() != userId) {
                // Do Nothing
            } else {
                returnList.add(sprinkle);
            }
        }


        return returnList;
    }

    /**
     * 금액을 나눈다.
     */
    private long[] divide(long amount, int count) {
        long[] array = new long[count];
        // 첫 번째 사람이 모든 금액을 가져가지 않게 count만큼 빼준다.
        long max = RandomUtils.nextLong(1, (amount - count));

        for (int i = 0; i < count - 1; i++) {
            array[i] = RandomUtils.nextLong(1, Math.min(max, amount));
            amount -= array[i];
        }
        // 마지막 값 처리
        array[count - 1] = amount;
        return array;
    }
}
