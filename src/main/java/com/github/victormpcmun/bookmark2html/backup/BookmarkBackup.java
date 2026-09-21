package com.github.victormpcmun.bookmark2html.backup;

import java.nio.file.Path;

public interface BookmarkBackup {

    /** Saves a copy of the bookmarks file inside the backup directory and returns the copy. */
    Path backup(Path bookmarksFile, Path backupDirectory);
}
