package org.jabref.logic.search;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.model.search.rules.SearchRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseSearcherKeywordTest {

    public static final SearchQuery INVALID_SEARCH_QUERY = new SearchQuery("\\asd123{}asdf", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));

    private BibDatabase database;

    @BeforeEach
    public void setUp() {
        database = new BibDatabase();
    }

    @Test
    public void testNoMatchesFromEmptyDatabase() {
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("whatever", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidSearchExpression() {
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(INVALID_SEARCH_QUERY, database).getResultNumber();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testGetDatabaseFromMatchesDatabaseWithEmptyEntries() {
        database.insertEntry(new BibEntry());
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("whatever", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromDatabaseWithArticleTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "harrer");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("whatever", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidQuery() {
        SearchQuery query = new SearchQuery("asdf[", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));

        DatabaseSearcherKeyword DatabaseSearcherKeyword = new DatabaseSearcherKeyword(query, database);

        assertEquals(Collections.emptyList(), DatabaseSearcherKeyword.getResultNumber());
    }

    @Test
    public void testNoMatchesFromDatabaseWithInCollectionTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.InCollection);
        entry.setField(StandardField.AUTHOR, "tonho, idk and yuliia");
        entry.setField(StandardField.KEYWORDS, "thiago");

        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("asdf", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));
        DatabaseSearcherKeyword DatabaseSearcherKeyword = new DatabaseSearcherKeyword(query, database);

        assertEquals(Collections.emptyList(), DatabaseSearcherKeyword.getResultNumber());
    }

    @Test
    public void testNoMatchFromDatabaseWithEmptyEntry() {
        BibEntry entry = new BibEntry();
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("tonho", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));
        DatabaseSearcherKeyword DatabaseSearcherKeyword = new DatabaseSearcherKeyword(query, database);

        assertEquals(Collections.emptyList(), DatabaseSearcherKeyword.getResultNumber());
    }

    @Test
    public void testCorrectMatchWithOneKeyword() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "Vieira, Tiago");
        entry.setField(StandardField.KEYWORDS, "bacalhau");
        entry.setField(StandardField.TITLE, "misterios do bacalhau");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("bacalhau", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        Map<String, List<String>> matchesList = new DatabaseSearcherKeyword(new SearchQuery("bacalhau", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultList();
        assertEquals("Tiago Vieira", matches.get(0).getKey());
        assertEquals(1, matches.get(0).getValue());
        assertTrue(matchesList.containsKey("Tiago Vieira"));
        assertEquals("misterios do bacalhau", matchesList.get("Tiago Vieira").get(0));
        assertEquals(1, matches.size());
    }

    @Test
    public void testCorrectMatchMoreThanOneKeyword() {
        BibEntry entry = new BibEntry(StandardEntryType.Book);
        entry.setField(StandardField.AUTHOR, "Ye, Diogo");
        entry.setField(StandardField.KEYWORDS, "bacalhau atum");
        entry.setField(StandardField.TITLE, "compendio do bacalhau e atum");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("bacalhau", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        Map<String, List<String>> matchesList = new DatabaseSearcherKeyword(new SearchQuery("bacalhau", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultList();
        assertEquals("Diogo Ye", matches.get(0).getKey());
        assertEquals(1, matches.get(0).getValue());
        assertEquals(1, matches.size());
        assertEquals("compendio do bacalhau e atum", matchesList.get("Diogo Ye").get(0));
    }

    @Test
    public void testCorrectMatchWithSeveralEntriesSameAuthor() {
        BibEntry entry0 = new BibEntry(StandardEntryType.Article);
        entry0.setField(StandardField.AUTHOR, "Vieira, Tiago");
        entry0.setField(StandardField.TITLE, "aprender JUnit para totos");
        entry0.setField(StandardField.KEYWORDS, "cs");


        BibEntry entry1 = new BibEntry(StandardEntryType.Article);
        entry1.setField(StandardField.AUTHOR, "Vieira, Tiago");
        entry1.setField(StandardField.TITLE, "aprender java para totos");
        entry1.setField(StandardField.KEYWORDS, "cs");

        database.insertEntry(entry0);
        database.insertEntry(entry1);

        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("cs", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        Map<String, List<String>> matchesList = new DatabaseSearcherKeyword(new SearchQuery("cs", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultList();
        assertEquals("Tiago Vieira", matches.get(0).getKey());
        assertEquals(2, matches.get(0).getValue());
        assertTrue(matchesList.containsKey("Tiago Vieira"));
        assertEquals("aprender JUnit para totos", matchesList.get("Tiago Vieira").get(0));
        assertEquals("aprender java para totos", matchesList.get("Tiago Vieira").get(1));
        assertEquals(1, matches.size());
    }

    @Test
    public void testCorrectMatchWithSeveralEntriesDiffAuthors() {
        BibEntry entry0 = new BibEntry(StandardEntryType.Article);
        entry0.setField(StandardField.AUTHOR, "Vieira, Tiago");
        entry0.setField(StandardField.TITLE, "aprender JUnit para totos");
        entry0.setField(StandardField.KEYWORDS, "cs");


        BibEntry entry1 = new BibEntry(StandardEntryType.Article);
        entry1.setField(StandardField.AUTHOR, "Vieira, Tiago");
        entry1.setField(StandardField.TITLE, "aprender java para totos");
        entry1.setField(StandardField.KEYWORDS, "cs");

        BibEntry entry2 = new BibEntry(StandardEntryType.Article);
        entry2.setField(StandardField.AUTHOR, "Ye, Diogo");
        entry2.setField(StandardField.TITLE, "javafx for dummies");
        entry2.setField(StandardField.KEYWORDS, "cs");

        database.insertEntry(entry0);
        database.insertEntry(entry1);
        database.insertEntry(entry2);

        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherKeyword(new SearchQuery("cs", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultNumber();
        Map<String, List<String>> matchesList = new DatabaseSearcherKeyword(new SearchQuery("cs", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getResultList();
        assertEquals("Tiago Vieira", matches.get(0).getKey());
        assertEquals(2, matches.get(0).getValue());

        assertEquals("Diogo Ye", matches.get(1).getKey());
        assertEquals(1, matches.get(1).getValue());

        assertTrue(matchesList.containsKey("Tiago Vieira"));
        assertEquals("aprender JUnit para totos", matchesList.get("Tiago Vieira").get(0));
        assertEquals("aprender java para totos", matchesList.get("Tiago Vieira").get(1));

        assertTrue(matchesList.containsKey("Diogo Ye"));
        assertEquals("javafx for dummies", matchesList.get("Diogo Ye").get(0));

        assertEquals(2, matches.size());
    }
}
