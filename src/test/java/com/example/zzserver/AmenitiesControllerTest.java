package com.example.zzserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.zzserver.accommodation.dto.request.AmenitiesRequest;
import com.example.zzserver.accommodation.dto.response.AmenitiesResponse;
import com.example.zzserver.accommodation.service.AmenitiesService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(MockConfig.class)
@AutoConfigureMockMvc
@Transactional
public class AmenitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AmenitiesService amenitiesService;


    private AmenitiesResponse amenitiesResponse ;

    private AmenitiesResponse amenitiesResponse2;

    private AmenitiesResponse amenitiesResponse3;

    private AmenitiesResponse amenitiesResponse4;

    @BeforeEach
    void setUp(){
        amenitiesResponse= AmenitiesResponse.builder()
                .id(UUID.randomUUID())
                .name("Free Wi-Fi")
                .build();

        amenitiesResponse2= AmenitiesResponse.builder()
                .id(UUID.randomUUID())
                .name("Swimming Pool")
                .build();
        amenitiesResponse3= AmenitiesResponse.builder()
                .id(UUID.randomUUID())
                .name("Gym")
                .build();

        amenitiesResponse4= AmenitiesResponse.builder()
                .id(UUID.randomUUID())
                .name("Parking")
                .build();


    }

    @Test
    public void apiAmenitiesIdTestGet() throws Exception {


        List<AmenitiesResponse> amenitiesResponses = List.of(
                amenitiesResponse,
                amenitiesResponse2,
                amenitiesResponse3,
                amenitiesResponse4
        );

        when(amenitiesService.findAll())
                .thenReturn(amenitiesResponses);

        mockMvc.perform(get("/api/amenities")).
                andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                                ,responseFields(
                                        fieldWithPath("[].id").description("The unique identifier of the amenity"),
                                        fieldWithPath("[].name").description("The name of the amenity"),
                                        fieldWithPath("[].iconUrl").description("The date and time when the amenity was created")
                                )));
    }

    @Test
    public void apiAmenitiesIdTestPost() throws Exception {
        AmenitiesRequest response = AmenitiesRequest.builder()
                .name("Frees Wi-Fi")
                .build();

        UUID newId = UUID.randomUUID();
        when(amenitiesService.create(any()))
                .thenReturn(newId);

        mockMvc.perform(
            post("/api/amenities")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(response)
                )
        )                    .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;


    }

    @Test
    public void apiAmenitiesIdTestDelete() throws Exception {


        UUID newId = amenitiesResponse4.getId();
        when(amenitiesService.create(any())).thenReturn(newId);


        doNothing().when(amenitiesService).deleteById(newId);


        mockMvc.perform(
                        delete("/api/amenities/{id}", newId)
        )
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }
}
