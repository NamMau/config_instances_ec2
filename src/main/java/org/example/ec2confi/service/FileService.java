package org.example.ec2confi.service;

import org.example.ec2confi.dto.FileResponse;
import org.example.ec2confi.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    FileResponse saveFile(MultipartFile file, User user) throws IOException;

    List<FileResponse> getUserFiles(User user);

    Resource loadFile(Long fileId, User user);

    void deleteFile(Long fileId, User user) throws IOException;

    void shareFile(Long fileId, String targetUsername, String ownerUsername);
    List<FileResponse> getSharedWithMeFiles(String username);
}