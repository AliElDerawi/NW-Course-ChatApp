package chatappfn01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Ali El-Derawi @ Momen Zaqout
 */
public class client extends Application implements Initializable {
    // Initializable it is needed when handling FXML controller.

    @FXML
    TextArea txtAreaMessage;
    @FXML
    TextField txtFieldMessage;
    @FXML
    private Button btnSend;
    @FXML
    private ComboBox usersList;
    @FXML
    private ComboBox groupsList;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField TxtFieldAddGroup;

    @FXML
    private Button btnEnroll;
    int broadcast = 2;

    @FXML
    private RadioButton broadRadBox;

    @FXML
    private RadioButton singleRadBox;

    @FXML
    private RadioButton groupRadBox;

    @FXML
    private Label userNameW;

    String user_name;
    private static final int PORT = 9010;
    BufferedReader in;
    static PrintWriter out;
    Socket socket;
    ObservableList<String> userNames = FXCollections.observableArrayList();
    ObservableList<String> groupNames = FXCollections.observableArrayList();
    ObservableList<String> group_enrolled = FXCollections.observableArrayList();
    ObservableList<String> useres_enrolled = FXCollections.observableArrayList();
    static String singleMessage;
    static String groupMessage;
    public static ToggleGroup toggleGroup = new ToggleGroup();
    static boolean singleM;
    static boolean groupM;
    static boolean broadM;
    String group_selected;
    static String usersInsideGroup;

    int exist = 0;
    private static int x = 0;
    private static boolean userEnrolled = false;
    Stage s1;
    @FXML
    private ListView<String> users_enrl_ListView;
    @FXML
    private ListView<String> groupListView;

    @Override
    public void start(Stage stage) throws Exception {
        s1 = stage;
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    private void run() throws IOException {

        socket = new Socket("localhost", PORT);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); // Or use System.in
        // StandardCharsets.UTF_8

        out = new PrintWriter(socket.getOutputStream(), true); // Or DataOutputStream.
        // boolean autoFlush : automatic line flushing, from an existing OutputStream

        new Thread() {

            @Override
            public void run() {

                try {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // Make some cooling for the Chat App
                            txtAreaMessage.setEditable(false);
                            broadRadBox.setToggleGroup(toggleGroup);
                            singleRadBox.setToggleGroup(toggleGroup);
                            groupRadBox.setToggleGroup(toggleGroup);
                            broadRadBox.setSelected(true);

                        }
                    });

                    while (true) {
                        String line = in.readLine();
                        // read the line receving from the server
                        // the server must be running first
                        if (line != null) {
                            System.out.println("line is : " + line);
                            if (line.startsWith("SUBMIT YOUR USER NAME")) {
                                out.println(user_name);
                                System.out.println("user_name " + user_name);
                            } else if (line.startsWith("NAME ACCEPTED")) {
                                System.out.println("Name is Accepted");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        userNameW.setText(user_name);
                                    }
                                });
                                // To refresh the list of Online User when a user access to the system.
                                out.println("REFRESH USER LIST");
//                                To refresh the list of available group when a user access to the system.
                                out.println("REFRESH GROUP LIST");

                            } else if (line.startsWith("NAME NOT ACCEPTED")) {
                                System.out.println("user name is exsist");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage stage = (Stage) TxtFieldAddGroup.getScene().getWindow();
                                        stage.close();
                                    }
                                });
                                JOptionPane.showMessageDialog(null, "NAME EXIST");

                                break;

                            } else if (line.startsWith("newname:")) {
                                System.out.println("Reresh User List in Progress");
//                                  We will just add here the new name for the ArrayList for all users
//                                  We will reresh th UI later using Runnable.
//                                  Anthoer solution is to include a method.
//                               
                                String[] newnames = line.substring(8).split("/"); // Remove newname: and /
                                System.out.println("new name is : " + line.substring(8));

                                for (String string : newnames) {
                                    System.out.println("new name : " + string);
                                    NAMES.add(string); // Now we will add the new user for the list 
//                                            of online users, and prevent duplicate in user name there.
                                }
//                                   
                            } else if (line.startsWith("MESSAGE")) {
                                System.out.println("line.substring(8) " + line.substring(8));
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
//                               
                                        txtAreaMessage.insertText(0, line.substring(8) + "\n");
//                                  
                                    }

                                });

                            } else if (line.startsWith("GROUP CREATED")) { // Let's start tracing from the button create group.
                                System.out.println("Adding Group to list is in progress");
                                out.println("REFRESH GROUP LIST"); // All available group list.

                            } else if (line.startsWith("NEWGROUP:")) {
                                System.out.println("Now Add this pala pala to the group");
                                System.out.println(line);
//                                We will just add here the new name for the ArrayList for all users
//                                We will reresh th UI later using Runnable.
//                                Anthoer solution is to include a method.
                                String[] newnames = line.substring(9).split("/");
                                System.out.println("new name is : " + line.substring(9));
                                for (String string : newnames) {
                                    System.out.println("new name : " + string);
                                    NAMES.addGroup(string); // Now we will add the new group 
//                                             for the list of all available group and prevent duplicating there. 
//                                             
                                }
//                                 
//                             
                            } else if (line.startsWith("GROUP enrolled")) {
                                System.out.println("Adding enrolled Group to combo is in progress");
                                String[] parts = line.split("-");
                                String GroupName = parts[1];
                                refresh_combo(GroupName);
                            } else if (line.startsWith("users_enrolled")) {
                                String[] parts = line.split("-");
                                usersInsideGroup = parts[1];
                                refresh_users_enrolled(usersInsideGroup);
                            } else if (line.startsWith("No Member Inside The Group")) {
                                // to load empty member when the user want to see the member of empty group.
                                refresh__empty_users_enrolled();
                            } else {
                                return;
                            }

                            Platform.runLater(new Runnable() { // Now refresh Users List , Group List
//                                 make method to do this job.
                                @Override
                                public void run() {

                                    userNames.clear();
                                    for (String name : NAMES.names_arl) {
                                        if (!name.equals(user_name)) {

                                            userNames.add(name);

                                        }
                                    }

                                    usersList.setItems(userNames);

                                    groupNames.clear();
                                    for (String name : NAMES.names_arl_group) {
                                        groupNames.add(name);
                                    }

                                    groupListView.setItems(groupNames);

                                }

                            });

                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
                .start();
    }

    private void refresh_combo(String GroupName) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                group_enrolled.add(GroupName);
                groupsList.setItems(group_enrolled);
            }
        });
    }

    private void refresh_users_enrolled(String usersName) {
        System.out.println("USERS ENROLLED : " + usersName);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String[] u = usersName.split("/");
                useres_enrolled.clear();
                for (String userName : u) {
                    useres_enrolled.add(userName);
                }
                users_enrl_ListView.setItems(useres_enrolled);

            }

        });
    }

    private void refresh__empty_users_enrolled() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                useres_enrolled.clear();
                users_enrl_ListView.setItems(useres_enrolled);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
