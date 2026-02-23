package org.example.ec2confi.service;

import jakarta.annotation.Resource;
import org.example.ec2confi.entity.FileMetadata;
import org.example.ec2confi.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    FileMetadata saveFile(MultipartFile file, User user) throws IOException;
    List<FileMetadata> getUserFiles(User user);
    Resource loadFile(Long fileId, User user);
}
