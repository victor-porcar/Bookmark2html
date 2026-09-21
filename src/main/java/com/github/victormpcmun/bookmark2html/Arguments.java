package com.github.victormpcmun.bookmark2html;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Command line arguments: bookmarks location, output html file, folder to export and, optionally,
 * the directory where the Chrome bookmarks file is backed up (no directory, no backup).
 * The keyword ALL_EXISTING_BOOKMARKS is turned into an empty folder name, which means every bookmark.
 */
public record Arguments(Path bookmarksFile, Path outputFile, String folderName, Optional<Path> backupDirectory) {

    static final String CHROME_BOOKMARKS_FILE = "Bookmarks";
    static final String ALL_BOOKMARKS_KEYWORD = "ALL_EXISTING_BOOKMARKS";
    static final String ALL_BOOKMARKS = "";

    private static final int MANDATORY_ARGUMENTS = 3;
    private static final int ALL_ARGUMENTS = 4;

    private static final String USAGE = """
            expected 3 or 4 arguments
              1. Chrome profile directory (or the Bookmarks file itself)
              2. Output html file, as a full path
              3. Name of the bookmark folder to export (ALL_EXISTING_BOOKMARKS exports every bookmark)
              4. Optional: directory where the Chrome bookmarks file is backed up as a zip, as a full path
                 (missing or "" makes no backup)
            example:
              java -jar bookmark2html.jar "C:\\Users\\me\\AppData\\Local\\Google\\Chrome\\User Data\\Default" \
            "D:\\bookmarks\\bookmarks.html" TECHNICAL "D:\\bookmarks\\backup\"""";

    public static Arguments parse(String[] args) {
        if (args.length < MANDATORY_ARGUMENTS || args.length > ALL_ARGUMENTS) {
            throw new ExportException(USAGE);
        }
        return new Arguments(
                resolveBookmarksFile(pathOf(args[0])),
                fullPathOf(args[1], "output html file"),
                folderNameOf(args[2]),
                backupDirectoryOf(args));
    }

    private static Optional<Path> backupDirectoryOf(String[] args) {
        if (args.length < ALL_ARGUMENTS || args[3].isBlank()) {
            return Optional.empty();
        }
        return Optional.of(fullPathOf(args[3], "backup directory"));
    }

    private static String folderNameOf(String value) {
        String folderName = value.strip();
        if (folderName.isEmpty()) {
            throw new ExportException("folder name is empty; use " + ALL_BOOKMARKS_KEYWORD + " to export every bookmark");
        }
        return folderName.equals(ALL_BOOKMARKS_KEYWORD) ? ALL_BOOKMARKS : folderName;
    }

    private static Path resolveBookmarksFile(Path path) {
        return Files.isDirectory(path) ? path.resolve(CHROME_BOOKMARKS_FILE) : path;
    }

    private static Path fullPathOf(String value, String description) {
        Path path = pathOf(value);
        if (!path.isAbsolute()) {
            throw new ExportException(description + " must be a full path: '" + value + "'");
        }
        return path;
    }

    private static Path pathOf(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new ExportException("invalid path: '" + value + "'", e);
        }
    }
}
