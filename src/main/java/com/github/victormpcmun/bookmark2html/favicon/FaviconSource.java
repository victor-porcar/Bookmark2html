package com.github.victormpcmun.bookmark2html.favicon;

import java.util.Map;
import java.util.Set;

public interface FaviconSource {

    /**
     * Favicons of the given domains as "data:" URIs, ready to embed in an img tag.
     * Domains whose favicon is not available are simply left out of the map.
     */
    Map<String, String> faviconsFor(Set<String> domains);
}
