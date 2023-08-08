package com.example.ourdiary.file.service;

import com.example.ourdiary.exception.CannotCreateDirectoryException;
import com.example.ourdiary.exception.FileNameNotFoundException;
import com.example.ourdiary.message.MessageService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Validated
public class FileService {
    private final MessageService messageService;

    public FileService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String upload(MultipartFile multipartFile, @NotNull Path path) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        String extension = getExtension(multipartFile);
        String newFileName = UUID.randomUUID() + extension;
        Path targetLocation = path.resolve(newFileName);
        Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Here you need to set your server's url and port
        String serverUrl = "http://localhost:8080";
        return serverUrl + "/" + targetLocation.toString().replace("\\", "/");
    }

    public boolean delete(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    private Path getFullPath(MultipartFile multipartFile, Path path) {
        String filename = createFilename(multipartFile);
        return path.resolve(filename);
    }

    private String createFilename(MultipartFile multipartFile) {
        String extension = getExtension(multipartFile);
        return UUID.randomUUID() + extension;
    }

    private String getExtension(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new FileNameNotFoundException(messageService.get("exception.file.file-name-not-found"));
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private void createDirectoriesIfNotExist(Path path) {
        if (!path.toFile().exists() && (!path.toFile().mkdirs())){
                throw new CannotCreateDirectoryException(messageService.get("exception.file.cannot-create-directory", path.toString()));
        }
    }
}
