package com.ftn.iss.eventPlanner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/upload")
public class FileController {

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/data/";

    @PostMapping
    public ResponseEntity<List<String>> uploadFiles(
            @RequestParam("files") MultipartFile[] files) {
        List<String> filePaths = new ArrayList<>();

        try {
            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();  // Create the directory if it doesn't exist
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filePath = UPLOAD_DIR + file.getOriginalFilename();
                    File dest = new File(filePath);
                    file.transferTo(dest);
                    filePaths.add(filePath); // Add the file path to the list
                }
            }

            // Return the list of file paths as response
            return ResponseEntity.ok(filePaths);  // Return the file paths as response
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
