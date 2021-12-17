package com.wootech.dropthecode.controller;

import javax.persistence.EntityNotFoundException;

import com.wootech.dropthecode.dto.request.RoomRequest;
import com.wootech.dropthecode.dto.response.RoomIdResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wootech.dropthecode.controller.mockmvc.RestDocsMockMvcFactory.OBJECT_MAPPER;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoomControllerTest extends ControllerTest {

    @Autowired
    RoomController roomController;

    @Test
    @DisplayName("채팅방 id를 받아온다. - 성공")
    void getRoomId() throws Exception {
        // given
        RoomIdResponse roomIdResponse = new RoomIdResponse(1L);
        given(roomService.getOrCreate(isA(RoomRequest.class))).willReturn(roomIdResponse);

        // when
        ResultActions resultActions = this.successMockMvc.perform(get("/rooms?studentId=1&teacherId=2")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                     .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(roomIdResponse)));
    }

    @Test
    @DisplayName("채팅방 id를 받아온다. - 존재하지 않는 member의 id로 인한 실패")
    void getRoomIdFailure() throws Exception {
        // given
        given(roomService.getOrCreate(isA(RoomRequest.class))).willThrow(new EntityNotFoundException("존재하지 않는 멤버입니다."));

        // when
        ResultActions resultActions = this.failMockMvc.perform(get("/rooms?studentId=1&teacherId=2")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
