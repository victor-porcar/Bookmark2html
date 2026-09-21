package com.github.victormpcmun.bookmark2html.writer;

import java.nio.file.Path;

public interface OutputWriter {

    void write(Path file, String content);
}
