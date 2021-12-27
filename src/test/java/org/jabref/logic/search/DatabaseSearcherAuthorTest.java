package org.jabref.logic.search;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
    public void testNoMatchesFromDabaseWithIncollectionTypeEntry() {
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
}
