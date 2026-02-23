package org.example.ec2confi.controller;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.dto.FileResponse;
import org.example.ec2confi.entity.User;
import org.example.ec2confi.repository.UserRepository;
import org.example.ec2confi.service.FileService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(fileService.saveFile(file, user));
    }

    @GetMapping("/my-files")
    public ResponseEntity<List<FileResponse>> listFiles(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(fileService.getUserFiles(user));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Resource resource = fileService.loadFile(id, user);

        // return file to header to know that download file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        fileService.deleteFile(id, user);
        return ResponseEntity.ok("File deleted successfully");
    }
}