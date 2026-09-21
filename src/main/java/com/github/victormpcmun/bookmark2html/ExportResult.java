package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;

import java.nio.file.Path;
import java.util.Optional;

/**
 * What an export produced: the folder that was rendered and, when one was asked for,
 * the backup of the bookmarks file.
 */
public record ExportResult(BookmarkFolder exportedFolder, Optional<Path> backupFile) {
}
