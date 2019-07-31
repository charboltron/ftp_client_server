package SFTPClient;

import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CommandLineInterface {

    private String command;
    private String userName;
    private String password;
    private String host;

    ArrayList<String> connectionCommands = new ArrayList<String> (Arrays.asList(
            "dirs", "lsr","lsr -al", "lsl", "cdr", "cdl", "pwdr", "mkdirr", "mkdirl", "mvl", "mvr", "rmdirr", "rmr", "chmodr", "dl", "dlm", "ul"));


    public SFTPConnection ourConnection;

    private StringBuilder greeting = new StringBuilder("Welcome to the SFTP Client interface." +
            "\n\tEnter '-help to a list of available commands" +
            "\n\tor enter '-c' to connect...");
    public StringBuilder menu = new StringBuilder("THIS IS THE MENU:" +
            "\n\t-help\tprints help menu" +
            "\n\t-c\t\tconnects SFTP server" +
            "\n\t\tdirs\t\t\tprints both local and remote working directories"+
            "\n\t\tlsr\t\t\t\tlists contents of current remote directory" +
            "\n\t\tlsr -al\t\t\tlists contents of current remote directory with permissions" +
            "\n\t\tlsl\t\t\t\tlists contents of current local directory" +
            "\n\t\tcdr\t\t\t\tchange remote directory" +
            "\n\t\tcdl\t\t\t\tchange local directory" +
            "\n\t\tmvr\t\t\t\trename or move a file or directory on remote server" +
            "\n\t\tmvl\t\t\t\trename or move a file or directory on local machine" +
            "\n\t\tpwdr\t\t\tprints remote working directory" +
            "\n\t\tpwdl\t\t\tprints local working directory" +
            "\n\t\tmkdirr\t\t\tmake directory on remote server" +
            "\n\t\tmkdirl\t\t\tmake directory on local machine" +
            "\n\t\trmdirr\t\t\tdelete directory on remote server" +
            "\n\t\trmr\t\t\t\tdelete file on remote server" +
            "\n\t\tchmodr\t\t\tchange remote file permissions" +
            "\n\t\tdl\t\t\t\tdownload from current remote directory to current local directory" +
            "\n\t\tdlm\t\t\t\tdownload multiple from current remote directory to current local directory" +
            "\n\t\tul\t\t\t\tupload to current remote directory from current local directory" +
            "\n\t-d\t\tdisconnects SFTP server" +
            "\n\t-q\t\tquit SFTP client interface" +
            "\n\n\tmore menu options coming soon..."); //Weirdly, this seems to print formatted differently from the terminal than inside IDEA. It's the ol' tabs vs spaces debate in action...


    CommandLineInterface(){}


    public static void main(String ... args) throws IOException {

        // instantiate new CLI object
        CommandLineInterface mainCLI = new CommandLineInterface();
        System.out.println(mainCLI.getGreeting());  // prints greeting

        // retrieves command option from System.in
        mainCLI.setCommand();

        while (true){
//            if (mainCLI.ourConnection.isConnected()){
//                //TODO: refactor CLI use of commandsManager 'while-loop' into its own method for re-use here
//            }
            mainCLI.ftpClientManager(mainCLI.getCommand());
        }
    }


    public void ftpClientManager(String command) throws IOException {

        switch(command){
            case ("-help"):
                System.out.println(getMenu());
                setCommand();
                break;
            case ("-c"):
                setUserNameAndPassword();
                ourConnection = new SFTPConnection(getUsername(), host, getPassword());
                ourConnection.connect();
                if (!ourConnection.isConnected()){
                    System.out.println("Failed to connect, please try again.");
                    setCommand();
                    break;
                }
                else{
                    System.out.println("Connection successful! Enjoy your files, stupid.");
                    setCommand();
                    while(true){
                        if(getCommand().charAt(0) == '-'){
                            if(getCommand().equals("-help")){
                                System.out.println(getMenu()); setCommand();
                            }
                            else{
                                break;
                            } //break while loop for non-SFTP client commands (i.e. '-q')
                        }
                        try{
                            ourConnection.commandsManager(getCommand()); // if command doesn't throw sftp exception, executes SFTP navigation commands in SFTP commandsManager method

                        }
                        catch(SftpException shit){ // if SftpException thrown, print exception, followed by help message, then retrieve new command after disconnecting from SFTP server
                            System.err.println(shit.getMessage());
                            System.out.println("Something went wrong, see the message above. Please try another command.");
                        }
                        setCommand();
                    }
                    break;
                }
            case("-d"):
                if(ourConnection != null && ourConnection.session.isConnected()){
                    ourConnection.disconnect();
                    System.out.println("Connection disconnected, enter '-q' to quit or '-help' to see available options\n");
                    setCommand();
                    break;
                }
                else{
                    System.out.println("No connection to disconnect. Enter '-c' to connect, '-q' to quit, or '-help' to see available options\n");
                    setCommand();
                    break;
                }

            case("-q"):
                if(ourConnection != null && ourConnection.session.isConnected()) {
                  ourConnection.disconnect();   // This check isn't strictly necessary, but it will stop errors from being thrown server side if the server side is poorly configured or extremely pedantic.
                }
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                if (connectionCommands.contains(getCommand())) {
                    System.out.println("You need to make a connection before you can use this command. Please type -c.");
                } else {
                    System.out.println("Unknown command. Enter '-help' for list of available commands or '-q' to exit.");
                }
                setCommand();
                break;
        }
    }

    public String getMenu(){
        return menu.toString();
    }

    public void setCommand(){
        System.out.printf("> ");
        Scanner input = new Scanner(System.in);
        command = input.nextLine();
        // input.close();
    }

    public String getCommand(){
        return command.trim();
    }

    public String getGreeting(){
        return greeting.toString();
    }

    public void setUserNameAndPassword(){
        //Scanner input = new Scanner(System.in);
        //System.out.println("Host: ");
        //host = input.nextLine();
        //System.out.println("Username: ");
        //userName = input.nextLine();
        //System.out.println("Password: ");
        //password = input.nextLine();

        //Hardcoded for ease of testing. Feel free to uncomment if you prefer to enter manually.
        host = "104.248.67.51"; //Hard-coded for now
        userName = "agilesftp";
        password = "SimpleAndSecureFileTransferProtocol";

    }

    public String getUsername(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

}
