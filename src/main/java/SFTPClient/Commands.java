package SFTPClient;

import com.jcraft.jsch.*;

import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
import org.apache.commons.io.IOUtils;
/**
 * * @param args
 */
public class Commands {

    File currentLocalPath;

    Commands(){this.currentLocalPath = new File(File.separator);}

    public void changeLocalDirectory() throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name: ");
        File temp = null;
        String directoryPath = scanner.nextLine().trim();
        temp = new File(currentLocalPath +File.separator+directoryPath);
        if(!temp.isDirectory()) {
            System.out.println("The directory you tried to change to does not exist.");
            return;
        } else {
            currentLocalPath = new File(temp.getCanonicalPath());
            System.out.println("Local directory: "+ currentLocalPath);
        }
    }

    public static void changeRemoteDirectory(ChannelSftp sftpChannel) throws SftpException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name: ");
        String directoryPath = scanner.nextLine().trim();
        sftpChannel.cd(directoryPath);
        System.out.println("Remote directory: "+directoryPath);

    }

    public static void printRemoteFile(ChannelSftp sftpChannel) throws SftpException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to see:");
        String remoteFile = scanner.nextLine().trim();
        try {
            InputStream out = null;
            out = sftpChannel.get(remoteFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        }catch(IOException e){
            System.out.println(e);
            return;
        }
    }

    public void printLocalFile(){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to see:");
        String localf = scanner.nextLine().trim();
        File localFile = new File(currentLocalPath +File.separator+localf);
        FileReader fr = null;
        BufferedReader br = null;
        String line = null;

            try {
            fr = new FileReader(localFile);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        }catch(IOException e){
            System.out.println(e);
            return;
        }

    }

    public static void listRemoteFiles(ChannelSftp sftpChannel, String flag) {
        try {
            String workingDir = sftpChannel.pwd();
            Vector fileList = sftpChannel.ls(workingDir);
            for (int i = 0; i < fileList.size(); i++) {
                if(flag.equals("-al")) {
                    System.out.println(fileList.get(i).toString());
                } else {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) fileList.get(i);
                    if(!(entry.getFilename().equals(".") || (entry.getFilename().equals("..")))) {
                        System.out.println(entry.getFilename());
                    }
                }
            }
        } catch (SftpException e) {
            System.out.println(e);
        }
    }

    public void listLocalFiles(ChannelSftp sftpChannel) throws IOException {

        File[] files = currentLocalPath.listFiles();
        for (File f : files) {
            if ((f.isFile() || f.isDirectory()) && !(f.getName().startsWith("."))){
                System.out.println(f.getName());
            }
        }
    }
    //    Files.walk(Paths.get(f.getAbsolutePath())).filter(Files::isRegularFile).forEach(System.out::println);


    public void uploadFiles(ChannelSftp sftpChannel) throws SftpException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to upload: ");
        String path = scanner.nextLine().trim();
        try {
            FileInputStream source = new FileInputStream(currentLocalPath + File.separator + path);
            sftpChannel.put(source, sftpChannel.pwd() + File.separator + path);
        } catch (IOException e) {
            System.err.println("Unable to find input file");
            return;
        }
        System.out.println("File "+path+" created!");
    }

    public void makeRemoteDirectory(ChannelSftp sftpChannel) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the directory you want to create: ");
        String newDir = scanner.nextLine().trim();
        try {
            sftpChannel.mkdir(newDir);
        } catch (SftpException e) {
            e.printStackTrace();
            System.out.println("There was an error creating the directory on the remote server. See the message above.");
            return;
        }
        System.out.println("New directory "+newDir+" created!");
    }

    public void changeRemotePermissions(ChannelSftp sftpChannel) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to chmod: ");
        String chmodFile = scanner.nextLine().trim();
        System.out.println("Enter the permissions command: ");
        String chmodCodeStr = scanner.nextLine();
        try {
            sftpChannel.chmod(Integer.parseInt(chmodCodeStr, 8),chmodFile);
        } catch (SftpException | NumberFormatException e) {
            System.out.println(e.getMessage());
            System.out.println("Error. Could not change permissions or invalid chmod code. See the message above.");
            return;
        }
        System.out.println("Permissions changed!");
    }

    public void renameRemoteFile(ChannelSftp sftpChannel) throws SftpException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to rename: ");
        String beforeFile = scanner.nextLine().trim();
        System.out.println("Enter the new name: ");
        String afterFile = scanner.nextLine();
        String workingDir = sftpChannel.pwd();
        Vector fileList = sftpChannel.ls(workingDir);
        if (fileList.contains(afterFile)){
            System.out.println("Error. There is already a file with that name!");
            return;
        }else{sftpChannel.rename(beforeFile, afterFile);
            System.out.println("Rename was successful!");
            return;
        }
    }

    public static void downloadFile(ChannelSftp sftpChannel) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path to the file you want to download (relative to current remote directory): ");
        String readPath = scanner.nextLine().trim();
        InputStream remoteFile = null;
        try {
            remoteFile = sftpChannel.get(readPath);
        } catch (SftpException ex) {
            System.err.println(ex.getMessage());
            System.out.println("An error occurred while trying to get the remote file.");
            return;
        }
        System.out.println("Enter the path to save the file as (relative to current directory): ");
        String writePath = scanner.nextLine().trim();
        if (writePath.equals("")) {
            System.out.println("Can't have an empty filename!");
            return;
        }
        File writeFile = new File(writePath);
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(writeFile);
            IOUtils.copy(remoteFile, fileOut);
            fileOut.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("error getting file!");
        }
    }

}


