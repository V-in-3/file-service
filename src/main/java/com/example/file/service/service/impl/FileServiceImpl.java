package com.example.file.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.file.service.dto.*;
import com.example.file.service.filter.FileFilter;
import com.example.file.service.mapper.FileMapper;
import com.example.file.service.model.File;
import com.example.file.service.repository.FileRepository;
import com.example.file.service.service.FileService;
import com.example.file.service.web.exceptions.FileNotFoundException;
import com.example.file.service.web.exceptions.TagNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public GetFilesByFilterResponse getAllByFilter(FileFilter filter, Pageable pageable) {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();
        // add tags
        if (filter != null && filter.getTags() != null && !filter.getTags().isEmpty()) {
            query.withQuery(QueryBuilders.termsQuery("tags", filter.getTags()));
        }
        // add pageable
        query.withPageable(pageable != null ? pageable : PageRequest.of(0, 10));

        // add q filter
        if (filter != null && filter.getQ() != null && StringUtils.isNoneBlank(filter.getQ())) {
            query.withQuery(QueryBuilders.wildcardQuery("name", "*" + filter.getQ() + "*").caseInsensitive(true));
        }

        SearchHits<File> searchHits = elasticsearchRestTemplate.search(query.build(), File.class);
        SearchPage<File> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
        Page<File> page = (Page<File>) SearchHitSupport.unwrapSearchHits(searchPage);
        return page != null ? new GetFilesByFilterResponse(page.getTotalElements(), page.getContent()) : new GetFilesByFilterResponse(0L, List.of());

    }

    @Override
    public GetAllFilesResponse getAllFilesWithoutPagebale() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        SearchRequest searchRequest = new SearchRequest("file");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final List<File> files = Stream.of(Objects.requireNonNull(searchResponse).getHits()
                        .getHits())
                .map(hit -> JSON.parseObject(hit.getSourceAsString(), File.class))
                .collect(Collectors.toList());

        return new GetAllFilesResponse(files);
    }

    @Override
    public GetDocumentByIdResponse getDocumentById(String id) {
        File file = fileRepository.findById(id).get();
        return new GetDocumentByIdResponse(file);
    }
}