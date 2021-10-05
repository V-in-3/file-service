package com.example.file.service.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileRequest {

    @Pattern(regexp = "^[a-zA-Z0-9._ -]+\\.(gif|png|jpg|jpeg|mp3|mp4|doc|docx|pdf|csv|xls)$", message = "filename is not valid")
    private String name;

    @Min(value = 0L, message = "value must be positive")
    private Long size;
}