package dev.lschen.cookit.recipe;

import dev.lschen.cookit.instruction.InstructionRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public record RecipeRequest (
        @NotNull(message = "Title is required")
        @NotEmpty(message = "Title is required")
        String title,
        String description,
        MultipartFile coverImage,
        String videoUrl,
        List<String> ingredients,
        List<InstructionRequest> instructions
) {
}
