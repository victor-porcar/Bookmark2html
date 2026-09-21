package com.github.victormpcmun.bookmark2html.backup;

import com.github.victormpcmun.bookmark2html.ExportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Deletes the oldest backups of a directory so that only the given number remain.
 * Only files named like a backup are considered, subfolders are not looked into, and the
 * backup just made is always kept, whatever the names of the others.
 */
public class BackupRotation {

    /** Keeps the newest backups, counting the one just made, and returns the deleted ones. */
    public List<Path> keepNewest(Path directory, Path justMade, int backupsToKeep) {
        List<Path> older = otherBackupsNewestFirst(directory, justMade);
        List<Path> surplus = older.subList(Math.min(backupsToKeep - 1, older.size()), older.size());
        surplus.forEach(BackupRotation::delete);
        return List.copyOf(surplus);
    }

    private static List<Path> otherBackupsNewestFirst(Path directory, Path justMade) {
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(Files::isRegularFile)
                    .filter(BackupFileName::isBackup)
                    .filter(file -> !file.equals(justMade))
                    .sorted(Comparator.comparing((Path file) -> file.getFileName().toString()).reversed())
                    .toList();
        } catch (IOException e) {
            throw new ExportException("cannot list the backups in " + directory, e);
        }
    }

    private static void delete(Path backup) {
        try {
            Files.delete(backup);
        } catch (IOException e) {
            throw new ExportException("cannot delete the old backup " + backup, e);
        }
    }
}
