package com.exam.kakaopay.controller;

import com.exam.kakaopay.jpa.entity.Recv;
import com.exam.kakaopay.jpa.entity.Sprinkle;
import com.exam.kakaopay.service.SprinkleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.exam.kakaopay.constants.Codes;
import com.exam.kakaopay.constants.Header;
import com.exam.kakaopay.dto.RecvDTO;
import com.exam.kakaopay.dto.SprinkleDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SprinkleApiController {
    private final SprinkleService sprinkleService;

    @PostMapping
    ResponseEntity<ApiResponse> sprinkle(
            @RequestHeader(Header.ROOM_ID) String roomId,
            @RequestHeader(Header.USER_ID) long userId,
            @RequestBody SprinkleRequest request,
            UriComponentsBuilder b) {
        log.debug("roomId={}, userId={}, body={}", roomId, userId, request);

        Sprinkle sprinkle = sprinkleService.sprinkle(roomId, userId, request.getAmount(), request.getCount());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(b.path("/{token}").buildAndExpand(sprinkle.getToken()).toUri());

        ApiResponse response = ApiResponse.of(Codes.S0000, sprinkle.getToken());
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{token:[a-zA-Z]{3}}")
    ApiResponse recvAmount(
            @RequestHeader(Header.ROOM_ID) String roomId,
            @RequestHeader(Header.USER_ID) long userId,
            @PathVariable("token") String token) {
        log.debug("roomId={}, userId={}, token={}", roomId, userId, token);
        long amount = sprinkleService.recvAmount(roomId, userId, token);
        log.debug("amount = {}", amount);
        return ApiResponse.of(Codes.S0000, amount);
    }

    @GetMapping(value = "/{token:[a-zA-Z]{3}}")
    ApiResponse retreiveSprinkleList2(
            @RequestHeader(Header.ROOM_ID) String roomId,
            @RequestHeader(Header.USER_ID) long userId,
            @PathVariable("token") String token) {
        log.debug("roomId={}, userId={}, token={}", roomId, userId, token);
        List<Sprinkle> sprinkleList = sprinkleService.retreiveSprinkleList2(userId, token);
        List<SprinkleDTO> dtoList = new ArrayList<SprinkleDTO>();

        for (Sprinkle sprinkle : sprinkleList) {
            SprinkleDTO dto = new SprinkleDTO(
                    sprinkle.getCreatedAt(),
                    sprinkle.getAmount(),
                    sprinkle.getRecvs().stream().filter(Recv::isReceived).mapToLong(Recv::getRecvAmount).sum(),
                    sprinkle.getRecvs().stream()
                            .filter(Recv::isReceived)
                            .map(it -> new RecvDTO(it.getUserId(), it.getRecvAmount()))
                            .collect(Collectors.toList())
            );
            dtoList.add(dto);
        }

        return ApiResponse.of(Codes.S0000, dtoList);
    }

}
