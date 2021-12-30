package org.jabref.logic.search;

import java.util.*;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.model.search.rules.SearchRules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DatabaseSearcherAuthorTest {

    public static final SearchQuery INVALID_SEARCH_QUERY = new SearchQuery("\\asd123{}asdf", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));

    private BibDatabase database;

    @BeforeEach
    public void setUp() {
        database = new BibDatabase();
    }

    @Test
    public void testNoMatchesFromEmptyDatabase() {
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("whatever", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidSearchExpression() {
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(INVALID_SEARCH_QUERY, database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testGetDatabaseFromMatchesDatabaseWithEmptyEntries() {
        database.insertEntry(new BibEntry());
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("whatever", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromDatabaseWithArticleTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "harrer");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("whatever", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidQuery() {
        SearchQuery query = new SearchQuery("asdf[", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));

        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.emptyList(), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testNoMatchesFromDatabaseWithInCollectionTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.InCollection);
        entry.setField(StandardField.AUTHOR, "tonho, idk and yuliia");
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("asdf", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));
        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.emptyList(), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testNoMatchFromDatabaseWithEmptyEntry() {
        BibEntry entry = new BibEntry();
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("tonho", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION));
        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.emptyList(), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testCorrectMatchWithOneCoAuthor() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "Ye, Diogo and Vieira, Tiago");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("Diogo", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getMatches();
        Iterator<Map.Entry<String, Integer>> it = matches.iterator();
        assertEquals("Tiago Vieira", it.next().getKey());
        assertFalse(it.hasNext());
    }

    @Test
    public void testCorrectMatchButNoCoAuthors() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "Ye, Diogo");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("Diogo", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testCorrectMatchWithSeveralEntries() {
        BibEntry entry0 = new BibEntry(StandardEntryType.Article);
        entry0.setField(StandardField.AUTHOR, "Ye, Diogo and Ribeiro, Pedro");

        BibEntry entry1 = new BibEntry(StandardEntryType.Article);
        entry1.setField(StandardField.AUTHOR, "Ye, Diogo and Vieira, Tiago");

        BibEntry entry2 = new BibEntry(StandardEntryType.Article);
        entry2.setField(StandardField.AUTHOR, "Ye, Diogo and Vieira, Tiago");

        BibEntry entry3 = new BibEntry(StandardEntryType.Article);
        entry3.setField(StandardField.AUTHOR, "Ye, Diogo and Vieira, Tiago");

        BibEntry entry4 = new BibEntry(StandardEntryType.Article);
        entry4.setField(StandardField.AUTHOR, "Ye, Diogo and Monteiro, Thiago");

        BibEntry entry5 = new BibEntry(StandardEntryType.Article);
        entry5.setField(StandardField.AUTHOR, "Ye, Diogo and Monteiro, Thiago");

        database.insertEntry(entry0);
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        database.insertEntry(entry3);
        database.insertEntry(entry4);
        database.insertEntry(entry5);

        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("Diogo", EnumSet.of(SearchRules.SearchFlags.CASE_SENSITIVE, SearchRules.SearchFlags.REGULAR_EXPRESSION)), database).getMatches();
        Iterator<Map.Entry<String, Integer>> it = matches.iterator();
        assertEquals("Tiago Vieira", it.next().getKey());
        assertEquals("Thiago Monteiro", it.next().getKey());
        assertEquals("Pedro Ribeiro", it.next().getKey());
        assertFalse(it.hasNext());
    }
}
