package dev.lschen.cookit.instruction;

public record InstructionRequest(
        Integer orderIndex,
        ContentType type,
        String content
) {
}
