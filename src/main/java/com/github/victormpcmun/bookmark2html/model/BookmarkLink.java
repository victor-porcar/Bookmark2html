package com.github.victormpcmun.bookmark2html.model;

import java.net.URI;

public record BookmarkLink(String name, String url) implements BookmarkNode {

    private static final String WWW_PREFIX = "www.";

    public String displayName() {
        return name.isBlank() ? url : name;
    }

    /** Host of the url without the "www." prefix, or empty when there is no host. */
    public String domain() {
        String host = hostOf(url);
        return host.startsWith(WWW_PREFIX) ? host.substring(WWW_PREFIX.length()) : host;
    }

    private static String hostOf(String url) {
        try {
            String host = URI.create(url).getHost();
            return host == null ? "" : host;
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
