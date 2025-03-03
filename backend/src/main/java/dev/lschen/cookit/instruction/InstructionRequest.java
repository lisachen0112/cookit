package dev.lschen.cookit.instruction;

import org.springframework.web.multipart.MultipartFile;

public record InstructionRequest(
        Integer orderIndex,
        ContentType type,
        String content,
        MultipartFile media
) {
}
