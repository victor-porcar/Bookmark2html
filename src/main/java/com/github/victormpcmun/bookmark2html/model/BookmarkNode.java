package com.github.victormpcmun.bookmark2html.model;

/**
 * An entry of the bookmark tree: either a folder or a link.
 */
public sealed interface BookmarkNode permits BookmarkFolder, BookmarkLink {

    String name();
}
