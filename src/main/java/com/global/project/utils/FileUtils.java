package com.global.project.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FileUtils {
    private static final String DOMAIN_NAME = "http://test.com/file/";
    private static final String UPLOAD_FOLDER = "C:\\dev\\";

    public String uploadFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String relativeFilePath = Instant.now().getEpochSecond() + file.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_FOLDER, relativeFilePath);

                Files.createDirectories(uploadPath.getParent());

                Files.write(uploadPath, file.getBytes());

                return DOMAIN_NAME + Instant.now().getEpochSecond()+ file.getOriginalFilename() + "?sg=" + file.getOriginalFilename();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public boolean deleteFile(String filename) {
        String[] fileSplit = filename.split("/");
        String fullFilePath = UPLOAD_FOLDER + File.separator + fileSplit[fileSplit.length - 1];
        java.io.File fileToDelete = new java.io.File(fullFilePath);
        if (fileToDelete.exists()) {
            try {
                if (fileToDelete.delete()) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    public String getPathFile(){
        return UPLOAD_FOLDER;
    }
}
