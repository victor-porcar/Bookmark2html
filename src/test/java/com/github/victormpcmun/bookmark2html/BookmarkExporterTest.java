package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.finder.FolderFinder;
import com.github.victormpcmun.bookmark2html.reader.ChromeBookmarkReader;
import com.github.victormpcmun.bookmark2html.render.PrettyHtmlRenderer;
import com.github.victormpcmun.bookmark2html.writer.FileOutputWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BookmarkExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesThePrettyHtmlOfTheRequestedFolder() throws IOException {
        Path output = tempDir.resolve("nested/bookmarks.html");
        Arguments arguments = new Arguments(TestBookmarks.file(), output, "TECHNICAL");

        exporter().export(arguments);

        String html = Files.readString(output);
        assertTrue(html.contains("<h1 class=\"page-title\">TECHNICAL</h1>"));
        assertTrue(html.contains("https://docs.oracle.com/en/java/"));
    }

    private BookmarkExporter exporter() {
        return new BookmarkExporter(
                new ChromeBookmarkReader(),
                new FolderFinder(),
                PrettyHtmlRenderer.withDefaultTemplate(domains -> Map.of()),
                new FileOutputWriter());
    }
}
