package com.github.victormpcmun.bookmark2html.reader;

import com.github.victormpcmun.bookmark2html.ExportException;
import com.github.victormpcmun.bookmark2html.TestBookmarks;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.model.BookmarkLink;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChromeBookmarkReaderTest {

    private final ChromeBookmarkReader reader = new ChromeBookmarkReader();

    @Test
    void readsEveryRootFolder() {
        BookmarkFolder root = reader.read(TestBookmarks.file());

        List<String> rootNames = root.subfolders().stream().map(BookmarkFolder::name).toList();

        assertEquals("All bookmarks", root.name());
        assertEquals(List.of("Bookmarks bar", "Other bookmarks", "Mobile bookmarks"), rootNames);
    }

    @Test
    void readsLinksAndNestedFolders() {
        BookmarkFolder bookmarkBar = reader.read(TestBookmarks.file()).subfolders().get(0);

        assertEquals(List.of(new BookmarkLink("News", "https://news.example.com/")), bookmarkBar.links());
        assertEquals(3, bookmarkBar.countLinks());
        assertEquals(3, bookmarkBar.countSubfolders());
    }

    @Test
    void missingFileIsReportedAsExportException() {
        assertThrows(ExportException.class, () -> reader.read(Path.of("does-not-exist")));
    }
}
