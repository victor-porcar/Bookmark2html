package com.github.victormpcmun.bookmark2html;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Command line arguments: bookmarks location, output html file, folder to export, the directory
 * where the Chrome bookmarks file is backed up and how many backups are kept there.
 * The keyword ALL_EXISTING_BOOKMARKS is turned into an empty folder name, which means every bookmark.
 */
public record Arguments(Path bookmarksFile, Path outputFile, String folderName,
                        Path backupDirectory, int backupsToKeep) {

    static final String CHROME_BOOKMARKS_FILE = "Bookmarks";
    static final String ALL_BOOKMARKS_KEYWORD = "ALL_EXISTING_BOOKMARKS";
    static final String ALL_BOOKMARKS = "";

    private static final int EXPECTED_ARGUMENTS = 5;

    private static final String USAGE = """
            expected 5 arguments
              1. Chrome profile directory (or the Bookmarks file itself)
              2. Output html file, as a full path
              3. Name of the bookmark folder to export (ALL_EXISTING_BOOKMARKS exports every bookmark)
              4. Directory where the Chrome bookmarks file is backed up as a zip, as a full path
              5. Number of backups to keep in that directory, counting the new one; older ones are deleted
            example:
              java -jar bookmark2html.jar "C:\\Users\\me\\AppData\\Local\\Google\\Chrome\\User Data\\Default" \
            "D:\\bookmarks\\bookmarks.html" TECHNICAL "D:\\bookmarks\\backup" 5""";

    public static Arguments parse(String[] args) {
        if (args.length != EXPECTED_ARGUMENTS) {
            throw new ExportException(USAGE);
        }
        return new Arguments(
                resolveBookmarksFile(pathOf(args[0])),
                fullPathOf(args[1], "output html file"),
                folderNameOf(args[2]),
                fullPathOf(args[3], "backup directory"),
                backupsToKeepOf(args[4]));
    }

    private static int backupsToKeepOf(String value) {
        try {
            int backupsToKeep = Integer.parseInt(value.strip());
            if (backupsToKeep >= 1) {
                return backupsToKeep;
            }
        } catch (NumberFormatException e) {
            // reported below, like any other invalid number
        }
        throw new ExportException("number of backups to keep must be a whole number of at least 1: '" + value + "'");
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
