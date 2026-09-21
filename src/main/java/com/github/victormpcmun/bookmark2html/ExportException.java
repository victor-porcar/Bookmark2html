package com.github.victormpcmun.bookmark2html;

/**
 * Any problem that prevents the export, with a message meant for the user.
 */
public class ExportException extends RuntimeException {

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
