package com.newstickr.newstickr.user.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j

public class FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;
//    private final String defaultImgUrl = "src/main/resources/static/defaultProfileImg.svg";

    @PostConstruct
    public void init(){
        File uploadDirectory = new File(uploadDir);
        if(!uploadDirectory.exists()){
            boolean created = uploadDirectory.mkdirs();
            if(!created){
                log.info("업로드 디렉토리가 생성되었습니다:{}", uploadDir);
            }
            else {
                log.error("업로드 디렉토리 생성에 실패했습니다:{}", uploadDir);
            }
        }
    }
    public Resource getImg(String fileName){
        try{
            Path path = Paths.get(uploadDir + fileName);
            Resource resource = new UrlResource(path.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
        }catch (Exception e){
            // [TODO]UrlResource에서 발생하는 예외 분석 필요.
            log.error(e.getMessage());
        }
        return getDefaultImage();
    }
    public ResponseEntity<Resource> downloadImg(String fileName){
        Resource resource = getImg(fileName);

        if(resource.exists() || resource.isReadable()) {
            String contentDisposition = "attachment; filename=\""+resource.getFilename()+"\"";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
    public String uploadImg(MultipartFile file) {
        //확장자는 자유 추후 결정
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = UUID.randomUUID() + "." + fileExtension;
        String filePath = uploadDir + fileName;
        File targetFile = new File(filePath);
        if(!targetFile.getParentFile().exists()){
            targetFile.getParentFile().mkdirs();
        }
        try {
            file.transferTo(targetFile);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
        return fileName;
    }

    private String getFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex) : "";
    }

    private Resource getDefaultImage(){
        try{
            return new ClassPathResource("static/defaultProfileImg.svg");
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
