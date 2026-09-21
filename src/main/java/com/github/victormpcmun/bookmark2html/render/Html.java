package com.github.victormpcmun.bookmark2html.render;

final class Html {

    private Html() {
    }

    /** Makes text safe to place inside html elements and quoted attributes. */
    static String escape(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
