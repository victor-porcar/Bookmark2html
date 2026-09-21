package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.finder.FolderFinder;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.reader.BookmarkReader;
import com.github.victormpcmun.bookmark2html.render.BookmarkRenderer;
import com.github.victormpcmun.bookmark2html.writer.OutputWriter;

/**
 * Orchestrates the export: read all bookmarks, pick one folder, render it and write it.
 */
public class BookmarkExporter {

    private final BookmarkReader reader;
    private final FolderFinder finder;
    private final BookmarkRenderer renderer;
    private final OutputWriter writer;

    public BookmarkExporter(BookmarkReader reader, FolderFinder finder,
                            BookmarkRenderer renderer, OutputWriter writer) {
        this.reader = reader;
        this.finder = finder;
        this.renderer = renderer;
        this.writer = writer;
    }

    public BookmarkFolder export(Arguments arguments) {
        BookmarkFolder allBookmarks = reader.read(arguments.bookmarksFile());
        BookmarkFolder folder = finder.find(allBookmarks, arguments.folderName());
        writer.write(arguments.outputFile(), renderer.render(folder));
        return folder;
    }
}
