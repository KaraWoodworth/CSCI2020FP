package Server;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

public class ServerMain extends Application {

    public int port = 8000;
    public String data = "";
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane mpane = new Pane();
        Scene mscene = new Scene(mpane,800,600);
        primaryStage.setTitle("Server");
        primaryStage.setScene(mscene);
        primaryStage.show();
        //readCSV("StudentData.txt");
        startServer();

    }
    public String readCSV(String filename){
        // looks in resource folder for specified file name
        String csvdata = "";
        try{
            ClassLoader classloader = getClass().getClassLoader();
            URL resource = classloader.getResource(filename);
            File fileobj = new File(resource.getFile());
            Scanner scanner = new Scanner(fileobj);
            while(scanner.hasNextLine()){
                csvdata += scanner.nextLine();
                // if there is going to be another row, we add a new line
                if(scanner.hasNextLine()){
                    csvdata += "\n";
                }
            }
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        csvdata = csvdata.replace("\"","");
        return csvdata;
    }

    public void startServer(){
        new Thread ( () -> {
            try {
                data = readCSV("StudentData.txt");
                ServerSocket server = new ServerSocket(port);
                while(true){
                    Socket socket = server.accept();
                    new Thread(new HandlecClient(socket)).start();
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }).start();
    }
    class HandlecClient implements Runnable {
        private Socket sckt;
        public HandlecClient(Socket socket){
            this.sckt = socket;
        }

        public void run() {
            try {
                //DataInputStream din = new DataInputStream(sckt.getInputStream());
                DataOutputStream dout = new DataOutputStream(sckt.getOutputStream());
                System.out.println("Client Connected");
                while(true) {
                    //stext.appendText( din.readUTF());
                    dout.writeUTF(data);

                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
