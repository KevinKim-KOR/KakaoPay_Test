package com.exam.kakaopay.jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
//@Table(name = "tb_pickup")
@Table(name = "tb_sprinkle_recv")
public class Recv implements Serializable {
    /**
     * 고유 아이디

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long id;
     */
    /**
     * 고유 아이디
     */
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "seq_sprinkle_recv")
     private Long id;

    /**
     * 뿌리기 엔티티
     */
    @NonNull
    @ManyToOne(targetEntity = Sprinkle.class, fetch = FetchType.EAGER)
    //@JoinColumn(name = "token")
    @JoinColumn(name = "sprinkleKey")
    private Sprinkle sprinkle;

    /**
     * 순번
     */
    @NonNull
    @Column(name = "seq", nullable = false)
    private Integer seq;

    /**
     * 받은 금액
     */
    @NonNull
//    @Column(name = "amount", nullable = false)
//    private Long amount;
    @Column(name = "recv_amount", nullable = false)
    private Long recvAmount;

    /**
     * 받은 금액
     */
    @NonNull
    @Column(name = "token", nullable = false)
    private String token;

    /**
     * 받은 사용자 식별값
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 받은 일시
     */
//    @Column(name = "pickup_at")
//    private LocalDateTime pickupAt;
    @Column(name = "recv_datetime")
    private LocalDateTime recvDatetime;

    /**
     * 뿌린 금액 받기.
     *
     * @param userId 사용자 식별값
     */
    public void recvAmount(long userId) {
        this.userId = userId;
//        pickupAt = LocalDateTime.now();
        recvDatetime = LocalDateTime.now();
    }

    /**
     * 뿌린 금액 받았는지 ?
     *
     * @return {@code} 받은 아이디
     */
    public boolean isReceived() {
        return Objects.nonNull(userId);
    }

    /**
     * 뿌린 금액 아직 안받았는지 ?
     *
     * @return {@code true} 받은 아이디 없음
     */
    public boolean isNotReceived() {
        return !isReceived();
    }

}
