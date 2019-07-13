package FTBClient;

import com.jcraft.jsch.SftpException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

//import java.lang.invoke.DirectMethodHandle$Holder;

public class CommandLine {

    private static final Logger LOGGER = Logger.getLogger("CommandLine2");

    public static void main(String[] args){

        LOGGER.log(Level.INFO, "Starting Command Line");
        Scanner scanner = new Scanner(System.in);  // Reading from System.in
        System.out.println("Welcome to the SFTP Connector! To connect, type (c)");
        while(true) {
            String newConnection = scanner.nextLine();
            if (newConnection.equals("c")) {
                makeConnection();
            }else {break;}
        }
        scanner.close();
        LOGGER.log(Level.INFO, "exiting Command Line");
    }

    private static void makeConnection(){

        LOGGER.log(Level.INFO, "Entering makeConnection/gathering log on details");
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
        LOGGER.log(Level.FINE, "sftpConnection.username+ \" is attempting to connect to \"+sftpConnection.host");
           boolean connected = sftpConnection.Connect();

            if(connected){
                try {
                    Options.run(sftpConnection.sftpChannel);
                } catch (SftpException e) {
                    LOGGER.log(Level.SEVERE, "FAILURE: "+e.getMessage());
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
