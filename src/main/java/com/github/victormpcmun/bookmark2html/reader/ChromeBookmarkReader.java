package com.github.victormpcmun.bookmark2html.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victormpcmun.bookmark2html.ExportException;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.model.BookmarkLink;
import com.github.victormpcmun.bookmark2html.model.BookmarkNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads the JSON "Bookmarks" file that Chrome keeps inside every profile directory.
 */
public class ChromeBookmarkReader implements BookmarkReader {

    private static final String ROOT_NAME = "All bookmarks";
    private static final String TYPE_FOLDER = "folder";
    private static final String TYPE_URL = "url";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public BookmarkFolder read(Path bookmarksFile) {
        JsonNode roots = readJson(bookmarksFile).path("roots");
        return new BookmarkFolder(ROOT_NAME, toNodes(roots));
    }

    private JsonNode readJson(Path file) {
        try {
            return mapper.readTree(file.toFile());
        } catch (IOException e) {
            throw new ExportException("cannot read Chrome bookmarks file " + file.toAbsolutePath(), e);
        }
    }

    private List<BookmarkNode> toNodes(JsonNode container) {
        return elements(container)
                .filter(this::isSupported)
                .map(this::toNode)
                .toList();
    }

    private BookmarkNode toNode(JsonNode node) {
        return isFolder(node) ? toFolder(node) : toLink(node);
    }

    private BookmarkFolder toFolder(JsonNode node) {
        return new BookmarkFolder(node.path("name").asText(), toNodes(node.path("children")));
    }

    private BookmarkLink toLink(JsonNode node) {
        return new BookmarkLink(node.path("name").asText(), node.path("url").asText());
    }

    private boolean isSupported(JsonNode node) {
        return isFolder(node) || TYPE_URL.equals(typeOf(node));
    }

    private boolean isFolder(JsonNode node) {
        return TYPE_FOLDER.equals(typeOf(node));
    }

    private String typeOf(JsonNode node) {
        return node.path("type").asText();
    }

    private static Stream<JsonNode> elements(JsonNode container) {
        return StreamSupport.stream(container.spliterator(), false);
    }
}
