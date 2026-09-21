package com.github.victormpcmun.bookmark2html.model;

import java.util.List;
import java.util.stream.Stream;

public record BookmarkFolder(String name, List<BookmarkNode> children) implements BookmarkNode {

    public BookmarkFolder {
        children = List.copyOf(children);
    }

    public List<BookmarkLink> links() {
        return children.stream()
                .filter(BookmarkLink.class::isInstance)
                .map(BookmarkLink.class::cast)
                .toList();
    }

    public List<BookmarkFolder> subfolders() {
        return children.stream()
                .filter(BookmarkFolder.class::isInstance)
                .map(BookmarkFolder.class::cast)
                .toList();
    }

    /** Links in this folder and in all its subfolders. */
    public List<BookmarkLink> allLinks() {
        return Stream.concat(links().stream(), subfolders().stream().flatMap(folder -> folder.allLinks().stream()))
                .toList();
    }

    public long countLinks() {
        return allLinks().size();
    }

    /** Subfolders at any depth, not counting this folder. */
    public long countSubfolders() {
        return subfolders().size() + subfolders().stream().mapToLong(BookmarkFolder::countSubfolders).sum();
    }
}
