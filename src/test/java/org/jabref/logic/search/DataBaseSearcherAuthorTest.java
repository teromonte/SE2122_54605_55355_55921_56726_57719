package org.jabref.logic.search;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.search.rules.SearchRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

public class DataBaseSearcherAuthorTest {
    public static final SearchQuery query = new SearchQuery("Diogo", null);
    private BibDatabase database;

    @BeforeEach
    public void setUp() {
        database = new BibDatabase();
    }

    @Test
    public void emptyDatabase(){
        
    }
}
