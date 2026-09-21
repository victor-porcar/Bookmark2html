package com.github.victormpcmun.bookmark2html.finder;

import com.github.victormpcmun.bookmark2html.ExportException;
import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;

import java.util.Optional;

/**
 * Finds a folder by its exact (case-sensitive) name. When several folders share the
 * name, the first one found walking the tree top-down wins. A blank name selects the
 * root, that is, every bookmark.
 */
public class FolderFinder {

    public BookmarkFolder find(BookmarkFolder root, String folderName) {
        if (folderName.isBlank()) {
            return root;
        }
        return searchInside(root, folderName)
                .orElseThrow(() -> new ExportException("bookmark folder '" + folderName + "' not found"));
    }

    private Optional<BookmarkFolder> search(BookmarkFolder folder, String folderName) {
        return folder.name().equals(folderName) ? Optional.of(folder) : searchInside(folder, folderName);
    }

    private Optional<BookmarkFolder> searchInside(BookmarkFolder parent, String folderName) {
        return parent.subfolders().stream()
                .map(subfolder -> search(subfolder, folderName))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
