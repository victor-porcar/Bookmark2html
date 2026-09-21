## Bookmark2html

Exports one folder of your Google Chrome bookmarks, or all of them, into a single,
self-contained and pretty HTML page (stylesheet and favicons embedded, works offline, light and
dark mode). Optionally, it also keeps a zipped backup of the Chrome bookmarks file it used.

It reads the JSON `Bookmarks` file that Chrome keeps inside each profile, so there is no
need to export anything from the browser first.

### Usage

```
java -jar target/bookmark2html.jar <CHROME_PROFILE_DIR> <OUTPUT_HTML_FILE> <FOLDER_NAME> [<BACKUP_DIR>]
```

or the same arguments with the launchers `bookmark2html.cmd` (Windows) / `bookmark2html.sh`
(Linux, macOS). The first three arguments are mandatory; `BACKUP_DIR` is optional.

- **CHROME_PROFILE_DIR** is the Chrome profile directory that contains the `Bookmarks` file.
  The path to the `Bookmarks` file itself is accepted as well.
  - Windows: `C:\Users\<USER>\AppData\Local\Google\Chrome\User Data\<PROFILE>`
  - macOS: `~/Library/Application Support/Google/Chrome/<PROFILE>`
  - Linux: `~/.config/google-chrome/<PROFILE>`

  `<PROFILE>` is `Default` for the first profile and `Profile 1`, `Profile 2`... for the others.
  Open `chrome://version` and look at *Profile Path* to know yours.
- **OUTPUT_HTML_FILE** is the full path of the HTML file to generate, for example
  `D:\bookmarks\bookmarks.html`. Missing parent directories are created.
- **FOLDER_NAME** is the case-sensitive name of the bookmark folder to export. Its bookmarks and
  all its subfolders are included. If several folders share the name, the first one found
  (bookmark bar first, then other bookmarks, then mobile bookmarks) is used. The Chrome root
  folders themselves (for example `Bookmarks bar`) can be exported too.
  To export every bookmark pass the keyword `ALL_EXISTING_BOOKMARKS`: the Chrome root folders
  (`Bookmarks bar`, `Other bookmarks`, `Mobile bookmarks`) then appear at the top of the tree.
  An empty name (`""`) is not accepted, because `java.exe` on Windows silently drops empty
  arguments. As a consequence, a folder named exactly `ALL_EXISTING_BOOKMARKS` cannot be exported
  on its own.
- **BACKUP_DIR** is optional: the full path of a directory where the Chrome `Bookmarks` file used
  for the export is saved, zipped, as `original_bookmarks_<yyyyMMdd_HHmmss>.zip` (the date and
  time of the export). It is created if it does not exist. The backup is made once the HTML is
  written, so a failed export leaves no backup. Every run adds a new zip; old ones are never
  deleted. When it is missing or empty (`""`) no backup is made.

The program prints what it exported (and where the backup went) and ends with exit code `0`.
On any problem (wrong arguments, folder not found, unreadable file...) it prints `Error: ...`
and ends with exit code `1`, which makes it easy to use from scripts.

Example, export the folder `TECHNICAL`:

```
bookmark2html.cmd "C:\Users\me\AppData\Local\Google\Chrome\User Data\Default" "D:\bookmarks\technical.html" TECHNICAL "D:\bookmarks\backup"
```

Export every bookmark, without backup:

```
bookmark2html.cmd "C:\Users\me\AppData\Local\Google\Chrome\User Data\Default" "D:\bookmarks\all.html" ALL_EXISTING_BOOKMARKS
```

### The generated page

- A header with the folder name and how many bookmarks and folders it holds.
- The bookmarks as a tree, in the same order as in Chrome: the content of every folder is
  indented one level to the right with a guide line, so the hierarchy is clear at a glance.
- Every folder can be collapsed and shows how many bookmarks it holds.
- Each bookmark shows its favicon, its title (the url when it has none) and its domain.
  Favicons are downloaded (in parallel, once per domain) from Google's favicon service while
  generating the page and embedded as `data:` images, so an internet connection is needed only
  at that moment. A bookmark whose favicon cannot be downloaded just shows no icon.
- The bookmark under the mouse (or with the keyboard focus) is highlighted in yellow.
- Buttons at the top right corner:
  - `A-` / `A+` make the whole tree smaller or bigger, so more (or less) of it fits on screen.
  - `Host` hides or shows the host at the right of every bookmark.
  - `Width` makes the sheet as wide as the window; pressing it again goes back to the original
    width.

  The browser remembers these choices in its `localStorage` (keys `bookmark2html.*`), not in the
  HTML file, so they survive regenerating the page. Chrome shares them among all the pages opened
  from local files.
- On narrow screens (phones) the hosts are always hidden, and printing leaves the buttons out.
- The indentation width is the `--indent` CSS variable.

### Customizing the look

All the styling lives in [`src/main/resources/style.css`](src/main/resources/style.css) and is
copied into every generated page. The easiest way to restyle it is changing the CSS variables at
the top of the file (colors, font, indentation, icon size...); the class names of every part of the
page are listed there too. Rebuild after editing it.

For a one-off change, edit the `<style>` block of a generated page directly.

The page skeleton is [`src/main/resources/template.html`](src/main/resources/template.html),
with the placeholders `{{title}}`, `{{summary}}`, `{{style}}`, `{{content}}` and `{{date}}`.

### Build

Requires Java 17 and Maven.

```
mvn clean package
```

It runs the tests and produces the executable `target/bookmark2html.jar`, with all its
dependencies inside.

### Design

```
Bookmark2html          entry point: parses arguments and wires the pieces
BookmarkExporter       read -> find folder -> render -> write -> back up
reader/                BookmarkReader, ChromeBookmarkReader (Chrome JSON -> model)
model/                 BookmarkFolder, BookmarkLink
finder/                FolderFinder
render/                BookmarkRenderer, PrettyHtmlRenderer, PageTemplate
favicon/               FaviconSource, GoogleFaviconSource
writer/                OutputWriter, FileOutputWriter
backup/                BookmarkBackup, ZipBookmarkBackup
```

Supporting another browser or another output format means adding a new `BookmarkReader` or
`BookmarkRenderer` implementation; the rest of the code stays the same.

### Credits

The code of this project has been generated by [Claude](https://claude.ai) (Anthropic).
