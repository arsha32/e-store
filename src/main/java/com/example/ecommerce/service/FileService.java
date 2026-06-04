package com.example.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    String UploadImage(String path, MultipartFile image) throws IOException {
        String imageName=image.getOriginalFilename();

        String uniqueID= UUID.randomUUID().toString();

        String generateImageName = uniqueID+imageName.substring(imageName.lastIndexOf("."));

        String filePath = path+ File.separator+generateImageName;

        File folder = new File(path);
        if(!folder.exists())
        {
            folder.mkdir();
        }

        Files.copy(image.getInputStream(), Paths.get(filePath));
        return generateImageName;
    }

}
