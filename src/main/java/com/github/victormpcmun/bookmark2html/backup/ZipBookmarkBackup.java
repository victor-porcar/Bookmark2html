package com.github.victormpcmun.bookmark2html.backup;

import com.github.victormpcmun.bookmark2html.ExportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Backs up the bookmarks file as original_bookmarks_yyyyMMdd_HHmmss.zip, creating the
 * backup directory when needed. Inside the zip the file keeps its original name.
 */
public class ZipBookmarkBackup implements BookmarkBackup {

    private final Clock clock;

    public ZipBookmarkBackup(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Path backup(Path bookmarksFile, Path backupDirectory) {
        Path zipFile = backupDirectory.resolve(BackupFileName.of(LocalDateTime.now(clock)));
        try {
            Files.createDirectories(backupDirectory);
            writeZip(bookmarksFile, zipFile);
            return zipFile;
        } catch (IOException e) {
            throw new ExportException("cannot back up the bookmarks file into " + zipFile, e);
        }
    }

    private void writeZip(Path file, Path zipFile) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            zip.putNextEntry(new ZipEntry(file.getFileName().toString()));
            Files.copy(file, zip);
            zip.closeEntry();
        }
    }
}
