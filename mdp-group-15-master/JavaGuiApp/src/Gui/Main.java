package Gui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.CornerRadii;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Paint background = Color.web("#D8D8D8");

        GuiTimer timer = new GuiTimer();
        GuiLogger logger = new GuiLogger();
        HBox hbox2 = new HBox();
        hbox2.getChildren().addAll(logger, timer);
        hbox2.setSpacing(10.);

        HBox hBox = new HBox();
        hBox.setBackground(new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY)));
        hBox.setPadding(new Insets(15, 30,15,30));
        hBox.setSpacing(20.);

        GuiButton start = new GuiButton("Start");
        start.registerClicked(new Command() {
            @Override
            public void run(Event e) {
                timer.start();
                logger.appendText("WHY YOU CLICK ME");
            }
        });

        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();
        GuiMap map = new GuiMap();
        vBox1.getChildren().addAll(map);

        GuiTabView tabView = new GuiTabView();
        GuiLoader loader = new GuiLoader();

        //Loading of file
        loader.registerClicked(0, new Command() {
            @Override
            public void run(Event e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load Map");
                File mapFile = fileChooser.showOpenDialog(primaryStage);

                if (mapFile == null)
                    return;

                loader.setDisplay(mapFile.getName());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(mapFile));
                    String line = br.readLine();
                    String complete = "";
                    while (line != null) {
                        complete = complete + line + ";";
                        line = br.readLine();
                    }

                    String[] row = complete.split(";");
                    String[] column;
                    int[][] mapArray = new int[20][15];
                    for(int i = 0; i < row.length; i++){
                        column = row[i].split(",");
                        for(int x = 0; x <column.length; x++){
                            mapArray[i][x] = Integer.parseInt(column[x]);
                        }
                    }

                    br.close();
                    map.loadMap(mapArray);
                } catch(Exception e1) {
                    System.out.print(e1);
                }
            }
        });

        //Saving of files
        loader.registerClicked(1, new Command() {
            @Override
            public void run(Event e) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(primaryStage);

                if(file == null)
                    return;

                int[][] mapArray = map.getMapArray();
                int[] mapArrayRow;
                String str = "";
                for(int r = 0; r < mapArray.length; r++){
                   mapArrayRow =  mapArray[r];
                   for(int c = 0; c < mapArrayRow.length; c++){
                       str = str + mapArray[r][c] + ",";
                   }
                   str = str + "\n";
                }
                try {
                    FileWriter fileWriter = null;
                    fileWriter = new FileWriter(file);
                    fileWriter.write(str);
                    fileWriter.close();
                } catch (Exception ex) {
                   //Logger.getLogger(JavaFX_Text.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Reset of map
        loader.registerClicked(2, new Command() {
            @Override
            public void run(Event e) {
                map.clearMap();
            }
        });

        tabView.appendControl(0, loader);
        tabView.appendControl(0, new GuiGroupButton());
        vBox2.getChildren().addAll(new GuiLabelButton("", ""), tabView, hbox2, start);
        vBox2.setSpacing(15.);
        hBox.getChildren().addAll(vBox1, vBox2);

        Scene scene = new Scene(hBox,1000, 600);

        primaryStage.setTitle("MDP Group15");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}