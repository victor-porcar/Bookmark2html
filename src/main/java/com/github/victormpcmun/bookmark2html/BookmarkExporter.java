package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.backup.BookmarkBackup;
import com.github.victormpcmun.bookmark2html.finder.FolderFinder;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.reader.BookmarkReader;
import com.github.victormpcmun.bookmark2html.render.BookmarkRenderer;
import com.github.victormpcmun.bookmark2html.writer.OutputWriter;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Orchestrates the export: read all bookmarks, pick one folder, render it and write it,
 * then back up the bookmarks file that was used when a backup directory was given.
 */
public class BookmarkExporter {

    private final BookmarkReader reader;
    private final FolderFinder finder;
    private final BookmarkRenderer renderer;
    private final OutputWriter writer;
    private final BookmarkBackup backup;

    public BookmarkExporter(BookmarkReader reader, FolderFinder finder, BookmarkRenderer renderer,
                            OutputWriter writer, BookmarkBackup backup) {
        this.reader = reader;
        this.finder = finder;
        this.renderer = renderer;
        this.writer = writer;
        this.backup = backup;
    }

    public ExportResult export(Arguments arguments) {
        BookmarkFolder allBookmarks = reader.read(arguments.bookmarksFile());
        BookmarkFolder folder = finder.find(allBookmarks, arguments.folderName());
        writer.write(arguments.outputFile(), renderer.render(folder));
        return new ExportResult(folder, backUp(arguments));
    }

    private Optional<Path> backUp(Arguments arguments) {
        return arguments.backupDirectory()
                .map(directory -> backup.backup(arguments.bookmarksFile(), directory));
    }
}
