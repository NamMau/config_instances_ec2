package org.example.ec2confi.controller;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.entity.User;
import org.example.ec2confi.repository.UserRepository;
import org.example.ec2confi.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file")
                                    MultipartFile file, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(fileService.saveFile(file, user));
    }

    @GetMapping("/my-files")
    public ResponseEntity<?> listFiles(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).get();
        return ResponseEntity.ok(fileService.getUserFiles(user));
    }

}
