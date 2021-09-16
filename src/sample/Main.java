/**
 * SPAM BLASTER
 * Authors: Alex Bianchi & Soman Sheikh
 */

package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.TestFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Main extends Application {
    Stage stage;
    ImageView imageView;

    Map<String,Double> spamProbabilityMap;
    static ObservableList<TestFile> files;
    File dir;
    File trainSpamDir;
    File trainHamDir;
    File testSpamDir;
    File testHamDir;

    @Override
    /**
     * starts the program
     */
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Image image = new Image(getClass().getClassLoader().getResource("spamLogo_resized.png").toString());
        imageView = new ImageView(image);
        choosePaths();

    }

    /**
     * creates the window where the user chooses their directory that stores their data
     */
    public void choosePaths(){
        // path label set to null when no directory is selected
        Text path = new Text("null");
        path.setFill(Color.rgb(72,61,139));
        path.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        // create a DirectoryChooser to pick the directory with the data in it
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));

        // DirectoryChooser opens on button press
        Button pickDir = new Button("Select Directory");
        pickDir.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");
        pickDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                dir = directoryChooser.showDialog(stage);
                path.setText(dir.getAbsolutePath());
                trainSpamDir = new File(dir.getAbsolutePath()+"\\train\\spam");
                trainHamDir = new File(dir.getAbsolutePath()+"\\train\\ham");
                testHamDir = new File(dir.getAbsolutePath()+"\\test\\ham");
                testSpamDir = new File(dir.getAbsolutePath()+"\\test\\spam");
            }
        });

        // create a button that moves on to the next stage of the program
        Button submit = new Button("Submit");
        submit.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //button does not work unless a directory has been chosen
                if(dir != null){
                    trainProgram();
                }
            }
        });

        // clears the path chosen by the user
        Button clear = new Button("Clear");
        clear.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                path.setText("null");
                dir = null;
            }
        });

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(submit,clear);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(pickDir,path);

        BorderPane imagePane = new BorderPane();
        imagePane.setStyle("-fx-background-color: darkslateblue;");
        imagePane.setCenter(imageView);

        VBox vbox2 = new VBox();
        vbox2.setStyle("-fx-border-color: black");
        vbox2.setStyle("-fx-background-color: Gainsboro");
        vbox2.setSpacing(10);
        vbox2.getChildren().addAll(vbox,hbox);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(imagePane);
        borderPane.setCenter(vbox2);
        vbox2.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        hbox.setAlignment(Pos.BOTTOM_CENTER);

        Scene scene = new Scene(borderPane, 600, 500);
        changeScene(scene,"SPAM BLASTER");
    }

    /**
     * goes through the files in the train folder and counts how many files contain each word,
     * then builds maps with the results
     */
    public void trainProgram(){
        //create a message to show to the user
        Text text = new Text("Please wait while data loads...");
        text.setFill(Color.rgb(72,61,139));
        text.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        BorderPane loadPane = new BorderPane();
        loadPane.setCenter(text);
        loadPane.setStyle("-fx-background-color: Gainsboro");

        // put the logo into a BorderPane to center it
        BorderPane imagePane = new BorderPane();
        imagePane.setCenter(imageView);
        imagePane.setStyle("-fx-background-color: darkslateblue;");
        loadPane.setTop(imagePane);

        // change the scene
        Scene loadingScene = new Scene(loadPane,600,499);
        changeScene(loadingScene,"SPAM BLASTER");

        // count words using the WordCounter class
        WordCounter trainSpamFreq = new WordCounter(trainSpamDir);
        WordCounter trainHamFreq = new WordCounter(trainHamDir);

        // initialize the spamProbabilityMap
        spamProbabilityMap = new TreeMap<>();

        // go through the keys(words) and do calculations
        Set<String> spamKeys = new HashSet<String>(trainSpamFreq.getKeys());
        for(String key : spamKeys){
            double wGivenS = (double)trainSpamFreq.getCount(key) / (double)trainSpamFreq.getFileCount();
            double wGivenH = (double)trainHamFreq.getCount(key) / (double)trainHamFreq.getFileCount();
            double sGivenW = wGivenS/(wGivenS + wGivenH);
            spamProbabilityMap.put(key, sGivenW);
        }
        testProgram();
    }

    /**
     * calculates the probability that files in the test folder are spam
     */
    public void testProgram(){
        // initialize the global variable 'files'
        files = FXCollections.observableArrayList();

        // create a ProbabilityChecker object
        // the constructor takes a map that contains the probability a file is spam
        ProbabilityChecker probabilityChecker = new ProbabilityChecker(spamProbabilityMap);
        double probability;

        double accuracy = 0;
        double precision = 0;

        //go through spam
        File[] spamFiles = testSpamDir.listFiles();
        for(File current: spamFiles){
            probability = probabilityChecker.calculate(current,true);
            files.add(new TestFile(current.getName(),probability,"spam"));
        }

        //go through ham
        File[] hamFiles = testHamDir.listFiles();
        for(File current: hamFiles){
            probability = probabilityChecker.calculate(current,false);
            files.add(new TestFile(current.getName(),probability,"ham"));
        }

        accuracy = probabilityChecker.getAccuracy();
        precision = probabilityChecker.getPrecision();

        try{
            showResults(accuracy,precision);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * displays the probability table with the results for each file tested
     */
    public void showResults(double accuracy, double precision)throws Exception{
        // create "file name" column
        TableColumn<TestFile, String> fileNameColumn = new TableColumn<>("File name");
        fileNameColumn.setMinWidth(300);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        // create "actual class" column
        TableColumn<TestFile, String> actualClassColumn = new TableColumn<>("Actual class");
        actualClassColumn.setMinWidth(100);
        actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        // create "spam probability" column
        TableColumn<TestFile, String> spamProbabilityColumn = new TableColumn<>("Spam probability");
        spamProbabilityColumn.setMinWidth(150);
        spamProbabilityColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbRounded"));

        // create the TableView that the above columns will be put into
        TableView<TestFile> tableView = new TableView<>();
        tableView.setItems(getFiles());
        tableView.getColumns().addAll(fileNameColumn, actualClassColumn, spamProbabilityColumn);
        tableView.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        tableView.setStyle("-fx-background-color: darkslateblue");

        // labels for the output of the precision and accuracy of the program
        DecimalFormat df = new DecimalFormat("0.00000");

        Text accLabel = new Text("Accuracy:");
        accLabel.setFill(Color.rgb(72,61,139));
        accLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Text prcLabel = new Text("Precision:");
        prcLabel.setFill(Color.rgb(72,61,139));
        prcLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TextField accNum = new TextField(df.format(accuracy));
        accNum.setEditable(false);
        TextField prcNum = new TextField(df.format(precision));
        prcNum.setEditable(false);

        // create a GridPane to put the above labels in
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.setMaxHeight(200);
        gridPane.setMaxWidth(600);
        gridPane.add(accLabel, 0, 0, 1, 1);
        gridPane.add(prcLabel, 0, 1, 1, 1);
        gridPane.add(accNum, 1, 0, 1, 1);
        gridPane.add(prcNum, 1, 1, 1, 1);

        // put the logo into a BorderPane to center it
        BorderPane imagePane = new BorderPane();
        imagePane.setCenter(imageView);
        imagePane.setStyle("-fx-background-color: darkslateblue;");

        // put everything created above into a VBox
        VBox sceneLayout = new VBox();
        sceneLayout.setSpacing(10);
        sceneLayout.getChildren().addAll(imagePane, tableView, gridPane);
        VBox.setVgrow(tableView,Priority.ALWAYS);

        // change the scene
        Scene scene = new Scene(sceneLayout,600,700);
        changeScene(scene,"SPAM BLASTER");
    }

    /**
     * @return returns an observable lists of TestFile objects
     */
    public static ObservableList<TestFile> getFiles(){
        return files;
    }

    /**
     * changes the scene on the main stage
     * @param scene the scene that the main stage will switch to
     * @param title the title that will be displayed in the stage window
     */
    public void changeScene(Scene scene, String title){
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
