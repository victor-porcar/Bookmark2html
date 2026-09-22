package com.github.victormpcmun.bookmark2html.render;

import com.github.victormpcmun.bookmark2html.model.BookmarkFolder;
import com.github.victormpcmun.bookmark2html.model.BookmarkLink;
import com.github.victormpcmun.bookmark2html.model.BookmarkNode;

import java.util.Map;
import java.util.stream.Collectors;

import static com.github.victormpcmun.bookmark2html.render.Html.escape;

/**
 * Renders the content of a folder as an indented tree, keeping the order of Chrome.
 * Every subfolder is a collapsible entry whose content hangs one level to the right.
 */
class BookmarkTreeHtml {

    private static final String TREE = """
            <ul class="tree">
            %s
            </ul>""";

    private static final String FOLDER = """
            <li class="folder" id="%1$s">
              <details open>
                <summary class="folder-header">
                  <span class="folder-name">%2$s</span>
                  <span class="folder-count">%3$d</span>
                  <a class="folder-anchor" href="#%1$s" title="Link to this folder" aria-label="Link to this folder">#</a>
                </summary>
            %4$s
              </details>
            </li>""";

    private static final String LINK = """
            <li class="link">
              <a href="%s" target="_blank" rel="noopener noreferrer">
                %s
                <span class="link-title">%s</span>
                <span class="link-domain">%s</span>
              </a>
            </li>""";

    private static final String FAVICON = "<img class=\"link-icon\" src=\"%s\" alt=\"\">";
    private static final String NO_FAVICON = "<span class=\"link-icon\"></span>";

    /** Favicon "data:" URI by domain. */
    private final Map<String, String> favicons;
    private final FolderAnchors anchors = new FolderAnchors();

    BookmarkTreeHtml(Map<String, String> favicons) {
        this.favicons = favicons;
    }

    String render(BookmarkFolder folder) {
        return tree(folder, "");
    }

    /** @param anchor anchor of the folder whose content is rendered, empty for the page folder */
    private String tree(BookmarkFolder folder, String anchor) {
        return TREE.formatted(folder.children().stream()
                .map(node -> node(node, anchor))
                .collect(Collectors.joining("\n")));
    }

    private String node(BookmarkNode node, String parentAnchor) {
        return node instanceof BookmarkFolder folder ? folder(folder, parentAnchor) : link((BookmarkLink) node);
    }

    private String folder(BookmarkFolder folder, String parentAnchor) {
        String anchor = anchors.anchorFor(parentAnchor, folder.name());
        return FOLDER.formatted(escape(anchor), escape(folder.name()), folder.countLinks(), tree(folder, anchor));
    }

    private String link(BookmarkLink link) {
        return LINK.formatted(
                escape(link.url()),
                favicon(link),
                escape(link.displayName()),
                escape(link.domain()));
    }

    /** An empty slot keeps the titles aligned when there is no favicon. */
    private String favicon(BookmarkLink link) {
        String dataUri = favicons.get(link.domain());
        return dataUri == null ? NO_FAVICON : FAVICON.formatted(escape(dataUri));
    }
}
