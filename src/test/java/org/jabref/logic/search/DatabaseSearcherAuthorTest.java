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

public class DatabaseSearcherAuthorTest {

    public static final SearchQuery INVALID_SEARCH_QUERY = new SearchQuery("\\asd123{}asdf", null);

    private BibDatabase database;

    @BeforeEach
    public void setUp() {
        database = new BibDatabase();
    }

    @Test
    public void testNoMatchesFromEmptyDatabase() {
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("whatever", null), database).getMatches();
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
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("whatever", null), database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromDatabaseWithArticleTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "harrer");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("whatever", null), database).getMatches();
        assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testCorrectMatchFromDatabaseWithArticleTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "harrer");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("harrer", null), database).getMatches();
        assertEquals(Collections.singletonList(entry), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidQuery() {
        SearchQuery query = new SearchQuery("asdf[", null);

        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.emptyList(), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testCorrectMatchFromDatabaseWithIncollectionTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.InCollection);
        entry.setField(StandardField.AUTHOR, "tonho");
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("tonho", null);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(query, database).getMatches();

        assertEquals(Collections.singletonList(entry), matches);
    }

    @Test
    public void testNoMatchesFromDatabaseWithTwoEntries() {
        BibEntry entry = new BibEntry();
        database.insertEntry(entry);

        entry = new BibEntry(StandardEntryType.InCollection);
        entry.setField(StandardField.AUTHOR, "tonho");
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("tonho", null);
        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.singletonList(entry), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testNoMatchesFromDatabaseWithInCollectionTypeEntry() {
        BibEntry entry = new BibEntry(StandardEntryType.InCollection);
        entry.setField(StandardField.AUTHOR, "tonho");
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("asdf", null);
        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.emptyList(), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testNoMatchFromDatabaseWithEmptyEntry() {
        BibEntry entry = new BibEntry();
        database.insertEntry(entry);

        SearchQuery query = new SearchQuery("tonho", null);
        DatabaseSearcherAuthor databaseSearcherAuthor = new DatabaseSearcherAuthor(query, database);

        assertEquals(Collections.emptyList(), databaseSearcherAuthor.getMatches());
    }

    @Test
    public void testCorrectMatchWithOneCoAuthor() {
        BibEntry entry = new BibEntry(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "Ye, Diogo and Vieira, Tiago");
        database.insertEntry(entry);
        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("Diogo", null), database).getMatches();
        assertEquals(Collections.singletonList("Tiago"), matches);
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

        List<Map.Entry<String, Integer>> matches = new DatabaseSearcherAuthor(new SearchQuery("Diogo", null), database).getMatches();
        Iterator<Map.Entry<String, Integer>> it = matches.iterator();
        assertEquals("Tiago", it.next().getKey());
        assertEquals("Thiago", it.next().getKey());
        assertEquals("Pedro", it.next().getKey());
    }
}
