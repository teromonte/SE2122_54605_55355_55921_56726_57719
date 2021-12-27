package org.jabref.gui.search;

import javafx.fxml.FXML;


import javafx.scene.control.*;
import javafx.util.Pair;
import org.controlsfx.control.textfield.CustomTextField;
import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.preview.PreviewViewer;
import org.jabref.gui.util.BaseDialog;
import org.jabref.logic.search.DatabaseSearcherAuthor;
import org.jabref.logic.search.SearchQuery;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.Author;
import org.jabref.preferences.PreferencesService;

import javax.inject.Inject;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Iterator;
import java.util.Map;

public class AuthorSearchDialog extends BaseDialog<Void>  {

    private static final int MAX_ENTRIES = 10;
    private final BibDatabase database;
    private final CustomTextField text = SearchTextField.create();
    @Inject private PreferencesService preferencesService;
    @Inject private StateManager stateManager;
    @Inject private DialogService dialogService;


    public AuthorSearchDialog(BibDatabase database){
        this.database = database;
    }
    @FXML
    private void initialize() {
        BibDatabaseContext searchDatabaseContext = new BibDatabaseContext();
        PreviewViewer previewViewer = new PreviewViewer(searchDatabaseContext, dialogService, stateManager);
        previewViewer.setTheme(preferencesService.getTheme());
        previewViewer.setLayout(preferencesService.getPreviewPreferences().getCurrentPreviewStyle());
        SearchQuery newQuery = new SearchQuery(text.getText(), null);
        Map<String,Integer> it = new DatabaseSearcherAuthor(newQuery, database).getMatches();
        TableView table = new TableView<>();
        table.setEditable(false);
        TableColumn<String,String> firstCol = new TableColumn<>("Name");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        TableColumn<String,String> timesCol = new TableColumn<>("Number of Papers");
        timesCol.setCellValueFactory(new PropertyValueFactory<>("NumberOfPapers"));
        table.getColumns().add(firstCol);
        table.getColumns().add(timesCol);
        int counter = 0;
        Iterator<Map.Entry<String,Integer>> entries = it.entrySet().iterator();
        while( entries.hasNext() && counter < MAX_ENTRIES){
            Map.Entry<String,Integer> e = entries.next();
            table.getItems().add(new Pair(e.getKey(),e.getValue()));
            counter++;
        }
        container.getItems().addAll(table,previewViewer);


    }
    private static class Pair {
        private final String left;
        private final Integer right;
        public Pair(String key, Integer value){
            this.left = key;
            this.right = value;
        }
        public String getKey(){
            return left;
        }
        public int getValue(){
            return right;
        }
    }

}
