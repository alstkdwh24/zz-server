package com.example.zzserver.config.handler;

import com.example.zzserver.accommodation.dto.request.FileMetadata;
import com.example.zzserver.accommodation.entity.AccommodationImages;
import com.example.zzserver.rooms.entity.RoomImages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class FileHandler {

    @Value("${file.upload-dir}")
    private String uploadDir; // 예: ./uploads

    @Value("${file.access-base-url}")
    private String accessBaseUrl; // 예: http://localhost:8080/uploads

    private static final Set<String> SUPPORTED_MIME = Set.of("image/jpeg", "image/png");

    public List<FileMetadata> uploadFiles(List<MultipartFile> files, String subDirectory) {
        if (CollectionUtils.isEmpty(files)) return Collections.emptyList();

        List<FileMetadata> result = new ArrayList<>();

        String targetDirPath = uploadDir + File.separator + subDirectory;
        File dir = new File(targetDirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) throw new RuntimeException("업로드 디렉토리 생성 실패: " + targetDirPath);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (!SUPPORTED_MIME.contains(contentType)) {
                log.warn("지원하지 않는 파일 타입: {}", contentType);
                continue;
            }

            String ext = getExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "." + ext;
            String fullPath = targetDirPath + File.separator + storedName;

            try {
                file.transferTo(new File(fullPath));
                result.add(FileMetadata.builder()
                        .originalName(file.getOriginalFilename())
                        .storedName(storedName)
                        .fullPath(fullPath)
                        .accessUrl(accessBaseUrl + "/" + subDirectory + "/" + storedName)
                        .size(file.getSize())
                        .contentType(contentType)
                        .build());
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
            }
        }

        return result;
    }

    /**
     * 저장된 AccommodationImages 목록에서 로컬 파일 삭제
     */
    public void deleteAccommodationFiles(List<AccommodationImages> images) {
        for (AccommodationImages image : images) {
            try {
                String fullPath = resolveFilePathFromUrl(image.getImageUrl());
                File file = new File(fullPath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    log.info("파일 삭제: {} → {}", fullPath, deleted);
                } else {
                    log.warn("삭제 대상 파일이 존재하지 않음: {}", fullPath);
                }
            } catch (Exception e) {
                log.error("파일 삭제 중 예외 발생: {}", image.getImageUrl(), e);
            }
        }
    }

    /**
     * 저장된 RoomsImages 목록에서 로컬 파일 삭제
     */
    public void deleteRoomFiles(List<RoomImages> images) {
        for(RoomImages image: images) {
            try {
                String fullPath = resolveFilePathFromUrl(image.getImageUrl());
                File file = new File(fullPath);
                if(file.exists()) {
                    boolean deleted = file.delete();
                    log.info("파일 삭제: {} → {}", fullPath, deleted);
                } else {
                    log.warn("삭제 대상 파일이 존재하지 않음: {}", fullPath);
                }
            } catch (Exception e) {
                log.error("파일 삭제 중 예외 발생: {}", image.getImageUrl(), e);
            }
        }

    }

    /**
     * 접근 URL로부터 실제 로컬 경로 변환
     */
    private String resolveFilePathFromUrl(String accessUrl) {
        if (accessUrl == null || !accessUrl.contains(accessBaseUrl)) {
            throw new IllegalArgumentException("잘못된 접근 URL: " + accessUrl);
        }

        String relativePath = accessUrl.replace(accessBaseUrl, "").replace("/", File.separator);
        return uploadDir + relativePath;
    }

    private String getExtension(String filename) {
        int dotIdx = filename.lastIndexOf('.');
        if (dotIdx == -1) return ""; // 확장자 없음
        return filename.substring(dotIdx + 1);
    }
}
