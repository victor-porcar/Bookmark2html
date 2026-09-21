package com.github.victormpcmun.bookmark2html;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Command line arguments: bookmarks location, output html file and folder to export.
 * An empty folder name means every bookmark.
 */
public record Arguments(Path bookmarksFile, Path outputFile, String folderName) {

    static final String CHROME_BOOKMARKS_FILE = "Bookmarks";
    static final String ALL_FOLDERS = "";

    private static final String USAGE = """
            expected 2 or 3 arguments
              1. Chrome profile directory (or the Bookmarks file itself)
              2. Output html file
              3. Name of the bookmark folder to export (optional, empty or missing exports everything)
            example:
              java -jar bookmark2html.jar "C:\\Users\\me\\AppData\\Local\\Google\\Chrome\\User Data\\Default" bookmarks.html TECHNICAL""";

    public static Arguments parse(String[] args) {
        if (args.length < 2 || args.length > 3) {
            throw new ExportException(USAGE);
        }
        return new Arguments(resolveBookmarksFile(Path.of(args[0])), Path.of(args[1]), folderNameOf(args));
    }

    private static Path resolveBookmarksFile(Path path) {
        return Files.isDirectory(path) ? path.resolve(CHROME_BOOKMARKS_FILE) : path;
    }

    private static String folderNameOf(String[] args) {
        return args.length == 3 ? args[2].strip() : ALL_FOLDERS;
    }
}
