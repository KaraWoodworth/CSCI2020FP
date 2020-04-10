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
        generateBarGraph ggraph = new generateBarGraph();
        graphgroup = ggraph.makeBar();

        Scene mscene = new Scene(graphgroup,800,600);
        primaryStage.setTitle("Client");
        primaryStage.setScene(mscene);
        primaryStage.show();

    }

    // this is for creating all bars
    public class generateBarGraph{
        // TODO: make bar dimensions dependant on containor size
        public int barheight = 400; // should do something to ensure bars are normalized
        public int barwidth = 50; // should be containor size / bars + spacers
        public courseGrades cgrades;

        public generateBarGraph(){

            cgrades = new courseGrades();
            cgrades.gradeList.add(new gradeComponent("Math","test 1",4.4,10.0,true));
            cgrades.gradeList.add(new gradeComponent("Math","test 2",8.5,10.0,true));
            cgrades.gradeList.add(new gradeComponent("Math","final",null,80.0,false));
            makeBar();
        }
        // responsible for creating the components related to a single bar
        public Group makeBar(){
            Double completedweight = 0.0;
            Double unfinishedweight = 0.0;

            // these lists are necessary for when clicking on a bar we get a granular breakdown
            List<String> completeditems = new ArrayList<String>();
            List<String> incompleteItems = new ArrayList<String>();

            // process what grades are marked and what are still possible
            // also store list of grade names for display purposes
            for(gradeComponent gc : cgrades.gradeList){
                if(gc.isCompleted){
                    completedweight += gc.getRecieved();
                    completeditems.add(gc.getName());
                }
                else{
                    unfinishedweight += gc.getMaxGrade();
                    incompleteItems.add(gc.getName());
                }
            }

            // TODO: for now going to assume 100% is 400px
            Rectangle markedgrade = null;
            Rectangle unmarkedgrade;
            Group tmp = new Group();
            tmp.setLayoutY(10);
            tmp.setLayoutX(10);
            Line yaxis = new Line(0,0,0,barheight);
            Line xaxis = new Line(0,barheight,barheight,barheight);
            Line[] gradeaxis = new Line[5];
            Label[] gradelables = new Label[5];
            gradeaxis[0] = new Line(0,barheight * 0.5,barheight,barheight * 0.5);
            gradeaxis[1] = new Line(0,barheight * 0.4,barheight,barheight * 0.4);
            gradeaxis[2] = new Line(0,barheight * 0.3,barheight,barheight * 0.3);
            gradeaxis[3] = new Line(0,barheight * 0.2,barheight,barheight * 0.2);
            gradeaxis[4] = new Line(0,barheight * 0.1,barheight,barheight * 0.1);

            gradelables[0] = new Label("50%");
            gradelables[0].setLayoutX(5);
            gradelables[0].setLayoutY(barheight * 0.5 - 15);
            gradelables[1] = new Label("60%");
            gradelables[1].setLayoutX(5);
            gradelables[1].setLayoutY(barheight * 0.4 - 15);
            gradelables[2] = new Label("70%");
            gradelables[2].setLayoutX(5);
            gradelables[2].setLayoutY(barheight * 0.3 - 15);
            gradelables[3] = new Label("80%");
            gradelables[3].setLayoutX(5);
            gradelables[3].setLayoutY(barheight * 0.2 - 15);
            gradelables[4] = new Label("A+");
            gradelables[4].setLayoutX(5);
            gradelables[4].setLayoutY(barheight * 0.1 - 15);

            for(int i = 0; i < 5; i++){
                gradeaxis[i].setStrokeDashOffset(2);
                gradeaxis[i].getStrokeDashArray().addAll(5d);
                gradeaxis[i].setStroke(new Color(0.8,0.8,.8,1));
                gradeaxis[i].setStrokeWidth(2);
                tmp.getChildren().add(gradeaxis[i]);
                tmp.getChildren().add(gradelables[i]);
            }


            yaxis.setStroke(Color.BLACK);
            xaxis.setStroke(Color.BLACK);
            tmp.getChildren().addAll(yaxis,xaxis);

            if(completedweight > 0){
                markedgrade = new Rectangle(barwidth,(barheight / 100) * Math.round(completedweight));
                markedgrade.setY(barheight - markedgrade.getHeight());// - markedgrade.getHeight());
                markedgrade.setX(barwidth);
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

                tmp.getChildren().add(markedgrade);
            }
            if(unfinishedweight > 0)
            {
                unmarkedgrade = new Rectangle(barwidth,(barheight / 100) * Math.round(unfinishedweight));
                if(markedgrade != null) {
                    unmarkedgrade.setY(barheight - markedgrade.getHeight() - unmarkedgrade.getHeight());
                } else {
                    unmarkedgrade.setY(barheight- unmarkedgrade.getHeight());
                }
                unmarkedgrade.setX(barwidth);
                Stop[] stops2 = new Stop[] {
                        new Stop(0, new Color(0.1,0.8,0.1,0.6)),
                        new Stop(1,new Color(0.9,0.9,0.9,0.4))
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

                tmp.getChildren().add(unmarkedgrade);
            }
            return tmp;
            // TODO: calculate projected grade

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
        public Double getMaxGrade(){
            return this.grade_weight;
        }
        public Boolean getIsCompleted()
        {
            return this.isCompleted;
        }

    }
}
