package org.jabref.gui.search;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jabref.gui.DialogService;
import org.jabref.gui.JabRefFrame;
import org.jabref.gui.StateManager;
import org.jabref.gui.icon.IconTheme;
import org.jabref.logic.search.DatabaseSearcherAuthor;
import org.jabref.logic.search.SearchQuery;
import org.jabref.model.database.BibDatabase;
import org.jabref.preferences.PreferencesService;

import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Iterator;
import java.util.Map;


public class AuthorSearchDialog {


    private static final int MAX_ENTRIES = 10;
    private final BibDatabase database;
    private PreferencesService preferencesService;
    private StateManager stateManager;
    private DialogService dialogService;




    public AuthorSearchDialog(BibDatabase database, JabRefFrame frame, StateManager state, PreferencesService pref){
        this.database = database;
        this.dialogService = frame.getDialogService();
        this.stateManager = state;
        this.preferencesService =pref;

    }

    public void display(){
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Author Search");

        TextField text= new TextField();
        text.setText("Author Name");
        TableView<Pair> table = new TableView<Pair>();
        table.setEditable(false);
        TableColumn<Pair, String> firstCol = new TableColumn<>("Name");
        TableColumn<Pair, String> timesCol = new TableColumn<>("Number of Papers");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("Key"));
        timesCol.setCellValueFactory(new PropertyValueFactory<>("Value"));

        table.getColumns().add(firstCol);
        table.getColumns().add(timesCol);

        Button button1 = new Button("Search Author");
        button1.setGraphic(IconTheme.JabRefIcons.SEARCH_AUTHOR.getGraphicNode());
        button1.setOnAction(e-> {
            SearchQuery newQuery = new SearchQuery(text.getText(), null);
            if(newQuery.getQuery() != null) {
                int counter = 0;
                DatabaseSearcherAuthor searcherAuthor = new DatabaseSearcherAuthor(newQuery,database);
                Iterator<Map.Entry<String, Integer>> entries = searcherAuthor.getMatches().iterator();
                while (entries.hasNext() && counter < MAX_ENTRIES) {
                    Map.Entry<String, Integer> entry = entries.next();
                    table.getItems().add(new Pair(entry.getKey(), entry.getValue()));
                    counter++;
                }
            }
        });

        Button button2= new Button("Close");
        button2.setOnAction(e -> popUpWindow.close());

        VBox layout= new VBox(10);

        layout.getChildren().addAll(text, button1,table,button2);

        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);

        popUpWindow.setScene(scene1);

        popUpWindow.showAndWait();
    }

    public static class Pair {
        private final SimpleStringProperty left;
        private final SimpleIntegerProperty right;
        public Pair(String key, Integer value){
            this.left = new SimpleStringProperty(key);
            this.right = new SimpleIntegerProperty(value);
        }
        public String getKey(){
            return left.get();
        }
        public int getValue(){
            return right.get();
        }

    }


}
