package com.example.zzserver;


import com.example.zzserver.accommodation.dto.request.RoomsRequest;
import com.example.zzserver.accommodation.service.RoomsService;
import com.example.zzserver.rooms.dto.response.RoomsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(MockConfig.class)
@AutoConfigureMockMvc
@Transactional
public class RoomsControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RoomsService roomsService;

    private RoomsResponse roomsResponse;

    private RoomsResponse roomsResponse2;

    private RoomsResponse roomsResponse3;

    private RoomsResponse roomsResponse4;

    private RoomsResponse roomsResponse5;

    private RoomsResponse roomsResponse6;

    @BeforeEach
    public void setUp() {
        // 초기화 작업을 여기에 작성
        roomsResponse = RoomsResponse.builder()
                .id(UUID.randomUUID())
                .name("Test Room")
                .maxOccupacy(2)
                .available(true)
                .peopleCount(4)
                .build();



        // 필요한 다른 필드도 초기화

        roomsResponse2 = RoomsResponse.builder()
                .id(UUID.randomUUID())
                .name("Test Room 2")
                .maxOccupacy(3)
                .available(true)
                .peopleCount(1)
                .build();


        roomsResponse3 = RoomsResponse.builder()
                .id(UUID.randomUUID())
                .name("Test Room 3")
                .maxOccupacy(4)
                .available(false)
                .peopleCount(2)
                .build();

        roomsResponse4 = RoomsResponse.builder()
                .id(UUID.randomUUID())
                .name("Test Room 4")
                .maxOccupacy(5)
                .available(true)
                .peopleCount(3)
                .build();
    }


    @Test
    public void createRoom() throws Exception {

        UUID id = UUID.randomUUID();

        roomsResponse5 = RoomsResponse.builder()
                .id(id)
                .name("Test Room 5")
                .maxOccupacy(6)
                .available(true)
                .peopleCount(2)
                .build();


        when(roomsService.create(Mockito.any(),List.of(Mockito.any()))).thenReturn(id);

        mockMvc.perform(
                        post("/").contentType("application/json")
                                .content(objectMapper.writeValueAsString(roomsResponse5))
                )                        .andExpect(status().isOk()).

                andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("방 ID")
                                , fieldWithPath("name").description("방 이름"),

                                fieldWithPath("maxOccupacy").description("최대 수용 인원"),
                                fieldWithPath("available").description("방 사용 가능 여부"),
                                fieldWithPath("peopleCount").description("현재 인원 수"))));


    }
