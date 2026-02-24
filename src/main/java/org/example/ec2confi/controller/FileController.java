package org.example.ec2confi.controller;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.dto.FileResponse;
import org.example.ec2confi.entity.User;
import org.example.ec2confi.repository.UserRepository;
import org.example.ec2confi.service.FileService;
import org.springframework.http.MediaType;
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
    public ResponseEntity<?> upload(@RequestParam("files") MultipartFile[] files, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //check if array empty
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No files selected");
        }
        //return ResponseEntity.ok(fileService.saveFile(file, user));
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileService.saveFile(file, user);
            }
        }
        return ResponseEntity.ok("Upload success " + files.length + " file");
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        fileService.deleteFile(id, user);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<?> previewFile(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get Resource from service
        Resource resource = fileService.loadFile(id, user);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(400)
                    .body("{\"message\":\"File not readable or found on server storage\"}");
        }

        String contentType = "application/octet-stream";
        try {
            // Automatically determine the file type (image, pdf, text, etc.) based on the actual file.
            //contentType =
            java.nio.file.Path path = java.nio.file.Path.of(resource.getURI());
            contentType = java.nio.file.Files.probeContentType(path);
                    //java.nio.file.Path.of(resource.getFile().getAbsolutePath())
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

        } catch (IOException e) {
            System.err.println("Could not determine file type " + e.getMessage());
        }

        return ResponseEntity.ok()
                //.header(HttpHeaders.CONTENT_TYPE, contentType)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}