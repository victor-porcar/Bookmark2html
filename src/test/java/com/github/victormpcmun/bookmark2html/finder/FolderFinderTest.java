package com.github.victormpcmun.bookmark2html.finder;

import com.github.victormpcmun.bookmark2html.ExportException;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.model.BookmarkLink;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FolderFinderTest {

    private final FolderFinder finder = new FolderFinder();

    private final BookmarkFolder deep = new BookmarkFolder("Target",
            List.of(new BookmarkLink("a", "https://a.com")));
    private final BookmarkFolder shallow = new BookmarkFolder("Target", List.of());
    private final BookmarkFolder root = new BookmarkFolder("All bookmarks", List.of(
            new BookmarkFolder("Bar", List.of(new BookmarkFolder("Nested", List.of(deep)))),
            new BookmarkFolder("Other", List.of(shallow))));

    @Test
    void findsTheFirstFolderWalkingTopDown() {
        assertSame(deep, finder.find(root, "Target"));
    }

    @Test
    void blankNameSelectsTheRootWithEveryBookmark() {
        assertSame(root, finder.find(root, ""));
    }

    @Test
    void rootIsNeverMatchedByName() {
        assertThrows(ExportException.class, () -> finder.find(root, "All bookmarks"));
    }

    @Test
    void nameIsCaseSensitive() {
        assertThrows(ExportException.class, () -> finder.find(root, "target"));
    }

    @Test
    void unknownFolderIsReported() {
        ExportException error = assertThrows(ExportException.class, () -> finder.find(root, "Nope"));

        assertEquals("bookmark folder 'Nope' not found", error.getMessage());
    }
}
