package com.github.victormpcmun.bookmark2html.writer;

import com.github.victormpcmun.bookmark2html.ExportException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes UTF-8 text files, creating the parent directories when needed.
 */
public class FileOutputWriter implements OutputWriter {

    @Override
    public void write(Path file, String content) {
        try {
            createParentDirectories(file);
            Files.writeString(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ExportException("cannot write output file " + file.toAbsolutePath(), e);
        }
    }

    private void createParentDirectories(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
