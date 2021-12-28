package org.jabref.gui.search;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jabref.gui.icon.IconTheme;
import org.jabref.logic.search.DatabaseSearcherKeyword;
import org.jabref.logic.search.SearchQuery;
import org.jabref.model.database.BibDatabase;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KeywordSearchDialog {

    private static final int MAX_ENTRIES = 10;
    private final BibDatabase database;

    public KeywordSearchDialog(BibDatabase database){
        this.database = database;
    }

    public void display(){
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Keyword Search");
        TextField text = new TextField();
        text.setText("Keyword");
        TableView<EntriesKeyWordClass> table = new TableView<>();
        table.setEditable(false);
        TableColumn<EntriesKeyWordClass, String> firstCol = new TableColumn<>("Name");
        TableColumn<EntriesKeyWordClass, Integer> timesCol = new TableColumn<>("Number of Papers");
        TableColumn<EntriesKeyWordClass, List<String>> papersCol = new TableColumn<>("Titles of Papers");


        firstCol.setCellValueFactory(new PropertyValueFactory<>("Author"));
        timesCol.setCellValueFactory(new PropertyValueFactory<>("Number"));
        papersCol.setCellValueFactory(new PropertyValueFactory<>("Papers"));
        table.getColumns().add(firstCol);
        table.getColumns().add(timesCol);
        table.getColumns().add(papersCol);
        Button button1 = new Button("Search Keyword");
        button1.setGraphic(IconTheme.JabRefIcons.KEYWORD.getGraphicNode());
        button1.setOnAction(e-> {
            SearchQuery newQuery = new SearchQuery(text.getText(), null);
            if(newQuery.getQuery() != null) {
                int counter = 0;
                DatabaseSearcherKeyword searcherKeyword = new DatabaseSearcherKeyword(newQuery,database);
                Iterator<Map.Entry<String, Integer>> numbers = searcherKeyword.getResultNumber().iterator();
                Map<String,List<String>> titles = searcherKeyword.getResultList();
                while (numbers.hasNext() && counter < MAX_ENTRIES) {
                    Map.Entry<String, Integer> entry = numbers.next();
                    List<String> paperTitles = titles.get(entry.getKey());
                    table.getItems().add(new EntriesKeyWordClass(entry.getKey(), entry.getValue(),paperTitles));
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

    public class EntriesKeyWordClass {
        private SimpleStringProperty author;
        private SimpleIntegerProperty number;
        private SimpleListProperty papers;


        public EntriesKeyWordClass(String author, int number, List<String> papers) {
            this.author = new SimpleStringProperty(author);
            this.number = new SimpleIntegerProperty(number);
            this.papers = new SimpleListProperty(FXCollections.observableList(papers));
        }

        public String getAuthor() {
            return author.get();
        }


        public int getNumber() {
            return number.get();
        }

        public ObservableList getPapers() {
            return papers.get();
        }
    }
}