// All of the predefined listner and method.
        groupListView.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                System.out.println("view member");
                out.println("REFRESH Group Enrolled Member List" + "-" + group_selected);
            }

        });

        groupListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                group_selected = newValue;
            }
        });

        usersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    singleMessage = newValue;
                    System.out.println("You have Select Name : " + newValue);
                }
            }

        });

        groupsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                groupMessage = newValue;
                System.out.println("You have Select a group Name : " + newValue);

            }
        });

        toggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            singleM = false;
            groupM = false;
            broadM = false;
            Toggle selectedToggle = toggleGroup.getSelectedToggle();
            if (selectedToggle == singleRadBox) {
                singleM = true;
                System.out.println("You selected Single Message");
            } else if (selectedToggle == groupRadBox) {
                groupM = true;
                System.out.println("You Selected Group Message");
            } else if (selectedToggle == broadRadBox) {
                broadM = true;
                System.out.println("You Selected Broad Message");
            }
        });

        txtFieldMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    send_();
                }
            }

        });
        


        btnSend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                send_();
            }

        });
        
        btnEnroll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                enroll();
            }

        });

        enter_name();
    }

    void enter_name() {
        String name = JOptionPane.showInputDialog("User Name");

        if (name != null & !name.equals("")) {
            user_name = name;
            try {
                run();

            } catch (IOException ex) {
                Logger.getLogger(client.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            enter_name();
        }

    }

    void enter_name_exist() {
        String name = JOptionPane.showInputDialog("Unique User Name");

        if (name != null & !name.equals("")) {
            user_name = name;
            out.println(user_name);
            out.println("REFRESH USER LIST");
            out.println("REFRESH GROUP LIST");
        } else {
            System.out.println("close");
            enter_name_exist();
        }

    }

    @FXML
    private void create(ActionEvent event) {
        String g_name = TxtFieldAddGroup.getText();
        if (!g_name.isEmpty()) { // to prevent enter a empty group name
            out.println("CREATE NEW GROUP" + "-" + g_name);
            System.out.println("Create a new Group is in processing");
            TxtFieldAddGroup.setText("");

        } else {
            JOptionPane.showMessageDialog(null, "Please Enter Group Name !");
        }
    }

    @FXML
    private void enroll() {

        if (group_selected != null) { // to prevent enter a empty group name

            out.println("enroll to a GROUP" + "-" + group_selected);
            System.out.println("enroll to a Group is in processing");

        } else {
            JOptionPane.showMessageDialog(null, "Please select Group Name !");

        }
    }

    public static void main(String[] args) {

        launch(args);

    }

    @FXML
    private void send_() {

        if (singleM) {
            if (singleMessage != null) {
                out.println("1" + singleMessage + "-" + txtFieldMessage.getText());
                txtFieldMessage.setText("");
                System.out.println("Single Message is Done");
            }

        } else if (groupM) {
            // adding user name to show the name of user who send the message in the group
            if (groupMessage != null) {

                out.println("3" + groupMessage + "-" + user_name + " : " + txtFieldMessage.getText());
                txtFieldMessage.setText("");
                System.out.println("Group Message is Done");
            }

        } else {
            out.println("2" + txtFieldMessage.getText());
            System.out.println("Broad Cast Message is Done");
            txtFieldMessage.setText("");
        }

    }
}
