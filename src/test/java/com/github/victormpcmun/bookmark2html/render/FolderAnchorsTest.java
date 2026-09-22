package com.github.victormpcmun.bookmark2html.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FolderAnchorsTest {

    private final FolderAnchors anchors = new FolderAnchors();

    @Test
    void aFolderAtTheTopIsAnchoredByItsOwnName() {
        assertEquals("java", anchors.anchorFor("", "Java"));
    }

    @Test
    void aSubfolderIsAnchoredByTheNamesOfItsAncestorsAndItsOwn() {
        assertEquals("java/spring-boot", anchors.anchorFor("java", "Spring Boot"));
    }

    @Test
    void theSameAnchorIsNeverGivenTwice() {
        anchors.anchorFor("", "Java");

        assertEquals("java-2", anchors.anchorFor("", "Java"));
        assertEquals("java-3", anchors.anchorFor("", "java"));
    }

    @Test
    void foldersWithTheSameNameUnderDifferentParentsKeepTheirPlainName() {
        anchors.anchorFor("java", "Docs");

        assertEquals("kotlin/docs", anchors.anchorFor("kotlin", "Docs"));
    }

    @Test
    void theSlugHasNoAccentsCapitalsOrSymbols() {
        assertEquals("programacion-c-java", FolderAnchors.slug("  Programación: C++ & Java!  "));
    }

    @Test
    void aNameWithoutLettersOrDigitsFallsBackToAFixedWord() {
        assertEquals("folder", FolderAnchors.slug("★ ★"));
    }
}
