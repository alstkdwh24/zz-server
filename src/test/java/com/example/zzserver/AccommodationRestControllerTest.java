package com.example.zzserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.example.zzserver.accommodation.consts.AccommodationType;
import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.dto.response.AccommodationResponseDto;
import com.example.zzserver.accommodation.service.AccommodationService;
import com.example.zzserver.address.domain.Address;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(MockConfig.class)
@AutoConfigureMockMvc
@Transactional

public class AccommodationRestControllerTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @MockitoBean // ✅ 새로 권장되는 애노테이션
        private AccommodationService accommodationService;

        private AccommodationResponseDto acc4;
        private AccommodationResponseDto acc2;
        private AccommodationResponseDto acc1;

        private AccommodationResponseDto acc3;

        @BeforeEach
        void setUp() {

                acc1 = AccommodationResponseDto.builder()
                                .id(UUID.randomUUID())
                                .name("Test Accommodation 1")
                                .address(Address.of("123 Test St", "`11212", "Test City"))
                                .latitude(37.5665)
                                .longitude(37.5665)
                                .type(AccommodationType.CAMPING).build();

                acc2 = AccommodationResponseDto.builder()
                                .id(UUID.randomUUID())
                                .name("Test Accommodation 2")
                                .address(Address.of("456 Test Ave", "11212", "Test City"))
                                .latitude(37.5665)
                                .longitude(37.5665)
                                .type(AccommodationType.HOTEL).build();

                acc3 = AccommodationResponseDto.builder()
                                .id(UUID.randomUUID())
                                .name("Test Accommodation 3")
                                .address(Address.of("789 Test Blvd", "11212", "Test City"))
                                .latitude(37.5665)
                                .longitude(37.5665)
                                .type(AccommodationType.RESORT).build();

                acc4 = AccommodationResponseDto.builder()
                                .id(UUID.randomUUID())
                                .name("Test Accommodation 4")
                                .address(Address.of("101 Test Rd", "11212", "Test City"))
                                .latitude(37.5665)
                                .longitude(37.5665)
                                .type(AccommodationType.GUEST_HOUSE).build();

        }

        @Test
        public void apiAccommmodationsTestGet() throws Exception {

                List<AccommodationResponseDto> accommodations = List.of(acc1, acc2, acc3, acc4);

                when(accommodationService.readDisplayedList()).thenReturn(accommodations);

                mockMvc.perform(get("/api/accommodations/"))
                                .andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(accommodations)))
                                .andDo(MockMvcResultHandlers.print())
                                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                preprocessRequest(
                                                                modifyHeaders().remove("Content-Length").remove("Host"),
                                                                prettyPrint()),
                                                preprocessResponse(modifyHeaders().remove("Content-length")
                                                                .remove("X-Content-Type-Options")
                                                                .remove("X-XSS-Protection").remove("Cache-Control")
                                                                .remove("Pragma")
                                                                .remove("Expires").remove("X-Frame-Options"),
                                                                prettyPrint()),

                                                responseFields(
                                                                fieldWithPath("[].id").description("숙소 ID"),
                                                                fieldWithPath("[].name").description("숙소 이름"),
                                                                fieldWithPath("[].address").description("숙소 주소"),
                                                                fieldWithPath("[].latitude").description("숙소 위도"),
                                                                fieldWithPath("[].longitude").description("숙소 경도"),
                                                                fieldWithPath("[].type").description("숙소 타입"),
                                                                fieldWithPath("[].displayed").description("노출 여부")

                                                )));

                // Implement the test logic here
        }

        @Test
        public void apiAccommmodationsTestPost() throws Exception {
                AccommodationRequest newAccommodationRequest = AccommodationRequest.builder()
                                .bussinessUserId(UUID.randomUUID())
                                .name("New Accommodation")
                                .zipCode("13qw1qe12").address("12345")
                                .detailAddress("123 Test St, Test City")
                                .latitude(37.5665)
                                .longitude(37.5665)
                                .type(AccommodationType.HOTEL)
                                .build();

                UUID newId = UUID.randomUUID();
                when(accommodationService.createAccommodation(any(AccommodationRequest.class), any()))
                                .thenReturn(newId);
                MockMultipartFile imageFile = new MockMultipartFile(
                                "request", "", "application/json",
                                objectMapper.writeValueAsBytes(newAccommodationRequest));

                mockMvc.perform(
                                org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
                                                .multipart("/api/accommodations/")
                                                .file(imageFile)
                                                .contentType("multipart/form-data"))
                                .andExpect(status().isOk())
                                .andDo(MockMvcResultHandlers.print())
                                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                preprocessRequest(prettyPrint()),
                                                preprocessResponse(prettyPrint())
                                // responseFields(
                                // fieldWithPath("id").description("생성된 숙소의 ID")
                                // )
                                ));

        }

        @Test
        public void apiAccommmodationsIdTestGet() throws Exception {
                UUID id = UUID.randomUUID();
                AccommodationResponseDto accommodation = new AccommodationResponseDto(
                                id,
                                "Test Accommodation",
                                Address.of("123 Test St", "11212", "Test City"),
                                37.5665,
                                126.9780,
                                AccommodationType.HOTEL,
                                true);

                when(accommodationService.findById(id)).thenReturn(accommodation);

                mockMvc.perform(get("/api/accommodations/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(accommodation)))
                                .andDo(MockMvcResultHandlers.print())
                                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                preprocessRequest(prettyPrint()),
                                                preprocessResponse(prettyPrint()),
                                                responseFields(
                                                                fieldWithPath("id").description("숙소 ID"),
                                                                fieldWithPath("name").description("숙소 이름"),
                                                                fieldWithPath("address").description("숙소 주소"),
                                                                fieldWithPath("latitude").description("숙소 위도"),
                                                                fieldWithPath("longitude").description("숙소 경도"),
                                                                fieldWithPath("type").description("숙소 타입"),
                                                                fieldWithPath("displayed").description("노출 여부"))));
        }

        @Test
        public void apiAccommmodationsIdTestPatch() throws Exception {
                UUID id = UUID.randomUUID();
                AccommodationRequest updateRequest = AccommodationRequest.builder()
                                .bussinessUserId(UUID.randomUUID())
                                .name("Updated Accommodation")
                                .zipCode("12345").address("123 Test St, Test City")
                                .detailAddress("123 Test St, Test City")
                                .latitude(37.5665)
                                .longitude(126.9780)
                                .type(AccommodationType.RESORT)
                                .build();

                MockMultipartFile requestFile = new MockMultipartFile(
                                "request", "", "application/json",
                                objectMapper.writeValueAsBytes(updateRequest));

                when(accommodationService.updateAccommodations(any(UUID.class), any(AccommodationRequest.class), any(),
                                any()))
                                .thenReturn(updateRequest.getBussinessUserId());

                mockMvc.perform(
                                org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
                                                .multipart("/api/accommodations/{id}", id)
                                                .file(requestFile)
                                                .contentType("multipart/form-data")
                                                .with(request -> {
                                                        request.setMethod("PATCH");
                                                        return request;
                                                }))
                                .andExpect(status().isOk())
                                .andExpect(content().string(
                                                objectMapper.writeValueAsString(updateRequest.getBussinessUserId())))
                                .andDo(MockMvcResultHandlers.print())
                                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                preprocessRequest(prettyPrint()),
                                                preprocessResponse(prettyPrint())
                                // responseFields(
                                // fieldWithPath("bussinessUserId").description("업데이트된 숙소의 사업자 사용자 ID")
                                // )
                                ));

        }

        @Test
        public void apiAccommmodationsIdTestDelete() throws Exception {
                UUID id = UUID.randomUUID();
                AccommodationResponseDto accommodation = new AccommodationResponseDto(
                                id,
                                "Test Accosdasmmodation",
                                Address.of("12323 Test St", "111312212", "Test City"),
                                37.562365,
                                123.9780,
                                AccommodationType.HOTEL,
                                true);
                when(accommodationService.findById(id)).thenReturn(accommodation);

                doNothing().when(accommodationService).deleteById(id);

                mockMvc.perform(delete("/api/accommodations/{id}", id))
                                .andExpect(status().isNoContent()).andDo(MockMvcResultHandlers.print())
                                .andDo(
                                                MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                                preprocessRequest(prettyPrint()),
                                                                preprocessResponse(prettyPrint())));

        }

}
