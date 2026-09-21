package com.github.victormpcmun.bookmark2html.backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackupRotationTest {

    @TempDir
    Path tempDir;

    private final BackupRotation rotation = new BackupRotation();

    @Test
    void keepsTheNewestCountingTheOneJustMade() throws IOException {
        backups("20260101_000000", "20260102_000000", "20260103_000000", "20260104_000000");
        Path justMade = backup("20260105_000000");

        List<Path> deleted = rotation.keepNewest(tempDir, justMade, 3);

        assertEquals(List.of(file("20260102_000000"), file("20260101_000000")), deleted);
        assertEquals(List.of(file("20260103_000000"), file("20260104_000000"), justMade), remaining());
    }

    @Test
    void deletesNothingWhileUnderTheLimit() throws IOException {
        backups("20260101_000000");
        Path justMade = backup("20260102_000000");

        assertEquals(List.of(), rotation.keepNewest(tempDir, justMade, 5));
        assertEquals(2, remaining().size());
    }

    @Test
    void keepingOneLeavesOnlyTheOneJustMade() throws IOException {
        backups("20260101_000000", "20260102_000000");
        Path justMade = backup("20260103_000000");

        rotation.keepNewest(tempDir, justMade, 1);

        assertEquals(List.of(justMade), remaining());
    }

    @Test
    void neverDeletesTheOneJustMadeEvenIfOthersLookNewer() throws IOException {
        Path futureDated = backup("20990101_000000");
        Path justMade = backup("20260101_000000");

        List<Path> deleted = rotation.keepNewest(tempDir, justMade, 1);

        assertEquals(List.of(futureDated), deleted);
        assertEquals(List.of(justMade), remaining());
    }

    @Test
    void leavesAloneFilesThatAreNotBackups() throws IOException {
        Path notes = Files.createFile(tempDir.resolve("notes.txt"));
        Path similar = Files.createFile(tempDir.resolve("original_bookmarks_old.zip"));
        Path subfolder = Files.createDirectories(tempDir.resolve("original_bookmarks_20200101_000000.zip.d"));
        Path nested = Files.createFile(subfolder.resolve("original_bookmarks_20200101_000000.zip"));
        Path justMade = backup("20260101_000000");

        assertEquals(List.of(), rotation.keepNewest(tempDir, justMade, 1));
        assertTrue(Files.exists(notes));
        assertTrue(Files.exists(similar));
        assertTrue(Files.exists(nested));
    }

    private void backups(String... timestamps) throws IOException {
        for (String timestamp : timestamps) {
            backup(timestamp);
        }
    }

    private Path backup(String timestamp) throws IOException {
        return Files.createFile(file(timestamp));
    }

    private Path file(String timestamp) {
        return tempDir.resolve("original_bookmarks_" + timestamp + ".zip");
    }

    private List<Path> remaining() throws IOException {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.sorted().toList();
        }
    }
}
