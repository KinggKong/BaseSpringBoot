package com.global.project.restController;

import com.global.project.repository.FileRepository;
import com.global.project.utils.Const;
import com.global.project.utils.FileUtils;
import com.global.project.utils.MediaTypeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "02. FILE")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/file")
public class RestFileController {
    @Autowired
    private ServletContext servletContext;
    @Autowired
    FileUtils fileUtils;
    @Autowired
    FileRepository fileRepository;

    @Operation(summary = "get file by name", description = "get file by name", tags = {"02. FILE"})
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

    @Operation(summary = "delete file by name", description = "delete file by name", tags = {"02. FILE"})
    @DeleteMapping(value = "/{fileName}")
    public Boolean deleteImage(@PathVariable(value = "fileName")String filename) {
        return fileUtils.deleteFile(filename);
    }
}
