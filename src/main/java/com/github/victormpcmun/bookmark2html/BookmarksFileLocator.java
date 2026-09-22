package com.github.victormpcmun.bookmark2html;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Finds the Chrome bookmarks file among several candidate paths separated by ';', taking the first
 * one that exists. Chrome has changed where it keeps bookmarks (Bookmarks, AccountBookmarks...), so
 * listing every possible location keeps working whatever the Chrome version.
 * A candidate may be the file itself or a profile directory, meaning the Bookmarks file inside it.
 */
final class BookmarksFileLocator {

    static final String CANDIDATE_SEPARATOR = ";";
    static final String CHROME_BOOKMARKS_FILE = "Bookmarks";

    private BookmarksFileLocator() {
    }

    static Path firstExisting(String candidates) {
        List<Path> paths = candidatePaths(candidates);
        if (paths.isEmpty()) {
            throw new ExportException("no Chrome bookmarks file given");
        }
        return paths.stream()
                .filter(Files::isRegularFile)
                .findFirst()
                .orElseThrow(() -> new ExportException("no Chrome bookmarks file found, tried: " + joined(paths)));
    }

    private static List<Path> candidatePaths(String candidates) {
        return Arrays.stream(candidates.split(CANDIDATE_SEPARATOR))
                .map(String::strip)
                .filter(candidate -> !candidate.isEmpty())
                .map(BookmarksFileLocator::pathOf)
                .map(BookmarksFileLocator::bookmarksFileOf)
                .toList();
    }

    private static Path bookmarksFileOf(Path path) {
        return Files.isDirectory(path) ? path.resolve(CHROME_BOOKMARKS_FILE) : path;
    }

    private static Path pathOf(String candidate) {
        try {
            return Path.of(candidate);
        } catch (InvalidPathException e) {
            throw new ExportException("invalid path: '" + candidate + "'", e);
        }
    }

    private static String joined(List<Path> paths) {
        return paths.stream().map(Path::toString).collect(Collectors.joining(CANDIDATE_SEPARATOR + " "));
    }
}
