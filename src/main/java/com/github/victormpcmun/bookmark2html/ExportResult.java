package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;

import java.nio.file.Path;
import java.util.List;

/**
 * What an export produced: the folder that was rendered, the backup of the bookmarks file
 * and the old backups deleted to stay within the number to keep.
 */
public record ExportResult(BookmarkFolder exportedFolder, Path backupFile, List<Path> deletedBackups) {
}
