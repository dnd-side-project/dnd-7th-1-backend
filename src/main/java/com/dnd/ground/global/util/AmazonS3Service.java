package com.dnd.ground.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @description AWS S3 관련 기능 구현
 * @author  박찬호
 * @since   2022.09.07
 * @updated 1. S3 업로드 및 삭제 기능 구현
 *          - 2022.09.07 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class AmazonS3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Value("${cloud.aws.s3.filePath}")
    private String filePath;

    /*S3에 파일 업로드*/
    public Map<String, String> uploadToS3(MultipartFile file, String path, String name) {
        String fileName = path + "/" + name; //S3에 저장될 파일 이름

        //사진에 대한 정보 추가
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        //S3에 업로드
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }

        Map<String, String> fileInfo = new HashMap<>();
        fileInfo.put("fileName", fileName);
        fileInfo.put("filePath", "https://"+filePath+fileName);

        return fileInfo;
    }

    /*S3의 파일 삭제*/
    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
