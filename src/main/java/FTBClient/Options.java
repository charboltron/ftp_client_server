package FTBClient;

import com.jcraft.jsch.*;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * @param args
 */
public class Options {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger("Options");

    public static void run(ChannelSftp sftpChannel) throws SftpException{

        Scanner scanner = new Scanner(System.in);

        while (true) {
            //cd lets you enter a directory. ls lists the files in that directory, and open will open a file
            //in current working directory.
            LOGGER.log(Level.INFO,"User has entered Options menu");
            System.out.println("What do you want to do? (pwdr) (pwdl) (cd) (ls) (print) (q)");
            String choice = scanner.nextLine();
            LOGGER.log(Level.INFO,"User has chosen: "+choice+"as a option");
            if (choice.equals("cd")) {
                changeDirectory(sftpChannel, scanner);
            } else if (choice.equals("ls")) { //works
                listFiles(sftpChannel);
            } else if (choice.equals("print")){ //works
                printFile(sftpChannel, scanner);
            } else if (choice.equals("pwdr")) {
                System.out.println(sftpChannel.pwd());
            } else if (choice.equals("pwdl")) {
                System.out.println(sftpChannel.lpwd());
            } else if(choice.equals("q")){
                break;
            }
            else {
                continue;
            }
        }
    }

    private static void changeDirectory(ChannelSftp sftpChannel, Scanner scanner) throws SftpException {
        LOGGER.log(Level.INFO,"Changing Directory");
        System.out.println("Enter directory name: ");
        String directoryPath = scanner.nextLine();
        LOGGER.log(Level.INFO,"Changing Directory to "+directoryPath);
        sftpChannel.cd(directoryPath);
    }

    private static void printFile(ChannelSftp sftpChannel, Scanner scanner) throws SftpException {
        LOGGER.log(Level.INFO,"User wants to print file");
        System.out.println("Enter the file you want to see:");
        String remoteFile = scanner.nextLine();
        try {
            InputStream out = null;
            out = sftpChannel.get(remoteFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;
            LOGGER.log(Level.INFO,"Printing file "+remoteFile);
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        }catch(IOException e){
            LOGGER.log(Level.SEVERE,"Error in printFile "+e.getMessage());
            System.out.println(e);
        }
    }

    private static void listFiles(ChannelSftp sftpChannel) {
        LOGGER.log(Level.INFO,"Entering listFiles");
        try {
            String workingDir = sftpChannel.pwd();
            Vector fileList = sftpChannel.ls(workingDir);
            LOGGER.log(Level.INFO,"Listing filess from "+workingDir);
            for (int i = 0; i < fileList.size(); i++) {
                System.out.println(fileList.get(i).toString());
            }
        } catch (SftpException e) {
            LOGGER.log(Level.SEVERE,"Error in listing files "+e.getMessage());
            System.out.println(e);
        }
    }
}

