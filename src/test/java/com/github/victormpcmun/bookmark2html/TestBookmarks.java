package com.github.victormpcmun.bookmark2html;

import java.net.URISyntaxException;
import java.nio.file.Path;

public final class TestBookmarks {

    private TestBookmarks() {
    }

    /** The sample Chrome "Bookmarks" file in src/test/resources. */
    public static Path file() {
        try {
            return Path.of(TestBookmarks.class.getClassLoader().getResource("Bookmarks").toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
