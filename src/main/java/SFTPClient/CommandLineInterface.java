package SFTPClient;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * {@link CommandLineInterface} provides a command line interface for the SFTP server client.
 *
 */

public class CommandLineInterface {

    private String command;
    private String userName;
    private String password;
    private String host;
    private static String [] argz;
    private static boolean argzbool;
    private static boolean enableLogging = false;
    private String key = "This is a secret";
    private static ArrayList<String> CommandTests = new ArrayList<>((Arrays.asList("@Test_help", "@Test_quit", "@Test_disconnect_no_connect", "@Test_disconnect_connected")));
    private static final java.util.logging.Logger LOGGER = Logger.getLogger( "Commands" );
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

    /**
     * The {@link CommandLineInterface} main method instantiates a CLI object and passes the arguments from the command line using the setCommand method.
     * @param args        Any of the arguments specified in the menu printed by main.
     * @throws IOException
     */
    public static void main(String ... args) throws IOException {


        if(args.length!=0){argz = args; argzbool=true;}
        // instantiate new CLI object
        CommandLineInterface mainCLI = new CommandLineInterface();
        System.out.println(mainCLI.getGreeting());  // prints greeting
        if(argzbool && argz[0].equals("@Test_greeting")){
            System.exit(0);
        }

        // retrieves command option from System.in
        mainCLI.setCommand();

        while (true){
//            if (mainCLI.ourConnection.isConnected()){
//                //TODO: refactor CLI use of commandsManager 'while-loop' into its own method for re-use here
//            }
            mainCLI.ftpClientManager(mainCLI.getCommand());
        }
    }