//    @Test
//    public void createRoom() throws Exception {
//
//        UUID id = UUID.randomUUID();
//
//        roomsResponse5 = RoomsResponse.builder()
//                .id(id)
//                .name("Test Room 5")
//                .maxOccupacy(6)
//                .available(true)
//                .peopleCount(2)
//                .build();
//
//
//        when(roomsService.create(Mockito.any())).thenReturn(roomsResponse5);
//
//        mockMvc.perform(
//                post("/").contentType("application/json")
//                        .content(objectMapper.writeValueAsString(roomsResponse5))
//        )                        .andExpect(status().isOk()).
//                andDo(MockMvcResultHandlers.print())
//                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
//                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
//                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
//                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
//                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
//                        responseFields(
//                                fieldWithPath("id").description("방 ID")
//                        , fieldWithPath("name").description("방 이름"),
//                                fieldWithPath("maxOccupacy").description("최대 수용 인원"),
//                                fieldWithPath("available").description("방 사용 가능 여부"),
//                                fieldWithPath("peopleCount").description("현재 인원 수"))));
//
//
//    }


    @Test
    public void createRoomIdGet() throws Exception {

        UUID id = UUID.randomUUID();

        roomsResponse6 = RoomsResponse.builder()
                .id(id)
                .name("Test Room 6")
                .maxOccupacy(7)
                .available(true)
                .peopleCount(3)
                .build();

        when(roomsService.findById(id)).thenReturn(roomsResponse6);


        mockMvc.perform(get("/"+ id).contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("방 ID"),
                                fieldWithPath("name").description("방 이름"),
                                fieldWithPath("maxOccupacy").description("최대 수용 인원"),
                                fieldWithPath("available").description("방 사용 가능 여부"),
                                fieldWithPath("peopleCount").description("현재 인원 수"))));

    }

    @Test
    public void accommodationAccommodationId() throws Exception {

        UUID accommodationId = UUID.randomUUID();

        when(roomsService.getAllByAccommodation(accommodationId)).thenReturn(List.of(roomsResponse, roomsResponse2, roomsResponse3, roomsResponse4));

        mockMvc.perform(get("/accommodation/" + accommodationId).contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("방 ID"),
                                fieldWithPath("[].name").description("방 이름"),
                                fieldWithPath("[].maxOccupacy").description("최대 수용 인원"),
                                fieldWithPath("[].available").description("방 사용 가능 여부"),
                                fieldWithPath("[].peopleCount").description("현재 인원 수"))));

    }

    @Test
    public void idPatch() throws Exception {

        UUID id = UUID.randomUUID();

        roomsResponse5 = RoomsResponse.builder()
                .id(id)
                .name("Updated Room")
                .maxOccupacy(8)
                .available(false)
                .peopleCount(1)
                .build();

        when(roomsService.update(Mockito.any(), (RoomsRequest) List.of(Mockito.any()), Mockito.any(),List.of(Mockito.any()))).thenReturn(id);

        mockMvc.perform(
                        patch("/" + id).contentType("application/json")
                                .content(objectMapper.writeValueAsString(roomsResponse5))
                )

                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("방 ID"),
                                fieldWithPath("name").description("방 이름"),
                                fieldWithPath("maxOccupacy").description("최대 수용 인원"),
                                fieldWithPath("available").description("방 사용 가능 여부"),
                                fieldWithPath("peopleCount").description("현재 인원 수"))));
    }


    @Test
    public void idDelete() throws Exception {

        UUID id = UUID.randomUUID();

        RoomsResponse roomsResponseToDeleteTest = RoomsResponse.builder()
                .id(id)
                .name("Room to Delete")
                .maxOccupacy(2)
                .available(true)
                .peopleCount(0)
                .build();
        Mockito.when(roomsService.create(Mockito.any(),Mockito.any())).thenReturn(id);
        // Mocking the service call
        Mockito.doNothing().when(roomsService).delete(id);

        mockMvc.perform(delete("/" + id))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint())));
    }
//    @Test
//    public void idPatch() throws Exception {
//
//        UUID id = UUID.randomUUID();
//
//        roomsResponse5 = RoomsResponse.builder()
//                .id(id)
//                .name("Updated Room")
//                .maxOccupacy(8)
//                .available(false)
//                .peopleCount(1)
//                .build();
//
//        when(roomsService.update(Mockito.any(), Mockito.any())).thenReturn(roomsResponse5);
//
//        mockMvc.perform(
//                patch("/" + id).contentType("application/json")
//                        .content(objectMapper.writeValueAsString(roomsResponse5))
//        )
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
//                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
//                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
//                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
//                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
//                        responseFields(
//                                fieldWithPath("id").description("방 ID"),
//                                fieldWithPath("name").description("방 이름"),
//                                fieldWithPath("maxOccupacy").description("최대 수용 인원"),
//                                fieldWithPath("available").description("방 사용 가능 여부"),
//                                fieldWithPath("peopleCount").description("현재 인원 수"))));
//    }
//
//
//    @Test
//    public void idDelete() throws Exception {
//
//        UUID id = UUID.randomUUID();
//
//        RoomsResponse roomsResponseToDeleteTest = RoomsResponse.builder()
//                .id(id)
//                .name("Room to Delete")
//                .maxOccupacy(2)
//                .available(true)
//                .peopleCount(0)
//                .build();
//        Mockito.when(roomsService.create(Mockito.any())).thenReturn(roomsResponseToDeleteTest);
//        // Mocking the service call
//        Mockito.doNothing().when(roomsService).delete(id);
//
//        mockMvc.perform(delete("/" + id))
//                .andExpect(status().isNoContent())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
//                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
//                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
//                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
//                                .remove("Expires").remove("X-Frame-Options"), prettyPrint())));
//    }
}