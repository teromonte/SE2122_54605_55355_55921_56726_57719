package org.jabref.logic.search;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.AuthorList;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DatabaseSearcherKeyword {

    private final static String AND = "and";
    private final Logger LOGGER = LoggerFactory.getLogger(DatabaseSearcherKeyword.class);
    private final SearchQuery query;
    private final List<BibEntry> entries;
    private Map<String,Integer> resultNumber;
    private Map<String,List<String>> resultTitles;

    public DatabaseSearcherKeyword(SearchQuery query, BibDatabase database){
        this.query = query;
        this.entries = database.getEntries();
        resultNumber = new TreeMap<>();
        resultTitles = new TreeMap<>();
        makeMatches();
    }

    private static class EntriesComparator implements Comparator<Map.Entry<String, Integer>> {

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

    private void makeMatches(){
        for (BibEntry e : entries) {
            Set<String> keys = e.getFieldAsWords(StandardField.KEYWORDS);

            if (keys.contains(query.getQuery())) {
                AuthorList al = AuthorList.parse(e.getField(StandardField.AUTHOR).get());
                String[] authorListParsed = al.getAsFirstLastNamesWithAnd().split(AND);
                for (String author : authorListParsed) {
                    if (!author.contentEquals(query.getQuery()) ) {
                        resultNumber.merge(author.trim(),1,Integer::sum);
                        List<String> titles = resultTitles.computeIfAbsent(author.trim(), k -> new LinkedList<>());
                        titles.add(e.getTitle().get());

                    }
                }
            }
        }

    }

    public List<Map.Entry<String, Integer>> getResultNumber(){
        Comparator<Map.Entry<String, Integer>> c = new EntriesComparator();
        List<Map.Entry<String, Integer>> resultList = new ArrayList<>(this.resultNumber.entrySet());
        Collections.sort(resultList,c);
        return resultList;
    }
    public Map<String,List<String>> getResultList(){
        return this.resultTitles;
    }
}
