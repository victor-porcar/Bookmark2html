package com.github.victormpcmun.bookmark2html.backup;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Backup file names: original_bookmarks_yyyyMMdd_HHmmss.zip. Being timestamps, sorting
 * them alphabetically also sorts them from oldest to newest.
 */
final class BackupFileName {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Pattern BACKUP_NAME = Pattern.compile("original_bookmarks_\\d{8}_\\d{6}\\.zip");

    private BackupFileName() {
    }

    static String of(LocalDateTime dateTime) {
        return "original_bookmarks_" + dateTime.format(TIMESTAMP) + ".zip";
    }

    static boolean isBackup(Path file) {
        return BACKUP_NAME.matcher(file.getFileName().toString()).matches();
    }
}
