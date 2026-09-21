package com.github.victormpcmun.bookmark2html.render;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A text with {{name}} placeholders. All placeholders are replaced in a single pass,
 * so a value that happens to contain "{{...}}" is never expanded again.
 */
public class PageTemplate {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)}}");

    private final String text;

    public PageTemplate(String text) {
        this.text = text;
    }

    public String fill(Map<String, String> values) {
        return PLACEHOLDER.matcher(text)
                .replaceAll(match -> Matcher.quoteReplacement(valueOf(values, match.group(1))));
    }

    private static String valueOf(Map<String, String> values, String name) {
        String value = values.get(name);
        if (value == null) {
            throw new IllegalStateException("no value for template placeholder {{" + name + "}}");
        }
        return value;
    }
}
