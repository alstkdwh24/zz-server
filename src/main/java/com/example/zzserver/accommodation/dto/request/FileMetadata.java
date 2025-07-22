package com.example.zzserver.accommodation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {

    private String originalName;
    private String storedName;
    private String fullPath;        // 실제 저장된 경로
    private String accessUrl;       // 정적 URL 접근용
    private long size;
    private String contentType;
}
