package com.example.file.service.mapper;

import com.example.file.service.dto.UploadFileRequest;
import com.example.file.service.model.File;
import org.mapstruct.*;

import java.util.List;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = RETURN_DEFAULT,
        nullValueCheckStrategy = ALWAYS,
        nullValuePropertyMappingStrategy = IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class FileMapper {

    public static final List<String> VIDEO_FILE_EXTENSIONS = List.of("mp4");
    public static final List<String> AUDIO_FILE_EXTENSIONS = List.of("mp3");
    public static final List<String> DOCUMENT_FILE_EXTENSIONS = List.of("doc", "docx", "pdf", "csv", "xls");
    public static final List<String> IMAGE_FILE_EXTENSIONS = List.of("gif", "png", "jpg", "jpeg");

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "size", source = "request.size")
    @Mapping(target = "tags", qualifiedByName = "toTags", source = "request")
    public abstract File toFile(String id, UploadFileRequest request);

    @Named("toTags")
    public List<String> createTags(UploadFileRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            String[] splitFileName = request.getName().split("\\.");
            if (splitFileName.length == 2) {
                Tag tag = getTagByFileExtension(splitFileName[1]);
                return tag != null ? List.of(tag.name()) : null;
            }
        }
        return null;
    }

    private Tag getTagByFileExtension(String fileExt) {
        if (fileExt != null) {
            if (VIDEO_FILE_EXTENSIONS.contains(fileExt)) {
                return Tag.video;
            } else if (AUDIO_FILE_EXTENSIONS.contains(fileExt)) {
                return Tag.audio;
            } else if (DOCUMENT_FILE_EXTENSIONS.contains(fileExt)) {
                return Tag.document;
            } else if (IMAGE_FILE_EXTENSIONS.contains(fileExt)) {
                return Tag.image;
            } else {
                return null;
            }
        }
        return null;
    }

    private enum Tag {audio, video, document, image}
}
