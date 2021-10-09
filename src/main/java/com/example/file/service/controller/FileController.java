package com.example.file.service.controller;

import com.example.file.service.dto.*;
import com.example.file.service.filter.FileFilter;
import com.example.file.service.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.file.service.Constants.BASE_URI;

@Slf4j
@RestController
@RequestMapping(BASE_URI)
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public UploadFileResponse upload(@Validated @RequestBody UploadFileRequest request) {
        log.debug("[d] Uploading file: {}", request);
        return fileService.upload(request);
    }

    @DeleteMapping("/{id}")
    public DeleteFileResponse delete(@PathVariable String id) {
        log.debug("[d] Deleting file by id '{}'", id);
        return fileService.delete(id);
    }

    @PostMapping("/{id}/tags")
    public AssignTagsResponse assignTags(@PathVariable String id, @Validated @RequestBody AssignTagsRequest request) {
        log.debug("[d] Assigning tags({}) to file with id '{}'", request.getTags(), id);
        return fileService.assignTags(id, request);
    }

    @DeleteMapping("/{id}/tags")
    public RemoveTagsResponse removeTags(@PathVariable String id, @Validated @RequestBody RemoveTagsRequest request) {
        log.debug("[d] Removing tags({}) for file with id '{}'", request.getTags(), id);
        return fileService.removeTags(id, request);
    }

    @GetMapping
    public GetFilesByFilterResponse getAllByFilter(FileFilter filter, Pageable pageable) {
        log.debug("[d] Finding files by filter: {}", filter);
        return fileService.getAllByFilter(filter, pageable);
    }

    @GetMapping(value = "/all")
    public GetAllFilesResponse getAll() {
        log.debug("[d] Getting all files");
        return fileService.getAllFilesWithoutPagebale();
    }

    @GetMapping(value = "/{id}")
    public GetDocumentByIdResponse GetDocumentById(@PathVariable String id) {
        log.debug("[d] Getting file by {}", id);
        return fileService.getDocumentById(id);
    }
}
