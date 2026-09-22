package com.github.victormpcmun.bookmark2html.render;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Gives every folder of a page an anchor made of its name and the names of its ancestors,
 * for example "java/spring-boot". Two folders that would get the same anchor are told apart
 * with a numeric suffix: "java", "java-2".
 */
class FolderAnchors {

    private static final String SEPARATOR = "/";
    private static final String FALLBACK = "folder";
    private static final Pattern ACCENTS = Pattern.compile("\\p{M}+");
    private static final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_DASHES = Pattern.compile("^-|-$");

    private final Set<String> used = new HashSet<>();

    /** @param parentAnchor anchor of the parent folder, empty for a folder at the top of the page */
    String anchorFor(String parentAnchor, String folderName) {
        String base = parentAnchor.isEmpty() ? slug(folderName) : parentAnchor + SEPARATOR + slug(folderName);
        String anchor = base;
        for (int copy = 2; !used.add(anchor); copy++) {
            anchor = base + "-" + copy;
        }
        return anchor;
    }

    static String slug(String name) {
        String unaccented = ACCENTS.matcher(Normalizer.normalize(name, Normalizer.Form.NFD)).replaceAll("");
        String dashed = NOT_ALPHANUMERIC.matcher(unaccented.toLowerCase(Locale.ROOT)).replaceAll("-");
        String slug = EDGE_DASHES.matcher(dashed).replaceAll("");
        return slug.isEmpty() ? FALLBACK : slug;
    }
}
