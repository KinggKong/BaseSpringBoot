package com.global.project.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FileUtils {
    @Value("${config.path}")
    private String pathFile;
    public String uploadFile(MultipartFile file){
        String relativeFilePath = null;
        String fullUploadDir = pathFile;
        if(!file.isEmpty()){
            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();

            java.io.File checkDir = new java.io.File(fullUploadDir);
            if(checkDir.exists() == true || checkDir.isFile() == true){
                checkDir.mkdir();
            }
            try{
                relativeFilePath = String.valueOf(Instant.now().getEpochSecond()) + file.getOriginalFilename();
                Files.write(Paths.get(fullUploadDir + relativeFilePath), file.getBytes());
            }
            catch (Exception e){
                return null;
            }
        }
        return relativeFilePath;
    }
    public String uploadFile(File file){
        String relativeFilePath = null;
        String fullUploadDir = pathFile;
        if(file.isFile()){
            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();

            java.io.File checkDir = new java.io.File(fullUploadDir);
            if(checkDir.exists() == true || checkDir.isFile() == true){
                checkDir.mkdir();
            }
            try{
                relativeFilePath = String.valueOf(Instant.now().getEpochSecond()) + file.getName();
                Files.write(Paths.get(fullUploadDir + relativeFilePath), file.getName().getBytes());
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }
        }
        return relativeFilePath;
    }
    public Boolean deleteFile(String fileName){
        File myObj = new File(pathFile + fileName);
        if(myObj.delete()){
            return true;
        }
        else{
            return false;
        }
    }

    public String getPathFile(){
        return pathFile;
    }

    public String uploadFileByBytes(byte[] file){
        String relativeFilePath = null;
        String fullUploadDir = pathFile;
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();

        java.io.File checkDir = new java.io.File(fullUploadDir);
        if(checkDir.exists() == true || checkDir.isFile() == true){
            checkDir.mkdir();
        }
        try{
            relativeFilePath = String.valueOf(Instant.now().getEpochSecond());
            Files.write(Paths.get(fullUploadDir + relativeFilePath), file);
        }
        catch (Exception e){
            return null;
        }
        return relativeFilePath;
    }
}
