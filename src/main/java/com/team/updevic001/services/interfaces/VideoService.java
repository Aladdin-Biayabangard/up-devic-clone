package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.response.video.PhotoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VideoService {

    String uploadVideo(MultipartFile multipartFile, String title) throws IOException;

    PhotoUploadResponse uploadPhoto(MultipartFile multipartFile, Long id) throws IOException;

    String getVideoUrl(String key);

}
