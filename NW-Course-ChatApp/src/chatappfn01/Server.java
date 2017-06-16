/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatappfn01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import javax.swing.JOptionPane;

/**
 *
 * @author Ali El-Derawi @ Momen Zaqout
 */
public class Server {

    private static final int PORT = 9010;
    /**
     * The set of all names of clients in the chat room. Maintained so that we
     * can check that new clients are not registering name already in use. So
     * the name of the client is the ID.
     */
    private static HashSet<String> names = new HashSet<String>();

    /**
     * The set of all the print writers for all the clients. This set is kept so
     * we can easily broadcast messages.
     */
    private static Hashtable<String, PrintWriter> clientHashtable = new Hashtable<String, PrintWriter>();
    private static Hashtable<String, ArrayList<String>> groubHashTable = new Hashtable<String, ArrayList<String>>();
    private static ArrayList<String> groupNameList;
    private static boolean UniqueGroupName;
    

    /**
     * The application main method, which just listens on a port and spawns
     * handler threads.
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("file.encoding", "UTF-8");
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class. Handlers are spawned from the listening loop and
     * are responsible for a dealing with a single client and handling its
     * messages.
     */
    private static class Handler extends Thread {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Constructs a handler thread, squirreling away the socket. All the
         * interesting work is done in the run method.
         */
        public Handler(Socket socket) {
            this.socket = socket; // New socket for server process to communicate with that particular client
        }

