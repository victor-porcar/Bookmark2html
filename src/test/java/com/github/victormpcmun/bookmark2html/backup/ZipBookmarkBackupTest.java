package com.github.victormpcmun.bookmark2html.backup;

import com.github.victormpcmun.bookmark2html.TestBookmarks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ZipBookmarkBackupTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-09-21T18:05:09Z"), ZoneOffset.UTC);

    @TempDir
    Path tempDir;

    private final ZipBookmarkBackup backup = new ZipBookmarkBackup(FIXED_CLOCK);

    @Test
    void namesTheZipWithTheDateAndTime() {
        Path zipFile = backup.backup(TestBookmarks.file(), tempDir);

        assertEquals(tempDir.resolve("original_bookmarks_20260921_180509.zip"), zipFile);
    }

    @Test
    void createsTheBackupDirectoryWhenMissing() {
        Path backupDirectory = tempDir.resolve("does/not/exist");

        Path zipFile = backup.backup(TestBookmarks.file(), backupDirectory);

        assertEquals(backupDirectory, zipFile.getParent());
    }

    @Test
    void zipHoldsTheBookmarksFileUnchanged() throws IOException {
        Path zipFile = backup.backup(TestBookmarks.file(), tempDir);

        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry = zip.getNextEntry();
            assertEquals("Bookmarks", entry.getName());
            assertArrayEquals(Files.readAllBytes(TestBookmarks.file()), zip.readAllBytes());
            assertNull(zip.getNextEntry());
        }
    }
}
