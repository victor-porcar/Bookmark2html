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
            <li class="folder">
              <details open>
                <summary class="folder-header">
                  <span class="folder-name">%s</span>
                  <span class="folder-count">%d</span>
                </summary>
            %s
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

    BookmarkTreeHtml(Map<String, String> favicons) {
        this.favicons = favicons;
    }

    String render(BookmarkFolder folder) {
        return TREE.formatted(folder.children().stream()
                .map(this::node)
                .collect(Collectors.joining("\n")));
    }

    private String node(BookmarkNode node) {
        return node instanceof BookmarkFolder folder ? folder(folder) : link((BookmarkLink) node);
    }

    private String folder(BookmarkFolder folder) {
        return FOLDER.formatted(escape(folder.name()), folder.countLinks(), render(folder));
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
