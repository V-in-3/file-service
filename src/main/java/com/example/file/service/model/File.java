package com.example.file.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "file")
public class File {
    @Id
    private String id;
    @Field(type = FieldType.Text, name = "name")
    private String name;
    @Field(type = FieldType.Long, name = "size")
    private Long size;
    @Field(type = FieldType.Auto, name = "tags")
    private List<String> tags;

    public void addTags(List<String> tags) {
        if (this.getTags() != null) {
            this.getTags().addAll(tags);
        } else {
            this.setTags(tags);
        }
    }

    public boolean existsTags() {
        return this.getTags() != null && !this.getTags().isEmpty();
    }
}