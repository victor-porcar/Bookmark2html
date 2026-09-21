package com.github.victormpcmun.bookmark2html.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

final class Resources {

    private Resources() {
    }

    /** Reads a text file packaged inside the jar. */
    static String read(String name) {
        try (InputStream input = open(name)) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read resource " + name, e);
        }
    }

    private static InputStream open(String name) {
        InputStream input = Resources.class.getClassLoader().getResourceAsStream(name);
        if (input == null) {
            throw new IllegalStateException("resource not found: " + name);
        }
        return input;
    }
}
