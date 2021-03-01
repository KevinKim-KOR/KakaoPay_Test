package com.exam.kakaopay;

import com.exam.kakaopay.jpa.entity.Sprinkle;
import com.exam.kakaopay.jpa.repository.SprinkleRepository;
import com.exam.kakaopay.constants.Codes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecvApiTest extends SprinkleTests {
    @MockBean
    private SprinkleRepository repository;

    @Test
    @DisplayName("받기 요청 하면 금액을 응답값으로 내려준다")
    void test001() throws Exception {
        Sprinkle sprinkle = stub();

        when(repository.findByToken(anyString())).thenReturn(sprinkle);

        pickup("ABC", "A", 90)  // 받지 않은 사용자
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.body").value(sprinkle.getRecvs().get(1).getAmount()))
        ;
    }

    @Test
    @DisplayName("뿌리기는 사용자 당 한 번만 받을 수 있습니다.")
    void test002() throws Exception {
        when(repository.findByToken(anyString())).thenReturn(stub());

        pickup("ABC", "A", 20) // 이미 받은 사용자
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0200.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없습니다.")
    void test003() throws Exception {
        when(repository.findByToken(anyString())).thenReturn(stub());

        pickup("ABC", "A", 10)  // 뿌리기한 사용자
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0200.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("뿌린 사용자와 동일한 대화방에 속한 사용자만이 받을 수 있습니다.")
    void test004() throws Exception {
        when(repository.findByToken(anyString())).thenReturn(stub());

        pickup("ABC", "B", 60)      // "B"는 다른 대화방
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0200.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("받기는 뿌린 후 10분간만 유효합니다.")
    void test005() throws Exception {
        Sprinkle sprinkle = Mockito.mock(Sprinkle.class);
        when(sprinkle.isExpired(anyInt())).thenReturn(true);     // 만료를 응답함
        when(repository.findByToken(anyString())).thenReturn(sprinkle);

        pickup("ABC", "A", 60)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0200.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

}
