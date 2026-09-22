package com.github.victormpcmun.bookmark2html;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookmarksFileLocatorTest {

    @TempDir
    Path tempDir;

    @Test
    void takesTheFirstCandidateThatExists() throws IOException {
        Path accountBookmarks = Files.createFile(tempDir.resolve("AccountBookmarks"));
        Path bookmarks = Files.createFile(tempDir.resolve("Bookmarks"));

        assertEquals(accountBookmarks, BookmarksFileLocator.firstExisting(accountBookmarks + ";" + bookmarks));
        assertEquals(bookmarks, BookmarksFileLocator.firstExisting(bookmarks + ";" + accountBookmarks));
    }

    @Test
    void skipsTheCandidatesThatDoNotExist() throws IOException {
        Path bookmarks = Files.createFile(tempDir.resolve("Bookmarks"));

        assertEquals(bookmarks, BookmarksFileLocator.firstExisting(tempDir.resolve("AccountBookmarks") + ";" + bookmarks));
    }

    @Test
    void aSingleCandidateStillWorks() {
        assertEquals(TestBookmarks.file(), BookmarksFileLocator.firstExisting(TestBookmarks.file().toString()));
    }

    @Test
    void aProfileDirectoryStandsForTheBookmarksFileInside() {
        Path profileDirectory = TestBookmarks.file().getParent();

        assertEquals(profileDirectory.resolve("Bookmarks"), BookmarksFileLocator.firstExisting(profileDirectory.toString()));
    }

    @Test
    void ignoresSpacesAndEmptyCandidates() throws IOException {
        Path bookmarks = Files.createFile(tempDir.resolve("Bookmarks"));

        assertEquals(bookmarks, BookmarksFileLocator.firstExisting(" ; " + bookmarks + " ;"));
    }

    @Test
    void reportsEveryCandidateTriedWhenNoneExists() {
        Path first = tempDir.resolve("AccountBookmarks");
        Path second = tempDir.resolve("Bookmarks");

        ExportException error = assertThrows(ExportException.class,
                () -> BookmarksFileLocator.firstExisting(first + ";" + second));

        assertEquals("no Chrome bookmarks file found, tried: " + first + "; " + second, error.getMessage());
    }

    @Test
    void anEmptyListIsAnError() {
        ExportException error = assertThrows(ExportException.class, () -> BookmarksFileLocator.firstExisting(" ; "));

        assertTrue(error.getMessage().contains("no Chrome bookmarks file given"));
    }
}
