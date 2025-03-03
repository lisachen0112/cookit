package dev.lschen.cookit.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaCategory {
    COVER("cover"),
    INSTRUCTIONS("instructions");

    private final String value;
}
