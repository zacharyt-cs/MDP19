package Gui;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GuiTabView extends VBox {
    private Boolean simulator = true;
    private GuiButton simulation;
    private GuiButton actualRun;
    private StackPane simulation_content;
    private StackPane actualRun_content;
    private StackPane displayed_content;

    private HBox topBar;
    private VBox actualRun_content_vbox;
    private VBox simulation_content_vbox;
    private Rectangle simulation_rect;
    private Rectangle actualRun_rect;

    public GuiTabView(){
        this(550, 38);
    }

    public GuiTabView(int width, int height){
        Paint white = Color.web("#ECF0F1");
        Paint black = Color.web("#000000");

        topBar = new HBox();

        //Simulation Button
        simulation = new GuiButton("Simulation",width/4, 38);
        simulation.enableHover(false);
        simulation.enabled(false);
        simulation.shadow(false);

        //Actual Button
        actualRun = new GuiButton("Actual Run", width/4, 38);
        actualRun.shadow(false);
        actualRun.enableHover(false);

        //Simulation tap, displayed when simulation is clicked
        simulation_rect = new Rectangle(width, 296, white);
        simulation_content_vbox = new VBox();
        simulation_content_vbox.setSpacing(10.);
        simulation_content_vbox.setAlignment(Pos.CENTER);
        simulation_content_vbox.setAlignment(Pos.TOP_LEFT);
        simulation_content_vbox.setPadding(new Insets(20,20,20,20));
        simulation_content = new StackPane();
        simulation_content.getChildren().addAll(simulation_rect, simulation_content_vbox);

        //Actual run tap, displayed when actual run is clicked
        actualRun_rect = new Rectangle(width, 296, black);
        actualRun_content_vbox = new VBox();
        actualRun_content_vbox.setAlignment(Pos.CENTER);
        actualRun_content = new StackPane();
        actualRun_content.getChildren().addAll(actualRun_rect, actualRun_content_vbox);

        //Register function to simulation button click
        simulation.registerClicked(new Command() {
            @Override
            public void run(Event e) {
                simulation.enabled(false);
                actualRun.enabled(true);
                simulator = true;
                draw();
            }
        });

        //Register function to actual run button click
        actualRun.registerClicked(new Command() {
            @Override
            public void run(Event e) {
                actualRun.enabled(false);
                simulation.enabled(true);
                simulator = false;
                draw();
            }
        });

        topBar.getChildren().addAll(simulation, actualRun);
        this.getChildren().addAll(topBar, simulation_content);

        //Set Shadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        this.setEffect(dropShadow);
    }

    public void draw(){
        if(this.simulator)
            displayed_content = simulation_content;
        else
            displayed_content = actualRun_content;

        this.getChildren().clear();
        this.getChildren().addAll(topBar, displayed_content);
    }

    public void appendControl(int tabid, Node control){
        if(tabid == 0){
            simulation_content_vbox.getChildren().add(control);
        } else {
            actualRun_content_vbox.getChildren().add(control);
        }
    }
}