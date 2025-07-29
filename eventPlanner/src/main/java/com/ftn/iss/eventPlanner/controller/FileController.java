package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class FileController {
    @Autowired
    private  FileService fileStorageService;

    @PostMapping
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<String> fileNames = fileStorageService.saveFiles(files);
        return new ResponseEntity<>(fileNames, HttpStatus.CREATED);
    }
}
