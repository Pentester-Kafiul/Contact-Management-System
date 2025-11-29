package contactmanager;

import contactmanager.view.ContactView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        ContactView view = new ContactView();
        view.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
