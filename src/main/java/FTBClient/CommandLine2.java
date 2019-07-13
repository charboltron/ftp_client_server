package FTBClient;

import com.jcraft.jsch.*;

import javax.swing.text.html.Option;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;

public class CommandLine2 {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);  // Reading from System.in

        System.out.println("Welcome to the SFTP Connector! To connect, type (c)");
        while(true) {

            String newConnection = scanner.nextLine();
            if (newConnection.equals("c")) {
                makeConnection();
            }else {break;}
        }
        scanner.close();
    }

    private static void makeConnection(){


        //hard-coded for now
        System.out.println("Enter your username: ");
        String user = "agilesftp";
        //String user = scanner.nextLine(); // Scans the next token of the input as an int once finished

        System.out.println(("Enter a hostname: "));
        String host = "104.248.67.51";
        //String host = scanner.nextLine();

        System.out.println("Enter your password (It will not be masked!): ");
        String pwd = "totsReal"; 
        //String pwd = scanner.nextLine();

        SFTPConnection sftpConnection = new SFTPConnection(user,host,pwd);
        System.out.println(sftpConnection.username+ " is attempting to connect to "+sftpConnection.host);

           boolean connected = sftpConnection.Connect();

            if(connected){
                try {
                    Options.run(sftpConnection.sftpChannel);
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
                sftpConnection.Disconnect();
                System.out.println("Connection Closed. Open a new connection (c) or quit? (q):  ");

            }else{

                System.out.println("Connection failed. Open a new connection (c) or quit? (q):  ");
            }



    }
}
