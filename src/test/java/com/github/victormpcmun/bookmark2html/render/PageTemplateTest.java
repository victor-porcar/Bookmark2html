package com.github.victormpcmun.bookmark2html.render;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageTemplateTest {

    @Test
    void replacesEveryPlaceholder() {
        PageTemplate template = new PageTemplate("<h1>{{title}}</h1><p>{{title}} by {{author}}</p>");

        String text = template.fill(Map.of("title", "Hi", "author", "me"));

        assertEquals("<h1>Hi</h1><p>Hi by me</p>", text);
    }

    @Test
    void valuesAreNeverExpandedAgain() {
        PageTemplate template = new PageTemplate("{{a}}{{b}}");

        String text = template.fill(Map.of("a", "{{b}} $1 \\", "b", "x"));

        assertEquals("{{b}} $1 \\x", text);
    }

    @Test
    void missingValueIsAnError() {
        PageTemplate template = new PageTemplate("{{unknown}}");

        assertThrows(IllegalStateException.class, () -> template.fill(Map.of()));
    }
}
