package com.github.victormpcmun.bookmark2html;

import com.github.victormpcmun.bookmark2html.favicon.GoogleFaviconSource;
import com.github.victormpcmun.bookmark2html.finder.FolderFinder;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.reader.ChromeBookmarkReader;
import com.github.victormpcmun.bookmark2html.render.PrettyHtmlRenderer;
import com.github.victormpcmun.bookmark2html.writer.FileOutputWriter;

public final class Bookmark2html {

    private Bookmark2html() {
    }

    public static void main(String[] args) {
        try {
            run(Arguments.parse(args));
        } catch (ExportException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(Arguments arguments) {
        BookmarkFolder exported = createExporter().export(arguments);
        System.out.printf("Exported %d bookmarks from folder '%s' into %s%n",
                exported.countLinks(), exported.name(), arguments.outputFile().toAbsolutePath());
    }

    private static BookmarkExporter createExporter() {
        return new BookmarkExporter(
                new ChromeBookmarkReader(),
                new FolderFinder(),
                PrettyHtmlRenderer.withDefaultTemplate(new GoogleFaviconSource()),
                new FileOutputWriter());
    }
}
