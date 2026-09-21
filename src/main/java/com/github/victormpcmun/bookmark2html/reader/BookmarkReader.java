package com.github.victormpcmun.bookmark2html.reader;

import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;

import java.nio.file.Path;

public interface BookmarkReader {

    /** Reads every bookmark in the file, grouped under a single root folder. */
    BookmarkFolder read(Path bookmarksFile);
}
