package com.exam.kakaopay;

import com.exam.kakaopay.jpa.repository.SprinkleRepository;
import com.exam.kakaopay.constants.Codes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InquiryApiTest extends SprinkleTests {
    @MockBean
    private SprinkleRepository repository;

    @Test
    @DisplayName("token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다.")
    void test001() throws Exception {
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(stub());

        inquiry("MGh", "A", 10) // 뿌린 본인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(Codes.S0000.code)))
//                .andExpect(jsonPath("$.body.list[0].totalAmount", is(330)))
//                .andExpect(jsonPath("$.body.recvAmount", is(330)))
//                .andExpect(jsonPath("$.body.recvDTOList.length()", is(1)))
//                .andExpect(jsonPath("$.body.recvDTOList[0].userId", is(20)))
//                .andExpect(jsonPath("$.body.recvDTOList[0].amount", is(330)))
        ;
    }

    @Test
    @DisplayName("뿌린 사람만 조회를 할 수 있습니다.")
    void test002() throws Exception {
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(stub());

        inquiry("Cjg", "A", 90) // 뿌린 본인이 아님
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(Codes.E0030.code)))
        ;
    }

    @Test
    @DisplayName("유효하지 않은 token에 대해서는 조회 실패응답이 내려가야 합니다.")
    void test003() throws Exception {
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(stub());

        pickup("ZZZ", "A", 10) // "ZZZ" = 존재하지 않는 토큰
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(Codes.E0040.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("뿌린 건에 대해서는 7일동안 조회할 수 있습니다.")
    void test004() throws Exception {
        // 조회 데이터 없음
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(null);

        pickup("Cjg", "A", 10)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(Codes.E0040.code)))
                .andExpect(jsonPath("$.body", nullValue()))
        ;
    }

}
