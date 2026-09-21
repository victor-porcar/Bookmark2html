package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.backup.ZipBookmarkBackup;
import com.github.victormpcmun.bookmark2html.finder.FolderFinder;
import com.github.victormpcmun.bookmark2html.reader.ChromeBookmarkReader;
import com.github.victormpcmun.bookmark2html.render.PrettyHtmlRenderer;
import com.github.victormpcmun.bookmark2html.writer.FileOutputWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookmarkExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesThePrettyHtmlOfTheRequestedFolderAndBacksUpTheBookmarks() throws IOException {
        Path output = tempDir.resolve("nested/bookmarks.html");
        Path backupDirectory = tempDir.resolve("backup");
        Arguments arguments = new Arguments(TestBookmarks.file(), output, "TECHNICAL", Optional.of(backupDirectory));

        ExportResult result = exporter().export(arguments);

        String html = Files.readString(output);
        assertTrue(html.contains("<h1 class=\"page-title\">TECHNICAL</h1>"));
        assertTrue(html.contains("https://docs.oracle.com/en/java/"));
        Path backupFile = result.backupFile().orElseThrow();
        assertEquals(backupDirectory, backupFile.getParent());
        assertTrue(Files.exists(backupFile));
    }

    @Test
    void makesNoBackupWithoutBackupDirectory() throws IOException {
        Path output = tempDir.resolve("bookmarks.html");
        Arguments arguments = new Arguments(TestBookmarks.file(), output, "TECHNICAL", Optional.empty());

        ExportResult result = exporter().export(arguments);

        assertTrue(Files.exists(output));
        assertEquals(Optional.empty(), result.backupFile());
        try (Stream<Path> files = Files.list(tempDir)) {
            assertEquals(List.of(output), files.toList());
        }
    }

    private BookmarkExporter exporter() {
        return new BookmarkExporter(
                new ChromeBookmarkReader(),
                new FolderFinder(),
                PrettyHtmlRenderer.withDefaultTemplate(domains -> Map.of()),
                new FileOutputWriter(),
                new ZipBookmarkBackup(Clock.systemDefaultZone()));
    }
}
