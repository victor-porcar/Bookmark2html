package com.github.victormpcmun.bookmark2html;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsTest {

    @TempDir
    Path tempDir;

    @Test
    void readsTheFiveArguments() {
        Path file = TestBookmarks.file();

        Arguments arguments = parse(file.toString(), output(), "TECHNICAL", backup(), "5");

        assertEquals(file, arguments.bookmarksFile());
        assertEquals(Path.of(output()), arguments.outputFile());
        assertEquals("TECHNICAL", arguments.folderName());
        assertEquals(Path.of(backup()), arguments.backupDirectory());
        assertEquals(5, arguments.backupsToKeep());
    }

    @Test
    void directoryArgumentPointsToItsBookmarksFile() {
        Path profileDirectory = TestBookmarks.file().getParent();

        Arguments arguments = parse(profileDirectory.toString(), output(), "TECHNICAL", backup(), "5");

        assertEquals(profileDirectory.resolve("Bookmarks"), arguments.bookmarksFile());
    }

    @Test
    void keywordMeansEveryBookmark() {
        assertEquals("", parse("Bookmarks", output(), "ALL_EXISTING_BOOKMARKS", backup(), "5").folderName());
    }

    @Test
    void emptyFolderNameIsRejected() {
        ExportException error = assertThrows(ExportException.class,
                () -> parse("Bookmarks", output(), " ", backup(), "5"));

        assertEquals("folder name is empty; use ALL_EXISTING_BOOKMARKS to export every bookmark", error.getMessage());
    }

    @Test
    void outputFileMustBeAFullPath() {
        ExportException error = assertThrows(ExportException.class,
                () -> parse("Bookmarks", "out.html", "TECHNICAL", backup(), "5"));

        assertEquals("output html file must be a full path: 'out.html'", error.getMessage());
    }

    @Test
    void backupDirectoryMustBeAFullPath() {
        assertThrows(ExportException.class, () -> parse("Bookmarks", output(), "TECHNICAL", "backup", "5"));
    }

    @Test
    void oneBackupToKeepIsTheMinimum() {
        assertEquals(1, parse("Bookmarks", output(), "TECHNICAL", backup(), "1").backupsToKeep());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-3", "five", "2.5", ""})
    void backupsToKeepMustBeAWholeNumberOfAtLeastOne(String value) {
        ExportException error = assertThrows(ExportException.class,
                () -> parse("Bookmarks", output(), "TECHNICAL", backup(), value));

        assertEquals("number of backups to keep must be a whole number of at least 1: '" + value + "'",
                error.getMessage());
    }

    @Test
    void everyArgumentIsMandatory() {
        assertThrows(ExportException.class,
                () -> Arguments.parse(new String[]{"Bookmarks", output(), "TECHNICAL", backup()}));
        assertThrows(ExportException.class,
                () -> Arguments.parse(new String[]{"Bookmarks", output(), "TECHNICAL", backup(), "5", "6"}));
    }

    private Arguments parse(String bookmarks, String output, String folder, String backup, String backupsToKeep) {
        return Arguments.parse(new String[]{bookmarks, output, folder, backup, backupsToKeep});
    }

    private String output() {
        return tempDir.resolve("out.html").toString();
    }

    private String backup() {
        return tempDir.resolve("backup").toString();
    }
}
