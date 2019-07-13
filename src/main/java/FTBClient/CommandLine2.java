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

    }

    public static void makeConnection(){

        System.out.println("Enter your username: ");
        String user = "boltch";
        //String user = scanner.nextLine(); // Scans the next token of the input as an int once finished

        System.out.println(("Enter a hostname: "));
        String host = "linux.cecs.pdx.edu";
        //String host = scanner.nextLine();

        System.out.println("Enter your password (It will not be masked!): ");
        String pwd = "RpawkDZNT6ors2ZT";
        //String pwd = scanner.nextLine();

        SFTPConnection sftpConnection = new SFTPConnection(user,host,pwd);
        System.out.println(sftpConnection.username+ " is attempting to connect to "+sftpConnection.host);

        sftpConnection.Connect();

        Options options = new Options();
        try {
            options.run(sftpConnection.sftpChannel);
        }catch(SftpException e){
            System.err.println(e);
            System.exit(0);
        }

        sftpConnection.Disconnect();
        System.out.println("Connection Closed. Open a new connection (c) or quit? (q):  ");
    }
}
