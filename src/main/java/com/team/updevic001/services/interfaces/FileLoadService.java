package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileLoadService {

    FileUploadResponse uploadFileWithEncode(MultipartFile multipartFile, String id, String keyOfWhat) throws IOException;

    FileUploadResponse uploadFile(MultipartFile multipartFile, String id, String photoOfWhat) throws IOException;

    String getFileUrlWithEncode(String key);

    String getPublicFileUrl(String key);

    void deleteFileFromAws(String key);

}