    /**
     *  The <code>ftpClientManager</code> is passed the command variable set by the setCommand method, which it compares against the allowable switch cases.  ; only -help, -c, -d, -q are explicitly for, everything else falls into the <code>getCommand</code> method for evaluation.
     * @param command       Any of the allowable commands from the menu.
     * @throws IOException
     */
    public void ftpClientManager(String command) throws IOException {

        switch(command){
            case ("-help"):
                System.out.println(getMenu());
                if(argzbool && argz[0].equals("@Test_help")){
                    System.exit(0);
                }
                setCommand();
                break;
            case ("-c"):
                setUserNameAndPasswordFromFile();
                ourConnection = new SFTPConnection(getUsername(), host, getPassword());
                JSch jsch = new JSch(); //alright so you might be wondering why the hell. Well, the reason is Mockito. It was the only way I could get it going
                ourConnection.connect(jsch);
                if (!ourConnection.isConnected()){
                    System.out.println("Failed to connect, please try again.");
                    setCommand();
                    break;
                }
                else{
                    System.out.println("Connection successful! Enjoy your files, stupid.");
                    setCommand();
                    ourConnection.idleWake();
                    while(true){
                        if(getCommand().charAt(0) == '-'){
                            if(getCommand().equals("-help")){
                                System.out.println(getMenu()); setCommand(); ourConnection.idleWake();
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
                        ourConnection.idleWake();
                    }
                    break;
                }
            case("-d"):
                if(ourConnection != null && ourConnection.session.isConnected()){
                    ourConnection.disconnect();
                    System.out.println("Connection disconnected, enter '-q' to quit or '-help' to see available options\n");
                    ourConnection.timerCancel();
                    setCommand();
                    break;
                }
                else{
                    System.out.println("No connection to disconnect. Enter '-c' to connect, '-q' to quit, or '-help' to see available options\n");
                    if(argzbool && argz[0].equals("@Test_disconnect_no_connect")){
                        System.exit(0);
                    }
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

    /**
     * <code>getMenu</code> prints the menu options on the command line when invoked.
     * @return          the contents of the menu StringBuilder object, cast to a string.
     */
    public String getMenu(){
        return menu.toString();
    }

    /**
     * <code>setCommand</code> takes user input from the command line, and saves it into the command variable.
     */
    public void setCommand(){

        if (argzbool && CommandTests.contains(argz[0])){
            command = argz[1];
            return;
        }

        System.out.printf("> ");
        Scanner input = new Scanner(System.in);
        command = input.nextLine();
        // input.close();
    }

    /**
     * <code>getCommand</code> accesses the command variable saved by setCommand so it can be used in ftpClientManager and commandsManager.
     * @return      the contents of the command variable as a trimmed string
     */
    public String getCommand(){
        return command.trim();
    }

    /**
     * <code>getGreeting</code> accesses the contents of the StringBuilder 'greeting' and displays them when the CLI is instantiated.
     * @return       the contents of the greeting variable, cast to a string
     */
    public String getGreeting(){
        return greeting.toString();
    }

    /**
     * <code>setUserNameAndPassword</code> takes in the host name, username and password as input on the command line, and saves them to appropriate variables for use by the ftpClientManager method.
     */

    public void setUserNameAndPasswordFromFile(){
        Scanner input = new Scanner(System.in);
        System.out.println("If you would like to use a previous log in type -c, otherwise type -l: ");
        String answer = input.nextLine();
        boolean fromFile = false;
        if(answer.equals("-c")) {
            fromFile = true;
        }
        if(fromFile) {

            LOGGER.log(Level.INFO, "Reading log in information from file");
            fromFile = printCredientials();
        }

        if(!fromFile){
            System.out.println("Host: ");
            host = input.nextLine();
            System.out.println("Username: ");
            userName = input.nextLine();
            System.out.println("Password: ");
            password = input.nextLine();
            System.out.println("Would you like to save your log in information (y/n): ");
            answer = input.nextLine();
            if(answer.equals("y"))
            {
                writeCredentialsToDisk(host,userName,password);
            }
        }
    }

    static void EncryptDecryptFile(int cipherMode, String key, File inputFile, File outputFile){
        try {
            LOGGER.log(Level.INFO, "Entering Encrypt/Decrypt");
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error occured while in EncryptDecryptFile");
            e.printStackTrace();
        }
    }

    private List<String[]> readCredentialsFromDisk() {
        LOGGER.log(Level.SEVERE, "Reading credentials from disk");
        File secureFile = new File("Connections.txt");
        File exposedPassword = new File("temp.txt");
        List<String[]> listOfCreds = new ArrayList<>();
        synchronized (exposedPassword) {
            CommandLineInterface.EncryptDecryptFile(Cipher.DECRYPT_MODE, this.key, secureFile, exposedPassword);

            try {
                BufferedReader br = new BufferedReader(new FileReader(exposedPassword));
                String tempCreds;
                while ((tempCreds = br.readLine()) != null) {
                    String creds[];
                    creds = tempCreds.split(" ");
                    for (int i = 0; i < 3; i++) {
                        listOfCreds.add(creds);
                    }
                }
                br.close();
            } catch (Exception e) {
                System.out.println("Something went wrong reading from disk");
                LOGGER.log(Level.SEVERE, "Something went wrong while reading from file: " + e.getMessage());

            } finally {
                exposedPassword.delete();
            }
            return listOfCreds;
        }
    }
    private boolean printCredientials()
    {
            List<String[]> listOfCreds = new ArrayList<>();
            listOfCreds = readCredentialsFromDisk();

            int logOnAmounts = listOfCreds.size();
            if(logOnAmounts > 0) {
                System.out.println("Select the log in you would like to use:");
                for (int i = 0; i < logOnAmounts; i = i+3) {
                    String[] creds = listOfCreds.get(i);
                    System.out.println("[" + (i + 1) + "] " + creds[1] + " : " + creds[0]);
                }
                Scanner input = new Scanner(System.in);
                int intInput = -1;
                do {
                    try {
                        intInput = input.nextInt();
                        if (intInput < 0 || intInput > logOnAmounts) {
                            System.out.println("Enter a number between 0 and " + logOnAmounts);
                        }
                    } catch (Exception e) {
                        System.out.println("Enter a valid number please");
                    }
                } while (intInput < 0 || intInput > logOnAmounts);
                //   HOST = listOfCreds.get(intInput)[0]; Uncomment this when we're ready
                userName = listOfCreds.get(intInput - 1)[1];
                return true;
            }else{
                System.out.println("There are no saved log ons");
                return false;
            }
    }

    private void writeCredentialsToDisk(String HOST,String userName,String password)
    {
        try{
            LOGGER.log( Level.INFO, "Writing log in information to file");
            File secureFile = new File("Connections.txt");
            CommandLineInterface.EncryptDecryptFile(Cipher.DECRYPT_MODE,this.key,secureFile,secureFile);
            FileWriter fw = new FileWriter(secureFile,true);
            fw.write(HOST+" ");
            fw.write(userName+" ");
            fw.write(password+" ");
            fw.write("/\n");
            fw.close();
            CommandLineInterface.EncryptDecryptFile(Cipher.ENCRYPT_MODE,this.key,secureFile,secureFile);
        }catch (Exception e){
            System.out.println("Something went wrong writing to a file");
            LOGGER.log( Level.SEVERE, "Something went wrong while writing to file: "+e.getMessage());

        }
    }
    /**
     * <code>getUsername</code> provides an access method for the user name variable as necessary.
     * @return      a string containing the contents of the userName variable
     */
    public String getUsername(){
        return userName;
    }

    /**
     * The <code>getPassword</code> mathod provides an access method for the password
     * variable as necessary.
     * @return      a string containing the contents of the password variable
     */
    public String getPassword(){
        return password;
    }

}