package org.jabref.model.database;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BibDatabaseAuthorTest {
    private BibDatabase database;
    @BeforeEach
    void setUp() {
        database = new BibDatabase();
    }
    @Test
    void testGetAuthor(){
        BibEntry entry = new BibEntry();
        entry.setField(StandardField.AUTHOR, "ola, ahsdias");
        database.insertEntry(entry);
        String p = "ola";
        String sasd = database.getEntries().get(0).getAuthors();
        Set<String> dasdasd = database.getEntries().get(0).getFieldAsWords(StandardField.AUTHOR);
        System.out.println(sasd);
        System.out.println(dasdasd);
        String text = sasd.substring(0,p.length());
        System.out.println(text);
        System.out.println(p.length());
        assertTrue(p.equals(text));
    }

}
