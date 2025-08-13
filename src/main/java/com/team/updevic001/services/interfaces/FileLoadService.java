package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileLoadService {

    FileUploadResponse uploadFile(MultipartFile multipartFile, Long id,String photoOfWhat) throws IOException;

    String getFileUrl(String key);

    void deleteFileFromAws(String key);



}
