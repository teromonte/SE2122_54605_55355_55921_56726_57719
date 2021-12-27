package org.jabref.model.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.jabref.model.entry.Author.compare;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorTest {

    @Test
    void compareTest() {
        Author author1 = new Author("Diogo", "D.", "de la", "Madrid", "Jr.");
        Author author2 = new Author("Thiago", "T.", "de", "Barcelona", "Jr.");
        Author author3 = new Author("Thiago", "T.", "de", "Barcelona", "Jr.");
        Author author4 = new Author("Yulia", "Y.", "de", "Seville", "Jr.");

        assertThat("", -1, greaterThanOrEqualTo(compare(author1, author2)));
        assertThat("", 0, equalTo(compare(author3, author2)));
        assertThat("", 1, lessThanOrEqualTo(compare(author4, author3)));


    }

    @Test
    void addDotIfAbbreviationAddDot() {
        assertEquals("O.", Author.addDotIfAbbreviation("O"));
        assertEquals("A. O.", Author.addDotIfAbbreviation("AO"));
        assertEquals("A. O.", Author.addDotIfAbbreviation("AO."));
        assertEquals("A. O.", Author.addDotIfAbbreviation("A.O."));
        assertEquals("A.-O.", Author.addDotIfAbbreviation("A-O"));
    }

    @Test
    void addDotIfAbbreviationDoesNotAddMultipleSpaces() {
        assertEquals("A. O.", Author.addDotIfAbbreviation("A O"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"O.", "A. O.", "A.-O.",
            "O. Moore", "A. O. Moore", "O. von Moore", "A.-O. Moore",
            "Moore, O.", "Moore, O., Jr.", "Moore, A. O.", "Moore, A.-O.",
            "MEmre", "{\\'{E}}douard", "J{\\\"o}rg", "Moore, O. and O. Moore",
            "Moore, O. and O. Moore and Moore, O. O."})
    void addDotIfAbbreviationDoNotAddDot(String input) {
        assertEquals(input, Author.addDotIfAbbreviation(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void addDotIfAbbreviationIfNameIsNullOrEmpty(String input) {
        assertEquals(input, Author.addDotIfAbbreviation(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "a"})
    void addDotIfAbbreviationLowerCaseLetters(String input) {
        assertEquals(input, Author.addDotIfAbbreviation(input));
    }

    @Test
    void addDotIfAbbreviationStartWithUpperCaseAndHyphen() {
        assertEquals("A.-melia", Author.addDotIfAbbreviation("A-melia"));
    }

    @Test
    void addDotIfAbbreviationEndsWithUpperCaseLetter() {
        assertEquals("AmeliA", Author.addDotIfAbbreviation("AmeliA"));
    }

    @Test
    void addDotIfAbbreviationEndsWithUpperCaseLetterSpaced() {
        assertEquals("Ameli A.", Author.addDotIfAbbreviation("Ameli A"));
    }

    @Test
    void addDotIfAbbreviationEndsWithWhiteSpaced() {
        assertEquals("Ameli", Author.addDotIfAbbreviation("Ameli "));
    }

    @Test
    void addDotIfAbbreviationEndsWithDoubleAbbreviation() {
        assertEquals("Ameli A. A.", Author.addDotIfAbbreviation("Ameli AA"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1 23"})
    void addDotIfAbbreviationIfStartsWithNumber(String input) {
        assertEquals(input, Author.addDotIfAbbreviation(input));
    }

}
