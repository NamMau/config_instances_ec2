package org.example.ec2confi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    private String message;
    private FileResponse fileInfo;
}