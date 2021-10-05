package com.example.file.service.repository;

import com.example.file.service.model.File;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ElasticsearchRepository<File, String> {
}
