package org.example.ec2confi.service;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.dto.FileResponse;
import org.example.ec2confi.entity.FileMetadata;
import org.example.ec2confi.entity.User;
import org.example.ec2confi.repository.FileRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalFileService implements FileService {

    private final FileRepository fileRepository;

    // into k8s
    private final Path rootLocation = Paths.get("uploads");

    @Override
    public FileResponse saveFile(MultipartFile file, User user) throws IOException {
        Path userPath = this.rootLocation.resolve(user.getUsername());
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = userPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFileType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setFilePath(filePath.toString());
        metadata.setUploadDate(LocalDateTime.now());
        metadata.setOwner(user);

        FileMetadata saved = fileRepository.save(metadata);
        return mapToResponse(saved);
    }

    @Override
    public List<FileResponse> getUserFiles(User user) {
        return fileRepository.findByOwner(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Resource loadFile(Long fileId, User user) {
        try {
            FileMetadata metadata = fileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (metadata.getOwner().getId() != user.getId()) {
                throw new RuntimeException("Access denied");
            }

            Path file = Paths.get(metadata.getFilePath());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not readable");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL error: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(Long fileId, User user) throws IOException {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (metadata.getOwner().getId() != user.getId()) {
            throw new RuntimeException("Access denied");
        }

        Path path = Paths.get(metadata.getFilePath());
        Files.deleteIfExists(path);
        fileRepository.delete(metadata);
    }

    private FileResponse mapToResponse(FileMetadata metadata) {
        FileResponse res = new FileResponse();
        res.setId(metadata.getId());
        res.setFileName(metadata.getFileName());
        res.setFileType(metadata.getFileType());
        res.setFileSizeReadable(formatFileSize(metadata.getFileSize()));
        res.setUploadDate(metadata.getUploadDate());
        res.setDownloadUrl("/api/files/download/" + metadata.getId());
        return res;
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        if (size < 1024) return size + " B";

        double k = size / 1024.0;
        if (k < 1024) return String.format("%.1f KB", k);

        double m = k / 1024.0;
        if (m < 1024) return String.format("%.1f MB", m);

        double g = m / 1024.0;
        return String.format("%.1f GB", g);
    }
}