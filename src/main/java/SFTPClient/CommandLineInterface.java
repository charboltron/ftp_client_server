package SFTPClient;

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
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CommandLineInterface {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger( "Commands" );
    private String command;
    private String userName;
    private String password;
    private final String HOST = "104.248.67.51";
    private static boolean enableLogging = false;
    private String key = "This is a secret";
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
                setUserNameAndPasswordFromFile();
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
            System.out.println("Username: ");
            userName = input.nextLine();
            System.out.println("Password: ");
            password = input.nextLine();
            System.out.println("Would you like to save your log in information (y/n): ");
            answer = input.nextLine();
            if(answer.equals("y"))
            {
                writeCredentialsToDisk(HOST,userName,password);
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

    public String getUsername(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

}