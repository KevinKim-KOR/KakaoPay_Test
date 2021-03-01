package com.exam.kakaopay;

import com.exam.kakaopay.jpa.entity.Recv;
import com.exam.kakaopay.jpa.entity.Sprinkle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exam.kakaopay.constants.Header;
import com.exam.kakaopay.controller.SprinkleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
abstract public class SprinkleTests {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    protected Sprinkle stub() {
        Sprinkle sprinkle = new Sprinkle("AAA", "A", 10L, 1000L, 4, "AAA");
        Recv recv1 = new Recv(sprinkle, 1, 330L, "AAA");
        recv1.recvAmount(20); // 20 사용자가 받아감
        sprinkle.getRecvs().add(recv1);
        sprinkle.getRecvs().add(new Recv(sprinkle, 2, 269L, "AAA"));
        sprinkle.getRecvs().add(new Recv(sprinkle, 3, 235L, "AAA"));
        sprinkle.getRecvs().add(new Recv(sprinkle, 4, 466L, "AAA"));
        return sprinkle;
    }

    protected ResultActions sprinkle(String roomId, long userId, long amount, int count) throws Exception {
        SprinkleRequest request = new SprinkleRequest();
        request.setAmount(amount);
        request.setCount(count);

        return mockMvc.perform(
                post("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, roomId)
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                ;
    }

    protected ResultActions pickup(String token, String roomId, long userId) throws Exception {
        return mockMvc.perform(
                put("/api/v1/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, roomId)
                        .header(Header.USER_ID, userId)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                ;
    }

    protected ResultActions inquiry(String token, String roomId, long userId) throws Exception {
        return mockMvc.perform(
                get("/api/v1/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, roomId)
                        .header(Header.USER_ID, userId)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
