package com.exam.kakaopay;

import com.exam.kakaopay.constants.Codes;
import com.exam.kakaopay.constants.Header;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class XHeaderTest extends SprinkleTests {

    @Test
    @DisplayName("사용자 식별값 누락")
    void test001() throws Exception {
        mockMvc.perform(
                post("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, "112")
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0100.code)))
                .andExpect(jsonPath("$.message", Matchers.notNullValue()))
        ;
    }

    @Test
    @DisplayName("대화방 식별값 누락")
    void test002() throws Exception {
        mockMvc.perform(
                post("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, "AAZ")
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0100.code)))
                .andExpect(jsonPath("$.message", Matchers.notNullValue()))
        ;
    }

}
