package com.example.file.service.service;

import com.example.file.service.dto.*;
import com.example.file.service.filter.FileFilter;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface FileService {

    UploadFileResponse upload(UploadFileRequest request);

    DeleteFileResponse delete(String id);

    AssignTagsResponse assignTags(String id, AssignTagsRequest request);

    RemoveTagsResponse removeTags(String id, RemoveTagsRequest request);

    GetFilesByFilterResponse getAllByFilter(FileFilter filter, Pageable pageable);

    GetAllFilesResponse getAllFilesWithoutPagebale() throws IOException;
}