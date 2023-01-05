package com.global.project.RestController.Public;

import com.global.project.Repository.FileRepository;
import com.global.project.Utils.Const;
import com.global.project.Utils.FileUtils;
import com.global.project.Utils.MediaTypeUtils;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/public/api/file")
public class FilePublicController {
    @Autowired
    private ServletContext servletContext;
    @Autowired
    FileUtils fileUtils;
    @Autowired
    FileRepository fileRepository;

    @GetMapping(value = "/{fileName}")
    @Secured({Const.ROLE_SYSTEM})
    public ResponseEntity<InputStreamResource> fileUpload(@PathVariable(value = "fileName")String fileName) throws IOException {
        String nameDisplay = fileRepository.findFileByName(fileName);
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
        File file = new File(fileUtils.getPathFile() + fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + nameDisplay)
                .contentType(mediaType)
                .contentLength(file.length())
                .body(resource);
    }
    @DeleteMapping(value = "/{fileName}")
    public Boolean deleteImage(@PathVariable(value = "fileName")String filename) {
        return fileUtils.deleteFile(filename);
    }
}
