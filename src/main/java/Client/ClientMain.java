package Client;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane mpane = new Pane();


        Group graphgroup = new Group();
        Rectangle rect = new Rectangle(500.0,500.0);

        //rect.setFill(Color.BISQUE);
        graphgroup.getChildren().add(rect);
        courseGrades[] testgrades = new courseGrades[3];
        testgrades[0] = new courseGrades();
        testgrades[0].gradeList.add(new gradeComponent("Math","test 1",50d,10d,true));
        testgrades[0].gradeList.add(new gradeComponent("Math","test 2",30d,10d,true));
        testgrades[0].gradeList.add(new gradeComponent("Math","final",null,80d,false));

        testgrades[1] = new courseGrades();
        testgrades[1].gradeList.add(new gradeComponent("CSCI 2020","test 1",100d,10d,true));
        testgrades[1].gradeList.add(new gradeComponent("CSCI 2020","test 2",66d,15d,true));
        testgrades[1].gradeList.add(new gradeComponent("CSCI 2020","assignment 1",80d,25d,true));
        testgrades[1].gradeList.add(new gradeComponent("CSCI 2020","assignment 2",80d,25d,true));
        testgrades[1].gradeList.add(new gradeComponent("CSCI 2020","final",null,25d,false));

        testgrades[2] = new courseGrades();
        testgrades[2].gradeList.add(new gradeComponent("Physics 2","midterm",60d,10d,true));
        testgrades[2].gradeList.add(new gradeComponent("Physics 2","midterm 2",60d,15d,true));
        testgrades[2].gradeList.add(new gradeComponent("Physics 2","tutorial grade",60d,25d,true));
        testgrades[2].gradeList.add(new gradeComponent("Physics 2","labs",null,25d,false));
        testgrades[2].gradeList.add(new gradeComponent("Physics 2","final",null,25d,false));

        generateBarGraph ggraph = new generateBarGraph(testgrades,500,500);
        graphgroup = ggraph.getBarGraph();
        graphgroup.setLayoutY(10);
        graphgroup.setLayoutX(10);

        Scene mscene = new Scene(graphgroup,800,600);
        primaryStage.setTitle("Client");
        primaryStage.setScene(mscene);
        primaryStage.show();

    }

    // this is for creating all bars
    public class generateBarGraph{
        // TODO: make bar dimensions dependant on containor size
        public int graphHeight = 400; // should do something to ensure bars are normalized
        public int graphWidth = 50; // should be containor size / bars + spacers
        public int barWidth = 50;
        public courseGrades[] cgrades;

        public generateBarGraph(courseGrades[] studentgrades,int graphheight,int graphwidth){

            this.cgrades = studentgrades;
            this.graphHeight = graphheight;
            this.graphWidth = graphwidth;


        }
        // responsible for creating the components related to a single bar
        public Group getBarGraph(){
            Group bargroup = new Group();
            setBarLayout(bargroup);

            for(int i = 0; i < cgrades.length;i++){
                makeBar(cgrades[i],bargroup,i);
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
            int barx = barWidth * (offset * 2);

            // these lists are necessary for when clicking on a bar we get a granular breakdown
            List<String> completeditems = new ArrayList<String>();
            List<String> incompleteItems = new ArrayList<String>();

            // process what grades are marked and what are still possible
            // also store list of grade names for display purposes
            for(gradeComponent gc : course.gradeList){
                if(gc.isCompleted){
                    completedweight += (gc.getRecieved() * gc.getWeight() / 100);
                    completedAvg += gc.getRecieved();
                    completeditems.add(gc.getName());
                }
                else{
                    unfinishedweight += gc.getWeight();
                    incompleteItems.add(gc.getName());
                }
            }
            if(completeditems.size() > 0){
                completedAvg = completedAvg / completeditems.size();
            }
            if (incompleteItems.size() > 0 && completedAvg > 0) {
                expectedweight = unfinishedweight * completedAvg / 100;
            }

            // TODO: for now going to assume 100% is 400px
            Rectangle markedgrade = null;

            if(completedweight > 0){
                markedgrade = new Rectangle(barWidth,(graphHeight / 100) * Math.round(completedweight));

                markedgrade.setY(graphHeight - markedgrade.getHeight());// - markedgrade.getHeight());
                markedgrade.setX(barWidth);
                markedgrade.setLayoutX(barx);
                Stop[] stops1 = new Stop[] {
                        new Stop(0, new Color(0.2,0.2,1,0.75)),
                        new Stop(1,new Color(.8,.8,1,0.65))
                };
                LinearGradient lg1 = new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,stops1);
                markedgrade.setFill(Color.RED);
                markedgrade.setFill(lg1);
                DropShadow ds1 = new DropShadow();
                ds1.setOffsetX(1);
                ds1.setOffsetY(0);
                ds1.setRadius(20);
                ds1.setWidth(20);
                ds1.setHeight(0);
                markedgrade.setEffect(ds1);
                markedgrade.setOnMouseEntered( x -> { setMouseHoverScale((Rectangle)x.getSource(),true);});
                markedgrade.setOnMouseExited(x -> { setMouseHoverScale((Rectangle)x.getSource(),false);});
                basegroup.getChildren().add(markedgrade);
            }
            Rectangle expectedgrade = null;
            if(expectedweight > 0)
            {

                expectedgrade = new Rectangle(barWidth,(graphHeight / 100) * Math.round(expectedweight));
                if(markedgrade != null) {
                    expectedgrade.setY(graphHeight - markedgrade.getHeight() - expectedgrade.getHeight());
                } else {
                    expectedgrade.setY(graphHeight- expectedgrade.getHeight());
                }
                expectedgrade.setX(barWidth);
                expectedgrade.setLayoutX(barx);
                Stop[] stops2 = new Stop[] {
                        new Stop(0, new Color(0.1,0.8,0.1,0.6)),
                        new Stop(1,new Color(0.9,0.9,0.9,0.4))
                };
                LinearGradient lg2 = new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,stops2);
                expectedgrade.setFill(lg2);
                DropShadow ds2 = new DropShadow();
                ds2.setOffsetX(1);
                ds2.setOffsetY(0);
                ds2.setRadius(20);
                ds2.setWidth(20);
                ds2.setHeight(0);
                expectedgrade.setEffect(ds2);

                basegroup.getChildren().add(expectedgrade);
            }
            Rectangle unmarkedgrade;
            Double remainingheight = unfinishedweight - expectedweight;
            if(remainingheight > 0)
            {

                unmarkedgrade = new Rectangle(barWidth,(graphHeight / 100) * Math.round(remainingheight));
                if(expectedweight != null) {
                    unmarkedgrade.setY(expectedgrade.getY() - unmarkedgrade.getHeight());
                } else {
                    unmarkedgrade.setY(graphHeight- unmarkedgrade.getHeight());
                }
                unmarkedgrade.setX(barWidth);
                unmarkedgrade.setLayoutX(barx);
                Stop[] stops2 = new Stop[] {
                        new Stop(0, new Color(0.8,0.8,0.8,0.2)),
                        new Stop(1,new Color(0.9,0.9,0.9,0.1))
                };
                LinearGradient lg2 = new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,stops2);
                unmarkedgrade.setFill(lg2);
                DropShadow ds2 = new DropShadow();
                ds2.setOffsetX(1);
                ds2.setOffsetY(0);
                ds2.setRadius(20);
                ds2.setWidth(20);
                ds2.setHeight(0);
                unmarkedgrade.setEffect(ds2);

                basegroup.getChildren().add(unmarkedgrade);
            }
            //return basegroup;
            // TODO: calculate projected grade

        }
        void setMouseHoverScale(Rectangle rect,boolean isOn){
            if(isOn == true){
                rect.setWidth(rect.getWidth() + 20);
            }else {
                rect.setWidth(rect.getWidth() - 20);
            }
        }

    }


    public class GradeGraph{
        public class gradeRectangle extends Rectangle{
            List<String> namedComponents = new ArrayList<String>();
        }
    }

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
