package com.example.file.service.controller;

import com.example.file.service.dto.*;
import com.example.file.service.model.File;
import com.example.file.service.repository.FileRepository;
import com.example.file.service.service.FileService;
import com.example.file.service.web.exceptions.FileNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.file.service.Constants.BASE_URI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@ComponentScan({"com.example.file.service.web"})
public class FileControllerTest {

    @MockBean
    private FileService fileService;

    @MockBean
    private FileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadFileSuccess() throws Exception {
        String id = UUID.randomUUID().toString();

        when(fileService.upload(any())).thenReturn(new UploadFileResponse(id));

        mockMvc.perform(post(BASE_URI)
                        .content(objectMapper.writeValueAsString(objectMapper.readValue(ResourceUtils.getFile("classpath:json/upload-file-request.json"), JsonNode.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(id)));
    }

    @Test
    void uploadFileBadRequest() throws Exception {

        mockMvc.perform(post(BASE_URI)
                        .content(objectMapper.writeValueAsString(objectMapper.readValue(ResourceUtils.getFile("classpath:json/upload-file-bad-request.json"), JsonNode.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", equalTo(false)))
                .andExpect(jsonPath("$.error", is(notNullValue())));
    }

    @Test
    void deleteFileSuccess() throws Exception {
        String id = UUID.randomUUID().toString();

        when(fileService.delete(any())).thenReturn(new DeleteFileResponse(true));

        mockMvc.perform(delete(BASE_URI + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", equalTo(true)));
    }

    @Test
    void deleteFileNotFound() throws Exception {
        doThrow(new FileNotFoundException()).when(fileService).delete(any());

        mockMvc.perform(delete(BASE_URI + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", equalTo(false)))
                .andExpect(jsonPath("$.error", equalTo("file not found")));
    }

    @Test
    void assignTagsToFileSuccess() throws Exception {
        String id = UUID.randomUUID().toString();

        when(fileService.assignTags(any(), any())).thenReturn(new AssignTagsResponse(true));

        mockMvc.perform(post(BASE_URI + "/" + id + "/tags")
                        .content(objectMapper.writeValueAsString(objectMapper.readValue(ResourceUtils.getFile("classpath:json/assign-tags-request.json"), JsonNode.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", equalTo(true)));
    }

    @Test
    void removeTagsFromFileSuccess() throws Exception {
        String id = UUID.randomUUID().toString();

        when(fileService.removeTags(any(), any())).thenReturn(new RemoveTagsResponse(true));

        mockMvc.perform(delete(BASE_URI + "/" + id + "/tags")
                        .content(objectMapper.writeValueAsString(objectMapper.readValue(ResourceUtils.getFile("classpath:json/remove-tags-request.json"), JsonNode.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", equalTo(true)));
    }

    @Test
    void getFilesByFilterSuccess() throws Exception {
        List<File> files = List.of(
                new File("1", "name1", 12345L, Set.of("audio")),
                new File("2", "name2", 54321L, Set.of("audio")),
                new File("3", "name3", 12345L, Set.of("document"))
        );
        when(fileService.getAllByFilter(any(), any())).thenReturn(new GetFilesByFilterResponse(5L, files));

        mockMvc.perform(get(BASE_URI + "?tags=tag1,tag2,tag3&size=5")
                        .content(objectMapper.writeValueAsString(objectMapper.readValue(ResourceUtils.getFile("classpath:json/remove-tags-request.json"), JsonNode.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", equalTo(5)))
                .andExpect(jsonPath("$.page").isArray())
                .andExpect(jsonPath("$.page", hasSize(3)))
                .andExpect(jsonPath("$.page[*].id", containsInAnyOrder("1", "2", "3")))
                .andExpect(jsonPath("$.page[*].name", containsInAnyOrder("name1", "name2", "name3")))
                .andExpect(jsonPath("$.page[*].size", containsInAnyOrder(12345, 54321, 12345)))
                .andExpect(jsonPath("$.page[*].tags").isArray())
                .andExpect(jsonPath("$.page[*].tags[*]", containsInAnyOrder("audio", "document", "audio")));
    }

    @Test
    void getAll() {
        List<File> files = List.of(
                new File("1", "name1", 12345L, Set.of("audio")),
                new File("2", "name2", 54321L, Set.of("video")),
                new File("3", "name3", 12345L, Set.of("document"))
        );
        when(fileService.getAllFilesWithoutPagebale()).thenReturn(new GetAllFilesResponse(files));
        try {
            mockMvc.perform(get(BASE_URI + "/all")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                    .andExpect(jsonPath("$.*.[*]", hasSize(3)))
                    .andExpect(jsonPath("$.*.[*].id", containsInAnyOrder("1", "2", "3")))
                    .andExpect(jsonPath("$.*.[*].name", containsInAnyOrder("name1", "name2", "name3")))
                    .andExpect(jsonPath("$.*.[*].size", containsInAnyOrder(12345, 54321, 12345)))
                    .andExpect(jsonPath("$.*.[*].tags").isArray())
                    .andExpect(jsonPath("$.*.[*].tags[*]", containsInAnyOrder("audio", "video", "document")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}