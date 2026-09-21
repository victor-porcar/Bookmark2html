package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.backup.BackupRotation;
import com.github.victormpcmun.bookmark2html.backup.BookmarkBackup;
import com.github.victormpcmun.bookmark2html.finder.FolderFinder;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.reader.BookmarkReader;
import com.github.victormpcmun.bookmark2html.render.BookmarkRenderer;
import com.github.victormpcmun.bookmark2html.writer.OutputWriter;

import java.nio.file.Path;
import java.util.List;

/**
 * Orchestrates the export: read all bookmarks, pick one folder, render it and write it,
 * then back up the bookmarks file that was used and delete the backups that are too old.
 */
public class BookmarkExporter {

    private final BookmarkReader reader;
    private final FolderFinder finder;
    private final BookmarkRenderer renderer;
    private final OutputWriter writer;
    private final BookmarkBackup backup;
    private final BackupRotation rotation;

    public BookmarkExporter(BookmarkReader reader, FolderFinder finder, BookmarkRenderer renderer,
                            OutputWriter writer, BookmarkBackup backup, BackupRotation rotation) {
        this.reader = reader;
        this.finder = finder;
        this.renderer = renderer;
        this.writer = writer;
        this.backup = backup;
        this.rotation = rotation;
    }

    public ExportResult export(Arguments arguments) {
        BookmarkFolder allBookmarks = reader.read(arguments.bookmarksFile());
        BookmarkFolder folder = finder.find(allBookmarks, arguments.folderName());
        writer.write(arguments.outputFile(), renderer.render(folder));
        Path backupFile = backup.backup(arguments.bookmarksFile(), arguments.backupDirectory());
        List<Path> deleted = rotation.keepNewest(arguments.backupDirectory(), backupFile, arguments.backupsToKeep());
        return new ExportResult(folder, backupFile, deleted);
    }
}
