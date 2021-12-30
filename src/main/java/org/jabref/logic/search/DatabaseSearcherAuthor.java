package org.jabref.logic.search;

import org.jabref.logic.formatter.bibtexfields.NormalizeNamesFormatter;
import org.jabref.logic.importer.AuthorListParser;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.Author;
import org.jabref.model.entry.AuthorList;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DatabaseSearcherAuthor {

    private final static String AND = "and";
    private final Logger LOGGER = LoggerFactory.getLogger(DatabaseSearcherAuthor.class);
    private final SearchQuery query;
    private final BibDatabase database;
    private final List<BibEntry> entries;

    public DatabaseSearcherAuthor(SearchQuery query, BibDatabase database){
        this.query = query;
        this.database = database;
        this.entries = database.getEntries();
    }

    private static class EntriesComparator implements Comparator<Map.Entry<String, Integer>>{

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            if(o1.getValue() > o2.getValue())
                return -1;
            else if(o1.getValue() < o2.getValue())
                return 1;
            else
                return o2.getKey().compareTo(o1.getKey());
        }
    }

    /**
     * se nao existir coAuthor passa a 1 se existir pega no int e o segundo argumento e faz sum
     * @return mapa ordenado dos co authores
     */
    public List<Map.Entry<String, Integer>> getMatches(){
        Map<String,Integer> result = new HashMap<>();
        for(BibEntry e : entries){
            if(e.getFieldAsWords(StandardField.AUTHOR).contains(query.getQuery())) {
                AuthorList al = AuthorList.parse(e.getField(StandardField.AUTHOR).get());
                String[] authorListParsed = al.getAsFirstLastNamesWithAnd().split(AND);
                for (String author : authorListParsed) {
                    System.out.println(author);
                    if (!author.contains(query.getQuery())) {
                        result.merge(author.trim(),1,Integer::sum);
                    }
                }
            }
        }
        Comparator<Map.Entry<String, Integer>> c = new EntriesComparator();
        List<Map.Entry<String, Integer>> resultList = new ArrayList<>(result.entrySet());
        Collections.sort(resultList,c);
        return resultList;
    }
}
