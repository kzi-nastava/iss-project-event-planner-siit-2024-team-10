package com.ftn.iss.eventPlanner.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/data/";

    public List<String> saveFiles(MultipartFile[] files) throws IOException {
        List<String> fileNames = new ArrayList<>();

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String extension = "";
                String originalName = file.getOriginalFilename();
                int dotIndex = originalName.lastIndexOf('.');
                if (dotIndex >= 0) {
                    extension = originalName.substring(dotIndex);
                }

                String uniqueFileName = UUID.randomUUID() + extension;
                String filePath = UPLOAD_DIR + uniqueFileName;

                File dest = new File(filePath);
                file.transferTo(dest);

                fileNames.add(uniqueFileName);
            }
        }

        return fileNames;
    }
}
