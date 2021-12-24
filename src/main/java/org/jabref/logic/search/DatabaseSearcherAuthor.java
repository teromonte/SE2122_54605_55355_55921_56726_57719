package org.jabref.logic.search;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DatabaseSearcherAuthor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSearcherAuthor.class);
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
                return o1.getKey().compareTo(o2.getKey());
        }
    }

    /**
     * se nao existir coAuthor passa a 1 se existir pega no int e o segundo argumento e faz sum
     * @return mapa ordenado dos co authores
     */
    public Map<String, Integer> getMatches(){
        Map<String,Integer> result = new TreeMap<>();
        for(BibEntry e : entries){
            Set<String> set = e.getFieldAsWords(StandardField.AUTHOR);
            if(set.remove(query.getQuery())){
                for(String coAuthor : set){
                    result.merge(coAuthor, 1, Integer::sum);
                }
            }
        }
        result.entrySet().stream().sorted(new EntriesComparator()).forEach(System.out::println);
        return result;
    }
}
