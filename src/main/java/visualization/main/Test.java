package visualization.main;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;


public class Test extends Application {
    @Override
    public void start(Stage stage) {
        Button button = new Button("Click if you can");
        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        VBox vBox = new VBox(textField1, textField2, textField3, button);
        ReadOnlyBooleanProperty readOnlyBooleanProperty = new SimpleBooleanProperty(false);

        StringProperty stringProperty = new SimpleStringProperty();
        button.disableProperty().bind(textField1.textProperty().isEmpty().or(textField2.textProperty().isEmpty().or(textField3.textProperty().isEmpty())));

        //Creating a scene object
        Scene scene = new Scene(vBox, 600, 300);

        //Setting title to the Stage
        stage.setTitle("Multiple transformations");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

    }
    public static void main(String args[]){
        launch(args);
    }
}