        /**
         * Services this threads client by repeatedly requesting a screen name
         * until a unique one has been submitted, then acknowledges the name and
         * registers the output stream for the client in a global set, then
         * repeatedly gets inputs and handle them.
         */
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), "UTF-8")); // An input stream is attached to some input source 
                out = new PrintWriter(socket.getOutputStream(), true); // An output stream is attached to an output source

                //Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {

                    out.println("SUBMIT YOUR USER NAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                          
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        } else {
                            out.println("NAME NOT ACCEPTED");

                        }
                    }
                }

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAME ACCEPTED");

                clientHashtable.put(name, out);

               
                String name2 = in.readLine();

                if (name2.equals("REFRESH USER LIST")) {

                    Enumeration<String> enumKey2 = clientHashtable.keys();
                    String newName = "";
                    while (enumKey2.hasMoreElements()) {
                        String userID = enumKey2.nextElement();
                        System.out.println("userID : " + userID);
                        newName += userID + "/";
//                      Print Console Just for debugging purpose u can delete it and the others !
                        System.out.println("Before the PrintWriter: " + newName);

                        Enumeration<String> enumKey3 = clientHashtable.keys();
                        while (enumKey3.hasMoreElements()) {
                            userID = enumKey3.nextElement();
                            PrintWriter writer = clientHashtable.get(userID);
                            if (writer != null) {
                                writer.println("newname:" + newName);
                                // To make the list of all online users correct and consistence for all users 
                                // include new users , we send the list of all avialble users to every user input reader.
                                // so we prevent the mistake of updating the list for only the current users.
                                System.out.println("Aftre the PrintWriter: " + newName);
                            }
                        }
                    }
                }
                while (true) {
                    String input = in.readLine();

                    if (input == null) {
                        return;
                    } 
                    // if the message start with (1) and the condition was satisfied
                    // Single message was selected , then send it to the specified user
                    // and to himself !
                   
                    if (input.startsWith("1")) {
                        System.out.println("" + input);
                        input = input.substring(1, input.length());
                        System.out.println("" + input);
                        String[] parts = input.split("-");
                        String userName = parts[0]; 
                        String message = parts[1];
                        System.out.println("" + userName);
                        System.out.println("" + message);
                        if (userName != null) {
                        // get the output stream for the specefied user and send 
                        // the message to him
                            PrintWriter pw = clientHashtable.get(userName);
                            pw.println("MESSAGE " + name + ": " + message);
//                            Send the message to himself also.
                            PrintWriter writer = clientHashtable.get(name);
                            writer.println("MESSAGE " + name + ": " + message);

                        }
                        // if BroadCast, it's pretty simple !
                    } else if (input.startsWith("2")) {
                        Enumeration<String> enumKey = clientHashtable.keys();
                        while (enumKey.hasMoreElements()) {
                            String id = enumKey.nextElement();
                            // get the output stream for all users
                            PrintWriter writer = clientHashtable.get(id);
                            if (writer != null) {
                                writer.println("MESSAGE " + name + ": " + input.substring(1, input.length()));
                            }
                        }
                        // if GroupMessage 
                    } else if (input.startsWith("3")) {
                        System.out.println("Server : Sending a message to the group");
                        System.out.println("" + input);
                        input = input.substring(1, input.length());
                        System.out.println("" + input);
                        String[] parts = input.split("-");
                        String groupName = parts[0];
                        String message = parts[1];
                        System.out.println("" + groupName);
                        System.out.println("" + message);
                        if (groupName != null) {
                            ArrayList<String> groupUsers = groubHashTable.get(groupName);
                            System.out.println("" + groupUsers);
                            for (String groupUser : groupUsers) {
                                System.out.println("" + groupUser);
                                PrintWriter pw = clientHashtable.get(groupUser);
                                pw.println("MESSAGE " + message);

                            }
                        }

                    } else if (input.startsWith("CREATE NEW GROUP")) {
                        System.out.println("Server : Create a new Group is in processing");
                        String[] parts = input.split("-");
                        String newGroupName = parts[1];
                        UniqueGroupName = true;
                        Enumeration<String> enumKey3 = groubHashTable.keys();

                        while (enumKey3.hasMoreElements()) {
                            String groupName = enumKey3.nextElement();
                            System.out.println("Inside Checking for Unique Group Name");

                            if (groupName.equals(newGroupName)) {
                                UniqueGroupName = false;
                                JOptionPane.showMessageDialog(null, "The Group is already exists");
                                System.out.println("Not Unique");
                            }
                        }

                        if (UniqueGroupName) {
                            System.out.println("Unique");
                            groupNameList = new ArrayList<>();
                            groubHashTable.put(newGroupName, groupNameList);
                            System.out.println("Group Created Sucsessfully");
                            out.println("GROUP CREATED");
                        }

                    } else if (input.equals("REFRESH GROUP LIST")) {
                        Enumeration<String> enumKey2 = groubHashTable.keys();
                        String groupName = "";
                        while (enumKey2.hasMoreElements()) {
                            String id = enumKey2.nextElement();
                            System.out.println("groupID : " + id);
                            groupName += id + "/";
//                  
                            // TO Make the list of all available group correct and consistence for all users 
                            // include new users , se the list of all avialble group to the users input reader.
                            // so we prevent the mistake of updating the list for only the current users.
                            Enumeration<String> enumKey3 = clientHashtable.keys();
                            while (enumKey3.hasMoreElements()) {
                                id = enumKey3.nextElement();
                                PrintWriter writer = clientHashtable.get(id);
                                if (writer != null) {
                                    System.out.println("Group Refresed : " + groupName);
                                    writer.println("NEWGROUP:" + groupName);
                                }
                            }
                        }
                    } else if (input.startsWith("REFRESH Group Enrolled Member List")) {
                        String[] parts = input.split("-");
                        String GroupName = parts[1];
                        String users_enrolled = "";

                        ArrayList<String> arl = groubHashTable.get(GroupName);

                        for (String name : arl) {
                            System.out.println("string : " + name);
                            users_enrolled += name + "/";
                        }
                        if (!users_enrolled.isEmpty()) {
                            out.println("users_enrolled" + "-" + users_enrolled);

                        } else {
                            out.println("No Member Inside The Group");
                        }

                    } else if (input.startsWith("enroll to a GROUP")) {
                        System.out.println("Server : enroll to a GROUP is in processing");
                        String[] parts = input.split("-");
                        String GroupName = parts[1];
                        Enumeration<String> enumKey3 = groubHashTable.keys();

                        while (enumKey3.hasMoreElements()) {
                            String groupName = enumKey3.nextElement();
                            if (groupName.equals(GroupName)) {
                                ArrayList<String> arl = groubHashTable.get(GroupName);
                                boolean userEnrolled = false;
                                x: for (String userName : arl) {
                                    if (userName.equals(name)) {
                                        userEnrolled = true;
                                    }
                                }
                                if (!userEnrolled) { // Enroll to the group if you are not already enrolled
                                    System.out.println("You Are Not Enrolled");
                                    arl.add(name);
                                    groubHashTable.remove(GroupName);
                                    groubHashTable.put(GroupName, arl);
                                    out.println("GROUP enrolled" + "-" + GroupName);

                                }

                            }

                        }

                    }
                }

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    clientHashtable.clear();
                    groubHashTable.clear();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
