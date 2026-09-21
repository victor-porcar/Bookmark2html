package com.github.victormpcmun.bookmark2html;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsTest {

    @TempDir
    Path tempDir;

    @Test
    void readsTheFourArguments() {
        Path file = TestBookmarks.file();

        Arguments arguments = parse(file.toString(), output(), "TECHNICAL", backup());

        assertEquals(file, arguments.bookmarksFile());
        assertEquals(Path.of(output()), arguments.outputFile());
        assertEquals("TECHNICAL", arguments.folderName());
        assertEquals(Optional.of(Path.of(backup())), arguments.backupDirectory());
    }

    @Test
    void missingBackupDirectoryMeansNoBackup() {
        Arguments arguments = Arguments.parse(new String[]{"Bookmarks", output(), "TECHNICAL"});

        assertEquals(Optional.empty(), arguments.backupDirectory());
    }

    @Test
    void emptyBackupDirectoryMeansNoBackup() {
        assertEquals(Optional.empty(), parse("Bookmarks", output(), "TECHNICAL", "").backupDirectory());
    }

    @Test
    void directoryArgumentPointsToItsBookmarksFile() {
        Path profileDirectory = TestBookmarks.file().getParent();

        Arguments arguments = parse(profileDirectory.toString(), output(), "TECHNICAL", backup());

        assertEquals(profileDirectory.resolve("Bookmarks"), arguments.bookmarksFile());
    }

    @Test
    void keywordMeansEveryBookmark() {
        assertEquals("", parse("Bookmarks", output(), "ALL_EXISTING_BOOKMARKS", backup()).folderName());
    }

    @Test
    void emptyFolderNameIsRejected() {
        ExportException error = assertThrows(ExportException.class,
                () -> parse("Bookmarks", output(), " ", backup()));

        assertEquals("folder name is empty; use ALL_EXISTING_BOOKMARKS to export every bookmark", error.getMessage());
    }

    @Test
    void folderNameIsMandatory() {
        assertThrows(ExportException.class, () -> Arguments.parse(new String[]{"Bookmarks", output()}));
    }

    @Test
    void outputFileMustBeAFullPath() {
        ExportException error = assertThrows(ExportException.class,
                () -> parse("Bookmarks", "out.html", "TECHNICAL", backup()));

        assertEquals("output html file must be a full path: 'out.html'", error.getMessage());
    }

    @Test
    void backupDirectoryMustBeAFullPath() {
        assertThrows(ExportException.class, () -> parse("Bookmarks", output(), "TECHNICAL", "backup"));
    }

    @Test
    void wrongNumberOfArgumentsIsRejected() {
        assertThrows(ExportException.class, () -> Arguments.parse(new String[]{"only-one"}));
        assertThrows(ExportException.class, () -> Arguments.parse(new String[]{"1", "2", "3", "4", "5"}));
    }

    private Arguments parse(String bookmarks, String output, String folder, String backup) {
        return Arguments.parse(new String[]{bookmarks, output, folder, backup});
    }

    private String output() {
        return tempDir.resolve("out.html").toString();
    }

    private String backup() {
        return tempDir.resolve("backup").toString();
    }
}
