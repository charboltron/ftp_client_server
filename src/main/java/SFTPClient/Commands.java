package SFTPClient;

import com.jcraft.jsch.*;

import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
import org.apache.commons.io.IOUtils;

import javax.sound.midi.Soundbank;

/**
 * * @param args
 */
public class Commands {

    File currentLocalPath;

    Commands(){this.currentLocalPath = new File("").getAbsoluteFile();}

    public void changeLocalDirectory() throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Current local path: "+currentLocalPath);
        System.out.println("Enter directory name relative to above: ");
        File temp = null;
        String directoryPath = scanner.nextLine().trim();
        temp = new File(currentLocalPath +File.separator+directoryPath);
        if(!temp.isDirectory()) {
            System.out.println("The directory you tried to change to does not exist or invalid relative path.");
            return;
        } else {
            currentLocalPath = new File(temp.getCanonicalPath());
            System.out.println("Local directory: "+ currentLocalPath);
        }
    }

    public static void changeRemoteDirectory(ChannelSftp sftpChannel) throws SftpException {

        System.out.println("Current remote path: "+sftpChannel.pwd());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name relative to above: ");
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

    public void makeLocalDirectory() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the directory you want to create: ");
        String newLocalDir = scanner.nextLine().trim();
        boolean alreadyExists = (new File(currentLocalPath+File.separator+newLocalDir).isDirectory());
        if(alreadyExists){
            System.out.println("Error. Existing directory: the directory you are trying to create already exists.");
            return;
        }
        boolean directoryCreated = (new File(currentLocalPath+File.separator+newLocalDir)).mkdir();
        if (directoryCreated) {
            System.out.println("New local directory "+newLocalDir+" created!");
        } else {
            System.out.println("There was a problem creating a new directory");
        }
    }

    public void removeRemoteFile(ChannelSftp sftpChannel) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the file you want to delete: ");
        String removeRemoteFile = scanner.nextLine().trim();
        try {
            sftpChannel.rm(removeRemoteFile);
        } catch (SftpException e) {
            e.printStackTrace();
            System.out.println("There was an error deleting the file on the remote server. See the message above.");
            return;
        }
        System.out.println("File "+removeRemoteFile+" removed.");
    }

    public void removeRemoteDirectory(ChannelSftp sftpChannel) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the directory you want to delete: ");
        String removeRemoteDirectory = scanner.nextLine().trim();
        try {
            sftpChannel.rmdir(removeRemoteDirectory);
        } catch (SftpException e) {
            e.printStackTrace();
            System.out.println("There was an error deleting the directory on the remote server. See the message above.");
            return;
        }
        System.out.println("Directory "+removeRemoteDirectory+" removed.");
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

    public void renameLocalFile() throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file/path for the file/directory you want to rename: ");
        String pathOld = scanner.nextLine().trim();
        File oldLocalFile = new File(currentLocalPath+File.separator+pathOld);
        if (!oldLocalFile.exists()){
            System.out.println("Error. The file you want to rename doesn't exist! Check your local directory using `dirs` or `lsl`");
            return;
        }

        System.out.println("Enter the new name to rename the file/directory as: ");
        String pathNew = scanner.nextLine().trim();
        File newLocalFileRename = new File(currentLocalPath+File.separator+pathNew);

        if (newLocalFileRename.exists()) {
            System.out.println("Error. Existing file: there is already a file or directory with the new name you're trying use.");
            return;
        }

        oldLocalFile.renameTo(newLocalFileRename);

        if (!oldLocalFile.exists() && newLocalFileRename.exists()) {
            System.out.println("File successfully renamed!");
        } else {
            System.out.println("There was a problem renaming the file.");
        }
    }

    private String buildSuccessMessage(String localFilePath, String remoteFilePath) {
        String successMessage = "Succesfully downloaded file: ";
        String localFileNameNoPath = getFilenameFromPath(localFilePath);
        String remoteFileNameNoPath = getFilenameFromPath(remoteFilePath);
        if (!localFileNameNoPath.equals(remoteFileNameNoPath)) {
            successMessage += remoteFileNameNoPath + " (remote) ---> " + localFileNameNoPath + " (local)";
        } else {
            successMessage += localFileNameNoPath;
        }
        return successMessage;
    }

    private void downloadFileGivenNameAndPath(ChannelSftp sftpChannel, String remoteFilePath, String localFilePath) {
        InputStream remoteFile = null;
        try {
            remoteFile = sftpChannel.get(remoteFilePath);
        } catch (SftpException ex) {
            System.err.println(ex.getMessage());
            System.out.println("An error occurred while trying to get the remote file: " + remoteFilePath);
            return;
        }
        OutputStream fileOut = null;
        File writeFile = new File(this.currentLocalPath + File.separator + localFilePath);
        try {
            fileOut = new FileOutputStream(writeFile);
            IOUtils.copy(remoteFile, fileOut);
            fileOut.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("error getting file: " + localFilePath);
            return;
        }
        System.out.println(buildSuccessMessage(localFilePath, remoteFilePath));
    }

    public void downloadFile(ChannelSftp sftpChannel) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path to the file you want to download (relative to current remote directory): ");
        String readPath = scanner.nextLine().trim();
        if (readPath.equals("")) {
            System.out.println("Can't get an empty filename!");
            return;
        }
        System.out.println("Enter the path to save the file as (if not specified, will be the same as remote filepath): ");
        String writePath = scanner.nextLine().trim();
        if (writePath.equals("")) writePath = getFilenameFromPath(readPath);
        downloadFileGivenNameAndPath(sftpChannel, readPath, writePath);
    }

    private String getFilenameFromPath(String fileNameWithPath) {
        String[] filenameWithPathArr = fileNameWithPath.split(File.separator);
        return filenameWithPathArr[filenameWithPathArr.length - 1];
    }

    private String getWritePathFromGivenParams(String[] localF, String[] remoteF, int i) {
        if ((localF.length > i) && !(localF[i].equals(""))) {
            return localF[i];
        }
        return getFilenameFromPath(remoteF[i]);
    }

    public void downloadMultipleFiles(ChannelSftp sftpChannel) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a list of space-separated paths to the file(s) you want to download (relative to current remote directory): ");
        String userInputListOfFilesRemote = scanner.nextLine().trim();
        String[] listOfFilesRemote = userInputListOfFilesRemote.split(" ");
        System.out.println("Enter the path(s) to save the file(s) as (if not specified, will be the same as remote filepath): ");
        String userInputListOfFilesLocal = scanner.nextLine().trim();
        String[] listOfFilesLocal = userInputListOfFilesLocal.split(" ");
        if (listOfFilesLocal.length > listOfFilesRemote.length) {
            System.out.println("Too many local filenames specified!");
            return;
        }
        for (int i = 0; i < listOfFilesRemote.length; i++) {
            String readPath = listOfFilesRemote[i];
            String writePath = getWritePathFromGivenParams(listOfFilesLocal, listOfFilesRemote, i);
            downloadFileGivenNameAndPath(sftpChannel, readPath, writePath);
        }
    }

}


