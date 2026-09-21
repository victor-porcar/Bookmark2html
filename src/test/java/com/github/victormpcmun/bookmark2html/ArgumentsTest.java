package com.github.victormpcmun.bookmark2html;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsTest {

    @Test
    void directoryArgumentPointsToItsBookmarksFile() {
        Path profileDirectory = TestBookmarks.file().getParent();

        Arguments arguments = Arguments.parse(new String[]{profileDirectory.toString(), "out.html", "TECHNICAL"});

        assertEquals(profileDirectory.resolve("Bookmarks"), arguments.bookmarksFile());
    }

    @Test
    void fileArgumentIsUsedAsIs() {
        Path file = TestBookmarks.file();

        Arguments arguments = Arguments.parse(new String[]{file.toString(), "out.html", "TECHNICAL"});

        assertEquals(file, arguments.bookmarksFile());
        assertEquals(Path.of("out.html"), arguments.outputFile());
        assertEquals("TECHNICAL", arguments.folderName());
    }

    @Test
    void missingFolderNameMeansEveryBookmark() {
        Arguments arguments = Arguments.parse(new String[]{"Bookmarks", "out.html"});

        assertEquals("", arguments.folderName());
    }

    @Test
    void blankFolderNameMeansEveryBookmark() {
        Arguments arguments = Arguments.parse(new String[]{"Bookmarks", "out.html", "  "});

        assertEquals("", arguments.folderName());
    }

    @Test
    void wrongNumberOfArgumentsIsRejected() {
        assertThrows(ExportException.class, () -> Arguments.parse(new String[]{"only-one"}));
        assertThrows(ExportException.class, () -> Arguments.parse(new String[]{"1", "2", "3", "4"}));
    }
}
