package com.example.file.service.service.impl;

import com.example.file.service.dto.*;
import com.example.file.service.filter.FileFilter;
import com.example.file.service.mapper.FileMapper;
import com.example.file.service.model.File;
import com.example.file.service.repository.FileRepository;
import com.example.file.service.service.FileService;
import com.example.file.service.web.exceptions.FileNotFoundException;
import com.example.file.service.web.exceptions.TagNotFoundException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final FileRepository fileRepository;
    private final RestHighLevelClient client;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public FileServiceImpl(FileMapper fileMapper, FileRepository fileRepository, RestHighLevelClient client, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.fileMapper = fileMapper;
        this.fileRepository = fileRepository;
        this.client = client;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    public UploadFileResponse upload(UploadFileRequest request) {
        String id = UUID.randomUUID().toString();
        fileRepository.save(fileMapper.toFile(id, request));
        return new UploadFileResponse(id);
    }

    @Override
    public DeleteFileResponse delete(String id) {
        fileRepository.findById(id).orElseThrow(FileNotFoundException::new);
        fileRepository.deleteById(id);
        return new DeleteFileResponse(true);
    }

    @Override
    public AssignTagsResponse assignTags(String id, AssignTagsRequest request) {
        File file = fileRepository.findById(id).orElseThrow(FileNotFoundException::new);
        file.addTags(request.getTags());
        fileRepository.save(file);
        return new AssignTagsResponse(true);
    }

    @Override
    public RemoveTagsResponse removeTags(String id, RemoveTagsRequest request) {
        File file = fileRepository.findById(id).orElseThrow(FileNotFoundException::new);
        if (file.existsTags() && file.getTags().containsAll(request.getTags())) {
            file.getTags().removeAll(request.getTags());
            fileRepository.save(file);
        } else {
            throw new TagNotFoundException();
        }
        return new RemoveTagsResponse(true);
    }

    @SuppressWarnings("unchecked")
    // todo: It needs to be done
    @Override
    public GetFilesByFilterResponse getAllByFilter(FileFilter filter, Pageable pageable) {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();
        // add tags
        if (filter != null && filter.getTags() != null && !filter.getTags().isEmpty()) {
            query.withQuery(QueryBuilders.termsQuery("tags", filter.getTags()));
        }
        // add pageable
        query.withPageable(pageable != null ? pageable : PageRequest.of(0, 10));

        // todo add q filter
        //if (filter != null && filter.getQ() != null && !filter.getQ().isBlank()) {}

        SearchHits<File> searchHits = elasticsearchRestTemplate.search(query.build(), File.class);
        SearchPage<File> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
        Page<File> page = (Page<File>) SearchHitSupport.unwrapSearchHits(searchPage);
        return page != null ? new GetFilesByFilterResponse(page.getTotalElements(), page.getContent()) : new GetFilesByFilterResponse(0L, List.of());

    }

    @Override
    public GetAllFilesResponse getAllFilesWithoutPagebale() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        SearchRequest searchRequest = new SearchRequest("file");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<File> files = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            File file = new File();
            file.setId((String) sourceAsMap.get("id"));
            file.setName((String) sourceAsMap.get("name"));
            file.setSize(Long.valueOf((Integer) sourceAsMap.get("size")));
            file.setTags((List<String>) sourceAsMap.get("tags"));
            files.add(file);
        }
        return new GetAllFilesResponse(files);
    }
}