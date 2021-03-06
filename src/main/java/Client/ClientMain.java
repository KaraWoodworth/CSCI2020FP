package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public TextArea courseText;
    public generateBarGraph ggraph;
    public Group graphgroup;
    public HBox mainBox;
    @Override
    public void start(Stage primaryStage) {

        courseText = new TextArea(); // will be the area to display bar items
        graphgroup = new Group(); // will be the area the bar graph exists in

        graphgroup.setLayoutY(10);
        graphgroup.setLayoutX(10);
        mainBox = new HBox();

        Scene mscene = new Scene(mainBox,800,600);
        primaryStage.setTitle("Client: Course grade view");
        primaryStage.setScene(mscene);
        primaryStage.show();

        //load data from server and populate graph
        getServerData(8000);

    }

    public void getServerData(int port){

        Platform.runLater(() -> {
            try {
                Socket socket = new Socket("localhost",port);

                DataInputStream getdata = new DataInputStream(socket.getInputStream());

                String rawdata = getdata.readUTF(); // recieves csv data from server
                List<courseGrades> grades = getGraphList(rawdata); // converts csv data to a list

                //below is creating a new graph object with the list generated from csv
                generateBarGraph ggraph = new generateBarGraph(grades, 500, courseText);

                // getting graph layout information and assigning group to it
                graphgroup = ggraph.getBarGraph();
                mainBox.getChildren().addAll(graphgroup, courseText); // add graph and text area to main hbox
                courseText.setText("select a bar to view data"); // default view to help with knowing what to do


            } catch(IOException e){
                System.err.println(e);
            }
        });
    }

    // takes csv data and coverts it into list that graph class can use
    public List<courseGrades> getGraphList(String rawdata){
        List<courseGrades> allgrades = new ArrayList<courseGrades>();
        if(rawdata.length() > 0) {
            String[] rawlines = rawdata.split("\n");
            String currentclass = "";

            courseGrades coursegrade = null;
            // get individusal lines
            for (String line : rawlines) {
                // get current lines fields
                String[] Fields = line.split(",");
                // this ensures we are parsing the right amount of fields
                if(Fields.length >= 5) {
                    //check if we are reading same course
                    if(!Fields[0].equals(currentclass)){

                        if (coursegrade != null){
                            allgrades.add(coursegrade);
                        }
                        coursegrade = new courseGrades();
                        currentclass = Fields[0];
                    }
                    Boolean currentboolean = false;
                    if(Fields[4].endsWith("true")){
                        currentboolean = true;
                    }else {
                    }
                    Double field2 = null;

                    if(Fields[2].length() > 0) {
                        field2 = Double.parseDouble(Fields[2]);
                    }

                    Double field3 = Double.parseDouble(Fields[3]);
                    coursegrade.gradeList.add(new gradeComponent(Fields[0],Fields[1],field2,field3,currentboolean));
                }
                else{
                    System.out.println("Error in reading line" + line + "\n" + "too few fields");
                }
            }
            allgrades.add(coursegrade);
        }
        return allgrades;
    }

    // this is for creating all bars
    public class generateBarGraph{
        // TODO: make bar dimensions dependant on containor size
        public int graphHeight = 400; // should do something to ensure bars are normalized
        public int graphWidth = 50; // should be containor size / bars + spacers
        public int barWidth = 50;
        private TextArea couresDisplay;
        public List<courseGrades> cgrades;

        public generateBarGraph(List<courseGrades> studentgrades, int graphheight, TextArea coursedisplay){

            this.cgrades = studentgrades;
            this.graphHeight = graphheight;
            this.graphWidth = (studentgrades.size() * barWidth * 2) + barWidth;
            this.couresDisplay = coursedisplay;


        }
        // responsible for creating the components related to a single bar
        public Group getBarGraph(){
            Group bargroup = new Group();
            setBarLayout(bargroup);

            int offset = 0;
            int tmp = cgrades.size();
            for (courseGrades course : cgrades){
                makeBar(course,bargroup,offset);
                offset++;
            }

            return bargroup;
        }
        public void setBarLayout(Group basegroup){
            Rectangle chartbackground = new Rectangle(graphWidth,graphHeight,new Color(.9,.9,.9,1));
            basegroup.getChildren().add(chartbackground);
            Line[] gradeaxis = new Line[5];
            Label[] gradelables = new Label[5];
            gradeaxis[0] = new Line(0,graphHeight * 0.5,graphWidth,graphHeight * 0.5);
            gradeaxis[1] = new Line(0,graphHeight * 0.4,graphWidth,graphHeight * 0.4);
            gradeaxis[2] = new Line(0,graphHeight * 0.3,graphWidth,graphHeight * 0.3);
            gradeaxis[3] = new Line(0,graphHeight * 0.2,graphWidth,graphHeight * 0.2);
            gradeaxis[4] = new Line(0,graphHeight * 0.1,graphWidth,graphHeight * 0.1);

            gradelables[0] = new Label("50%");
            gradelables[0].setLayoutX(5);
            gradelables[0].setLayoutY(graphHeight * 0.5 - 15);
            gradelables[1] = new Label("60%");
            gradelables[1].setLayoutX(5);
            gradelables[1].setLayoutY(graphHeight * 0.4 - 15);
            gradelables[2] = new Label("70%");
            gradelables[2].setLayoutX(5);
            gradelables[2].setLayoutY(graphHeight * 0.3 - 15);
            gradelables[3] = new Label("80%");
            gradelables[3].setLayoutX(5);
            gradelables[3].setLayoutY(graphHeight * 0.2 - 15);
            gradelables[4] = new Label("A+");
            gradelables[4].setLayoutX(5);
            gradelables[4].setLayoutY(graphHeight * 0.1 - 15);

            for(int i = 0; i < 5; i++){
                gradeaxis[i].setStrokeDashOffset(2);
                gradeaxis[i].getStrokeDashArray().addAll(5d);
                gradeaxis[i].setStroke(new Color(0.8,0.8,.8,1));
                gradeaxis[i].setStrokeWidth(2);
                basegroup.getChildren().add(gradeaxis[i]);
                basegroup.getChildren().add(gradelables[i]);
            }
            Group tmp = new Group();
            tmp.setLayoutY(10);
            tmp.setLayoutX(10);
            Line yaxis = new Line(0,0,0,graphHeight);
            Line xaxis = new Line(0,graphHeight,graphWidth,graphHeight);

            yaxis.setStroke(Color.BLACK);
            xaxis.setStroke(Color.BLACK);
            basegroup.getChildren().addAll(yaxis,xaxis);
        }
        public void makeBar(courseGrades course, Group basegroup,int offset){


            Double completedweight = 0.0;
            Double completedAvg = 0.0;
            Double unfinishedweight = 0.0;
            Double expectedweight = 0.0;
            int barx = (barWidth * (offset * 2)) + barWidth;

            // these lists are necessary for when clicking on a bar we get a granular breakdown
            List<gradeComponent> completeditems = new ArrayList<gradeComponent>();
            List<gradeComponent> incompleteItems = new ArrayList<gradeComponent>();

            // process what grades are marked and what are still possible
            // also store list of grade names for display purposes
            for(gradeComponent gc : course.gradeList){
                if(gc.isCompleted){
                    completedweight += (gc.getRecieved() * gc.getWeight() / 100);
                    completedAvg += gc.getRecieved();
                    completeditems.add(gc);//.getName());
                }
                else{
                    unfinishedweight += gc.getWeight();
                    incompleteItems.add(gc);//.getName());
                }
            }
            if(completeditems.size() > 0){
                completedAvg = completedAvg / completeditems.size();
            }
            if (incompleteItems.size() > 0 && completedAvg > 0) {
                expectedweight = unfinishedweight * completedAvg / 100;
            }

            // setting bar segments to be % of bar height
            GraphTangle markedgrade = null;

            if(completedweight > 0){
                markedgrade = new GraphTangle(barWidth,
                        (int)((graphHeight / 100) * Math.round(completedweight)),
                        completeditems,this.couresDisplay);
                markedgrade.setFancyStyle(barx,
                        new Color(0.2,0.2,1,0.75),
                        new Color(.8,.8,1,0.65));
                markedgrade.setY(graphHeight - markedgrade.getHeight());

                basegroup.getChildren().add(markedgrade);
            }

            // this segment is the expected grade based on completed grade
            GraphTangle expectedgrade = null;
            if(expectedweight > 0)
            {
                expectedgrade = new GraphTangle(barWidth,
                        (int)((graphHeight / 100) * Math.round(expectedweight)),
                        incompleteItems,this.couresDisplay);

                if(markedgrade != null) {
                    expectedgrade.setY(graphHeight - markedgrade.getHeight() - expectedgrade.getHeight());
                } else {
                    expectedgrade.setY(graphHeight- expectedgrade.getHeight());
                }

                expectedgrade.setFancyStyle(barx,
                        new Color(0.1,0.8,0.1,0.6),
                        new Color(0.9,0.9,0.9,0.4));

                basegroup.getChildren().add(expectedgrade);
            }
            // this segment represents the remaining achievable grade minus expected grade
            GraphTangle unmarkedgrade;
            Double remaainingheight = unfinishedweight - expectedweight;
            if(remaainingheight > 0)
            {
                unmarkedgrade = new GraphTangle(barWidth,
                        (int)((graphHeight / 100) * Math.round(remaainingheight)),
                        incompleteItems,this.couresDisplay);
                // if there is no more available grade, then student has already achieved max grade
                if(expectedgrade != null) {
                    unmarkedgrade.setY(expectedgrade.getY() - unmarkedgrade.getHeight());
                } else {
                    unmarkedgrade.setY(graphHeight- unmarkedgrade.getHeight());
                }

                unmarkedgrade.setFancyStyle(barx,
                        new Color(0.8,0.8,0.8,0.2),
                        new Color(0.9,0.9,0.9,0.1));

                basegroup.getChildren().add(unmarkedgrade);
            }
        }
    }

    // custom shape object containing style and event information for bar component
    public class GraphTangle extends Rectangle{
        private List<gradeComponent>segmentItems;
        private TextArea textRef;

        public GraphTangle(int rwidth,int rheight,List<gradeComponent> segmentitems,TextArea textref){
            this.setWidth(rwidth);
            this.setHeight(rheight);
            this.segmentItems = segmentitems;
            this.textRef = textref;

            // bar segment events
            this.setOnMouseEntered( x -> { setMouseHoverScale((Rectangle)x.getSource(),true);});
            this.setOnMouseExited(x -> { setMouseHoverScale((Rectangle)x.getSource(),false);});
            this.setOnMouseClicked(x-> {setMouseClick();});
        }
        public List<gradeComponent> getSegmentItems(){
            return this.segmentItems;
        }
        public void setSegmentItems(List<gradeComponent>items){
            this.segmentItems = items;
        }
        private void setMouseClick(){
            textRef.setText("");
            if(segmentItems.size() > 0){
                String txt = "";
                for (gradeComponent x : this.segmentItems) {
                    if(txt == ""){
                        txt += "Course name: " + x.course_name + "\nGrade name: Grade Recieved,\t Grade Weight\n";
                    }
                        txt += x.grade_name + ": " + x.grade_recieved + ",\t" + x.grade_weight + "\n";

                }

                textRef.setText(txt);
            }
        }

        private void setMouseHoverScale(Rectangle rect,boolean isOn){
            if(isOn == true){
                rect.setWidth(rect.getWidth() + 20);
                rect.setX(rect.getX() - 10);
            }else {
                rect.setWidth(rect.getWidth() - 20);
                rect.setX(rect.getX() + 10);
            }
        }
        // rectangle style settings applied here
        public void setFancyStyle(int layoutx,Color colorstart,Color colorend){
            this.setLayoutX(layoutx);
            Stop[] stops1 = new Stop[] {
                    new Stop(0, colorstart),
                    new Stop(1,colorend)
            };
            LinearGradient lg1 = new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,stops1);

            this.setFill(lg1);
            DropShadow ds1 = new DropShadow();
            ds1.setOffsetX(1);
            ds1.setOffsetY(0);
            ds1.setRadius(20);
            ds1.setWidth(20);
            ds1.setHeight(0);
            this.setEffect(ds1);
        }
    }


// implementing iterable features for a collection of gradeComponents
    public class courseGrades implements Iterable<gradeComponent>{
        private final List<gradeComponent> gradeList = new ArrayList<gradeComponent>();
        @Override
        public Iterator<gradeComponent> iterator(){
            return gradeList.iterator();
        }

    }
    public static class gradeComponent{
        public String course_name;
        public String grade_name;
        public Double grade_recieved;
        public Double grade_weight;
        public Boolean isCompleted;
        public gradeComponent(String coursename,String gradename,Double graderecievedpercent, Double gradeweightpercent, Boolean iscompleted){
            this.course_name = coursename;
            this.grade_name = gradename;
            this.grade_recieved = graderecievedpercent;
            this.grade_weight = gradeweightpercent;
            this.isCompleted = iscompleted;
        }
        public String getName(){
            return this.grade_name;
        }
        public String getCourse(){
            return this.course_name;
        }
        public Double getRecieved(){
            return this.grade_recieved;
        }
        public Double getWeight(){
            return this.grade_weight;
        }
        public Boolean getIsCompleted()
        {
            return this.isCompleted;
        }

    }
}
