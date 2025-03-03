package dev.lschen.cookit.media;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class MediaService {
    private final String UPLOAD_DIR = "backend/src/main/resources/public/uploads/";

    public String uploadMedia(MultipartFile media, MediaCategory mediaCategory) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR + mediaCategory.getValue());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + media.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(media.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public void deleteMedia(String imageFileName, String folder) {
        String imagePath = UPLOAD_DIR + folder + imageFileName;
        Path path = Paths.get(imagePath);

        try {
            Files.deleteIfExists(path);
            log.info("Image deleted: {}", imagePath);
        } catch (IOException e) {
            log.error("Failed to delete image: {}", imagePath);
            e.printStackTrace();
        }
    }
}
