package com.example.file.service.dto;

import com.example.file.service.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllFilesResponse {
    private List<File> files;
}
