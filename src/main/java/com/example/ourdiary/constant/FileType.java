package com.example.ourdiary.constant;

import java.nio.file.Path;
import java.time.LocalDate;

public enum FileType {
    PROFILE_IMAGE(Path.of("file/profile-image/" + LocalDate.now())),
    POST_IMAGE(Path.of("file/post-image/" + LocalDate.now()));

    private final Path path;

    FileType(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }


}
