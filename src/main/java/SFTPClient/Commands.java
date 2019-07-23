package SFTPClient;

import com.jcraft.jsch.*;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * @param args
 */
public class Commands {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger( "Commands" );

    public static void changeDirectory(ChannelSftp sftpChannel) throws SftpException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name: ");
        String directoryPath = scanner.nextLine();
        LOGGER.log( Level.INFO, "Changing directory to "+directoryPath);
        sftpChannel.cd(directoryPath);
    }

    public static void printFile(ChannelSftp sftpChannel) throws SftpException {
        LOGGER.log( Level.INFO, "Entering print file");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to see:");
        String remoteFile = scanner.nextLine();
        LOGGER.log( Level.INFO, "Attempting to print "+remoteFile);
        try {
            InputStream out = null;
            out = sftpChannel.get(remoteFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            LOGGER.log( Level.INFO, "File printed successfully");
            br.close();
        }catch(IOException e){
            LOGGER.log( Level.SEVERE, "Error printing file: "+e.getMessage());
            System.out.println(e);
        }
    }

    public static void listFiles(ChannelSftp sftpChannel) {
        LOGGER.log( Level.INFO, "Entering list files");
        try {
            String workingDir = sftpChannel.pwd();
            Vector fileList = sftpChannel.ls(workingDir);
            for (int i = 0; i < fileList.size(); i++) {
                System.out.println(fileList.get(i).toString());
            }
            LOGGER.log( Level.INFO, "Files listed successfully");
        } catch (SftpException e) {
            LOGGER.log( Level.SEVERE, "Error listing files: "+e.getMessage());
            System.out.println(e);
        }
    }

    public static void uploadFiles(ChannelSftp sftpChannel) throws SftpException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to upload: ");
        String path = scanner.nextLine();
        LOGGER.log( Level.INFO, "Attempting to upload files");
        try {
            FileInputStream source = new FileInputStream(sftpChannel.lpwd() + "/" + path);
            sftpChannel.put(source, sftpChannel.pwd() + "/" + path);
            LOGGER.log( Level.INFO, "File uploaded successfully");
        } catch (IOException e) {
            LOGGER.log( Level.SEVERE,"Failed to upload file: "+e.getMessage());
            System.err.println("Unable to find input file");
        }
    }
}
