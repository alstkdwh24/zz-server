package com.example.zzserver;

import com.example.zzserver.amenities.dto.request.RoomAmenityRequest;
import com.example.zzserver.amenities.dto.response.RoomAmenityResponse;
import com.example.zzserver.amenities.service.RoomsAmenitiesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(MockConfig.class)
@AutoConfigureMockMvc
@Transactional
public class RoomsAmenitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RoomsAmenitiesService roomsAmenitiesService;

    private RoomAmenityResponse roomAmenityResponse;

    private RoomAmenityResponse roomAmenityResponse2;

    private RoomAmenityResponse roomAmenityResponse3;

    private RoomAmenityResponse roomAmenityResponse4;

    @BeforeEach
    public void setup() {
        roomAmenityResponse=RoomAmenityResponse.builder()
                .roomId(UUID.randomUUID())
                .amenityId(UUID.randomUUID())
                .build();

        roomAmenityResponse2=RoomAmenityResponse.builder()
                .roomId(UUID.randomUUID())
                .amenityId(UUID.randomUUID()).build();

        roomAmenityResponse3=RoomAmenityResponse.builder()
                .roomId(UUID.randomUUID())
                .amenityId(UUID.randomUUID()).build();

        roomAmenityResponse4=RoomAmenityResponse.builder()
                .roomId(UUID.randomUUID())
                .amenityId(UUID.randomUUID()).build();
    }

    @Test
    public void apiRoomsAmenitiesRoomsRoomId () throws Exception {

        UUID roomId = roomAmenityResponse.getRoomId();

        List<RoomAmenityResponse> RoomsAmenities= Arrays.asList(
                roomAmenityResponse,
                roomAmenityResponse2,
                roomAmenityResponse3,
                roomAmenityResponse4
            );

        when(roomsAmenitiesService.findByRoomId(roomId)).thenReturn(RoomsAmenities);
        mockMvc.perform(get("/api/rooms-amenities/room/{roomId}",roomId)).andExpect(status().isOk(

        )).andExpect(content().json(objectMapper.writeValueAsString(RoomsAmenities)))
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));



    }
    @Test
    public void apiRoomsAmenitiesPostTest () throws Exception {

        UUID id=UUID.randomUUID();
        UUID roomId = roomAmenityResponse.getRoomId();
        UUID amenityId = roomAmenityResponse.getAmenityId();

        when(roomsAmenitiesService.create(roomId, amenityId)).thenReturn(id);
        RoomAmenityRequest request = new RoomAmenityRequest(roomId, amenityId); // 생성자 또는 빌더 사용

        mockMvc.perform(post("/api/rooms-amenities")
                .content(objectMapper.writeValueAsString(request))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + id.toString() + "\""))                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    public void apiRoomsAmenitiesDeleteTest() throws Exception {

        UUID roomId = roomAmenityResponse.getRoomId();
        UUID amenityId = roomAmenityResponse.getAmenityId();

        mockMvc.perform(delete("/api/rooms-amenities")
                .content(objectMapper.writeValueAsString(new RoomAmenityRequest(roomId, amenityId)))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));

    }

}
