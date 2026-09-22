package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * What an export produced: the folder that was rendered and, unless no backup was asked for,
 * the backup of the bookmarks file and the old backups deleted to stay within the number to keep.
 */
public record ExportResult(BookmarkFolder exportedFolder, Optional<Path> backupFile, List<Path> deletedBackups) {

    static ExportResult withoutBackup(BookmarkFolder exportedFolder) {
        return new ExportResult(exportedFolder, Optional.empty(), List.of());
    }

    static ExportResult withBackup(BookmarkFolder exportedFolder, Path backupFile, List<Path> deletedBackups) {
        return new ExportResult(exportedFolder, Optional.of(backupFile), deletedBackups);
    }
}
