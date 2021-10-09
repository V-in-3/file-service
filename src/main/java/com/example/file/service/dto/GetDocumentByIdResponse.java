package com.example.file.service.dto;

import com.example.file.service.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentByIdResponse {
    private String id;
    private String name;
    private Long size;
    private Set<String> tags;

    public GetDocumentByIdResponse(File file){
        this.id = file.getId();
        this.name = file.getName();
        this.size = file.getSize();
        this.tags = file.getTags();

    }
}
