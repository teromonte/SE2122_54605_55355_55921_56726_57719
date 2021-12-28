package org.jabref.gui.search;

import org.jabref.gui.JabRefFrame;
import org.jabref.gui.StateManager;
import org.jabref.gui.actions.SimpleCommand;
import org.jabref.gui.actions.StandardActions;
import org.jabref.model.database.BibDatabase;
import org.jabref.preferences.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchAction extends SimpleCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchAction.class);

    private final StandardActions action;
    private final JabRefFrame frame;


    public SearchAction(StandardActions action, JabRefFrame frame){
        this.action = action;
        this.frame = frame;

    }

    @Override
    public String toString() {
        return this.action.toString();
    }

    @Override
    public void execute() {
        if(action == StandardActions.NEW_SEARCH_AUTHOR){
            BibDatabase database = frame.getCurrentLibraryTab().getDatabase();
            AuthorSearchDialog authorSearch = new AuthorSearchDialog(database);
            authorSearch.display();
        } else if(action == StandardActions.SEARCH_KEYWORDS){
            BibDatabase database = frame.getCurrentLibraryTab().getDatabase();
            KeywordSearchDialog keywordSearch = new KeywordSearchDialog(database);
            keywordSearch.display();
        }
    }
}
