package SFTPClient;

import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CommandLineInterface {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger( "Commands" );
    private String command;
    private String userName;
    private String password;
    private final String HOST = "104.248.67.51";
    private static boolean enableLogging = false;

    public SFTPConnection ourConnection;

    private StringBuilder greeting = new StringBuilder("Welcome to the SFTP Client interface." +
            "\n\tEnter '-help to a list of available commands" +
            "\n\tor enter '-c' to connect...");
    public StringBuilder menu = new StringBuilder("THIS IS THE MENU:" +
            "\n\t-help\tprints help menu" +
            "\n\t-c\t\tconnects SFTP server" +
            "\n\t\tlsr\t\t\t\tlists contents of current remote directory" +
            "\n\t\tlsl\t\t\t\tlists contents of current local directory" +
            "\n\t\tpwdr\t\t\tprints remote working directory" +
            "\n\t\tpwdl\t\t\tprints local working directory" +
            "\n\t\tdl <fileName>\tdownload <fileName> from current remote directory to current local directory" +
            "\n\t\tul <fileName>\tupload <fileName> to current remote directory from current local directory" +
            "\n\t-d\t\tdisconnects SFTP server" +
            "\n\t-q\t\tquit SFTP client interface" +
            "\n\n\tmore menu options coming soon...");




    CommandLineInterface(){}


    public static void main(String ... args){
        if(!enableLogging)
        {
            LogManager.getLogManager().reset();
        }

        LOGGER.log( Level.INFO, "Starting program");
        // instantiate new CLI object
        CommandLineInterface mainCLI = new CommandLineInterface();
        System.out.println(mainCLI.getGreeting());  // prints greeting

        // retrieves command option from System.in
        mainCLI.setCommand();

        while (true){
//            if (mainCLI.ourConnection.isConnected()){
//                //TODO: refactor CLI use of optionsManager 'while-loop' into its own method for re-use here
//            }
            mainCLI.ftpClientManager(mainCLI.getCommand());
        }




    }


    public void ftpClientManager(String command){

        switch(command){
            case ("-help"):
                System.out.println(getMenu());
                setCommand();
                break;
            case ("-c"):
                setUserNameAndPassword();
                ourConnection = new SFTPConnection(getUsername(), HOST, getPassword());
                ourConnection.connect();
                if (!ourConnection.isConnected()){
                    System.out.println("Failed to connect, please try again.");
                    setCommand();
                    break;
                }
                // TODO: re-enter options manager if '-help' received after successful '-c' connection. Might need to restructure location of optionsManager
                else{
                    System.out.println("Connection successful! Enjoy your files, stupid.");
                    setCommand();
                    while(true){
                        if(getCommand().charAt(0) == '-'){
                            break; //break while loop for non-SFTP client commands (i.e. '-q', '-help')
                        }
                        try{
                            ourConnection.optionsManager(getCommand()); // if command doesn't throw sftp exception, executes SFTP navigation commands in SFTP optionsManager method

                        }
                        catch(SftpException shit){ // if SftpException thrown, print exception, followed by help message, then retrieve new command after disconnecting from SFTP server
                            System.err.println(shit.getMessage());
                            if(ourConnection.isConnected()){
                                ourConnection.disconnect();
                            }
                            System.out.println("Connection failed... enter '-c' to reconnect, '-q' to quit', or '-help' a list of available options\n");
                            setCommand();
                            break; //break while loop with new command
                        }

                        setCommand();


                    }
                    break;
                }
            case("-d"):
                if(ourConnection != null && ourConnection.isConnected()){
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
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command. Enter '-help' for list of available commands or '-q' to exit.");
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
        return command;
    }

    public String getGreeting(){
        return greeting.toString();
    }

    public void setUserNameAndPassword(){
        Scanner input = new Scanner(System.in);
        System.out.println("If you would like to use a previous log in type -c, otherwise type -l: ");
        String answer = input.nextLine();
        if(answer.equals("-c"))
        {
            LOGGER.log( Level.INFO, "Reading log in information from file");
            readCredentialsFromDisk();
        }else{
            System.out.println("Username: ");
            userName = input.nextLine();
            System.out.println("Password: ");
            password = input.nextLine();
            System.out.println("Would you like to save this log in(y/n):");
            answer = input.nextLine();
            if(answer.equals("y"))
            {
                writeCredentialsToDisk(HOST,userName,password);
            }
        }
    }

    private void readCredentialsFromDisk()//TODO encrypt file
    {
        LOGGER.log( Level.INFO, "Reading log in information from file");
        List<String []> listOfCreds = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("Connections.txt")));
            String tempCreds;
            while ((tempCreds = br.readLine()) != null) {
                String creds[] = new String[3];
                creds = tempCreds.split(" ");
                for(int i = 0; i< 3;i++)
                {
                    listOfCreds.add(creds);
                }
            }
        }catch (Exception e)
        {
            System.out.println("Something went wrong reading from disk");
            LOGGER.log( Level.SEVERE, "Something went wrong while reading from file: "+e.getMessage());
        }
        System.out.println("Select the log in you would like to use:");
        int logOnAmounts = listOfCreds.size();
        for(int i = 0;i<logOnAmounts;i++)
        {
            String [] creds = listOfCreds.get(i);
            System.out.println("["+(i+1)+"] "+creds[1]+" : "+creds[0]);
        }
        Scanner input = new Scanner(System.in);
        int intInput = -1;
        do{
            try{
                intInput = input.nextInt();
                if(intInput < 0 || intInput > logOnAmounts)
                {
                    System.out.println("Enter a number between 0 and "+logOnAmounts);
                }
            }catch (Exception e){
                System.out.println("Enter a valid number please");
            }
        }while (intInput < 0 || intInput > logOnAmounts);
     //   HOST = listOfCreds.get(intInput)[0]; Uncomment this when we're ready
        userName = listOfCreds.get(intInput-1)[1];
        password = listOfCreds.get(intInput-1)[2];
    }
    private void writeCredentialsToDisk(String HOST,String userName,String password)
    {
        try{
            LOGGER.log( Level.INFO, "Writing log in information to file");
            FileWriter fw = new FileWriter(new File("Connections.txt"),false);
            fw.write(HOST+" ");
            fw.write(userName+" ");
            fw.write(password+" ");
            fw.write("/\n");
            fw.close();
        }catch (Exception e){
            System.out.println("Something went wrong writing to a file");
            LOGGER.log( Level.SEVERE, "Something went wrong while writing to file: "+e.getMessage());

        }
    }

    public String getUsername(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

}