package SFTPClient;

import com.jcraft.jsch.*;

import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 *  The commands class contains all the methods for functionality within the SFTP
 *  server: commands for changing directories, listing files stored remotely, etc.
 */
public class Commands {

    File currentLocalPath;
    private static final java.util.logging.Logger LOGGER = Logger.getLogger( "Commands" );

    Commands(){this.currentLocalPath = new File("").getAbsoluteFile();}

    /**
     *  The <code>changelocalDirectory</code> method takes a directory name from the command line
     *  after being invoked, and attempts to change the current directory on the
     *  client side to the provided directory.
     * @throws IOException
     */
    public void changeLocalDirectory() throws IOException {

        LOGGER.log(Level.INFO, "Entering change local directory");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Current local path: "+currentLocalPath);
        System.out.println("Enter directory name relative to above: ");
        File temp = null;
        String directoryPath = scanner.nextLine().trim();
        temp = new File(currentLocalPath +File.separator+directoryPath);
        if(!temp.isDirectory()) {
            LOGGER.log(Level.INFO, "User tried to change into non-existance directory");
            System.out.println("The directory you tried to change to does not exist.");
            return;
        } else {
            LOGGER.log(Level.INFO, "Changed directory");
            currentLocalPath = new File(temp.getCanonicalPath());
            System.out.println("Local directory: "+ currentLocalPath);
        }
    }

    /**
     * The <code>changeRemoteDirectory</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), then requests a directory name from the command line.
     * It attempts to change the directory on the remote server to the one provided
     * at the command line.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     * @throws SftpException
     */
    public static void changeRemoteDirectory(ChannelSftp sftpChannel) throws SftpException {

        LOGGER.log(Level.INFO, "Changing remote directory");
        System.out.println("Current remote path: "+sftpChannel.pwd());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name relative to above: ");
        String directoryPath = scanner.nextLine().trim();
        sftpChannel.cd(directoryPath);
        System.out.println("Remote directory: "+directoryPath);
        LOGGER.log(Level.INFO, "Successfully changed remote directory");
    }

    /**
     * The <code>printRemoteFile</code> method takes an sftpChannel object (which consists of an
     * open SFTP session), requests a file name at the command line, loads the contents
     * of that file into a BufferedReader object, and then outputs the contents of that
     * object to System.out.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     * @throws SftpException
     */
    public static void printRemoteFile(ChannelSftp sftpChannel) throws SftpException {

        LOGGER.log(Level.INFO, "Printing remote files");
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
            LOGGER.log(Level.INFO, "Successfully printed remote files");
        }catch(IOException e){
            LOGGER.log(Level.SEVERE, "Failed printing remote files");
            System.out.println(e);
            return;
        }
    }

    /**
     * The <code>printLocalFile</code> method requests a file name at the command line when invoked,
     * loads the contents of that file into a BufferedReader object, and then outputs the
     * contents of tht object to System.out.
     */
    public void printLocalFile(){

        LOGGER.log(Level.INFO, "Entering print local files");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to see:");
        String localf = scanner.nextLine().trim();
        File localFile = new File(currentLocalPath +File.separator+localf);
        FileReader fr = null;
        BufferedReader br = null;
        String line = null;

            try {
                LOGGER.log(Level.INFO, "Attempting to print local files");
            fr = new FileReader(localFile);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
                LOGGER.log(Level.INFO, "Local files printed successfully");
        }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Error while printing local files");
            System.out.println(e);
            return;
        }

    }

    /**
     * The <code>listRemoteFiles</code> method takes an sftpChannel object (which consists
     * of an open SFTP session) and returns a list of all the filenames.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     * @param flag              the only functional flag that can be passed is -al, which changes the output to long list format.
     */
    public static void listRemoteFiles(ChannelSftp sftpChannel, String flag) {

        try {
            LOGGER.log(Level.INFO, "Attempting to list remote file");
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
            LOGGER.log(Level.INFO, "Remote files listed successfully");
        } catch (SftpException e) {
            LOGGER.log(Level.SEVERE, "Error while listeing remote files");
            System.out.println(e);
        }
    }

    /**
     * The <code>listLocalFiles</code> method creates an array of files in the currentLocalPath,
     * then iterates through them, ignoring hidden files.
     */
    public void listLocalFiles() {
        LOGGER.log(Level.INFO, "Listing local files");
        File[] files = currentLocalPath.listFiles();
        for (File f : files) {
            if ((f.isFile() || f.isDirectory()) && !(f.getName().startsWith("."))){
                System.out.println(f.getName());
            }
        }
    }

    /**
     * The <code>uploadFiles</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), then requests a file name at the command
     * line, and attempts to upload that file to the remote server.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     * @throws SftpException
     */
    public void uploadFiles(ChannelSftp sftpChannel) throws SftpException {
        LOGGER.log(Level.INFO, "Entering upload files");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to upload: ");
        String path = scanner.nextLine().trim();
        try {
            LOGGER.log(Level.INFO, "Attempting to upload files");
            FileInputStream source = new FileInputStream(currentLocalPath + File.separator + path);
            sftpChannel.put(source, sftpChannel.pwd() + File.separator + path);
            LOGGER.log(Level.INFO, "Successfully uploaded files");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failure to upload files");
            System.err.println("Unable to find input file");
            return;
        }
        System.out.println("File "+path+" created!");
    }

    /**
     * The <code>makeRemoteDirectory</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), and requests a directory name at the command line.
     * If the directory does not already exist in the remote path, this method
     * creates it.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     */
    public void makeRemoteDirectory(ChannelSftp sftpChannel) {
        LOGGER.log(Level.INFO, "Entering make remote directory");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the directory you want to create: ");
        String newDir = scanner.nextLine().trim();
        try {
            LOGGER.log(Level.INFO, "Attempting to make remote directory");
            sftpChannel.mkdir(newDir);
            LOGGER.log(Level.INFO, "Successfully made remote directory");
        } catch (SftpException e) {
            LOGGER.log(Level.SEVERE, "Error while creating remote directory");
            e.printStackTrace();
            System.out.println("There was an error creating the directory on the remote server. See the message above.");
            return;
        }
        System.out.println("New directory "+newDir+" created!");
    }

    /**
     * The <code>makeLocalDirectory</code> method reates a new local directory within the client's
     * currentLocalPath.
     */
    public void makeLocalDirectory() {

        LOGGER.log(Level.INFO, "Entering make local directory");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the directory you want to create: ");
        String newLocalDir = scanner.nextLine().trim();
        boolean alreadyExists = (new File(currentLocalPath+File.separator+newLocalDir).isDirectory());
        if(alreadyExists){
            LOGGER.log(Level.INFO, "Local directory already exists, aborting");
            System.out.println("Error. Existing directory: the directory you are trying to create already exists.");
            return;
        }
        boolean directoryCreated = (new File(currentLocalPath+File.separator+newLocalDir)).mkdir();
        if (directoryCreated) {
            LOGGER.log(Level.INFO, "Directory created successfully");
            System.out.println("New local directory "+newLocalDir+" created!");
        } else {
            LOGGER.log(Level.SEVERE, "Error while creating local directory");
            System.out.println("There was a problem creating a new directory");
        }
    }

    /**
     * The <code>removeRemoteFile</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), and requests a file name at the command line.
     * The method then attempts to delete the specified remote directory.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     */
    public void removeRemoteFile(ChannelSftp sftpChannel) {
        LOGGER.log(Level.INFO, "Entering remove remote file");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the file you want to delete: ");
        String removeRemoteFile = scanner.nextLine().trim();
        try {
            LOGGER.log(Level.INFO, "Attempting to remove remote file");
            sftpChannel.rm(removeRemoteFile);
            LOGGER.log(Level.INFO, "Successfully removed remote file");
        } catch (SftpException e) {
            LOGGER.log(Level.SEVERE, "Error while removing remote file");
            e.printStackTrace();
            System.out.println("There was an error deleting the file on the remote server. See the message above.");
            return;
        }
        System.out.println("File "+removeRemoteFile+" removed.");
    }

    /**
     * The <code>removeRemoteDirectory</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), and requests a directory name at the command line.
     * It then attempts to delete the specified remote directory.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     */
    public void removeRemoteDirectory(ChannelSftp sftpChannel) {
        LOGGER.log(Level.INFO, "Entering remove remote directory");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the directory you want to delete: ");
        String removeRemoteDirectory = scanner.nextLine().trim();
        try {
            LOGGER.log(Level.INFO, "Attempting to remove remote directory");
            sftpChannel.rmdir(removeRemoteDirectory);
            LOGGER.log(Level.INFO, "Successfully removed remote directory");
        } catch (SftpException e) {
            LOGGER.log(Level.SEVERE, "Error while removing remote directory");
            e.printStackTrace();
            System.out.println("There was an error deleting the directory on the remote server. See the message above.");
            return;
        }
        System.out.println("Directory "+removeRemoteDirectory+" removed.");
    }

    /**
     * The <code>changeRemotePermissions</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), requests a file name from the command line, then
     * requests a chmod code from the command line.  It then attempts to apply the
     * specified chmod code to the specified file.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     */
    public void changeRemotePermissions(ChannelSftp sftpChannel) {


        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to chmod: ");
        String chmodFile = scanner.nextLine().trim();
        System.out.println("Enter the permissions command: ");
        String chmodCodeStr = scanner.nextLine();
        try {
            LOGGER.log(Level.INFO, "Attempting to change remote permissions");
            sftpChannel.chmod(Integer.parseInt(chmodCodeStr, 8),chmodFile);
            LOGGER.log(Level.INFO, "Successfully changed remote permissions");
        } catch (SftpException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Failed to change remote permissions");
            System.out.println(e.getMessage());
            System.out.println("Error. Could not change permissions or invalid chmod code. See the message above.");
            return;
        }
        System.out.println("Permissions changed!");
    }

    /**
     * The <code>renameRemoteFile</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), requests a file name at the command line, then
     * requests a new name for that file.  It then attempts to rename the specified
     * file to the new name provided on the command line.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     * @throws SftpException
     */
    public void renameRemoteFile(ChannelSftp sftpChannel) throws SftpException {
        LOGGER.log(Level.INFO, "Attempting to rename remote file");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to rename: ");
        String beforeFile = scanner.nextLine().trim();
        System.out.println("Enter the new name: ");
        String afterFile = scanner.nextLine();
        String workingDir = sftpChannel.pwd();
        Vector fileList = sftpChannel.ls(workingDir);
        if (fileList.contains(afterFile)){
            LOGGER.log(Level.INFO, "Error while renaming file: File name already exists");
            System.out.println("Error. There is already a file with that name!");
            return;
        }else{sftpChannel.rename(beforeFile, afterFile);
            LOGGER.log(Level.INFO, "File rename successful");
            System.out.println("Rename was successful!");
            return;
        }
    }

    /**
     * The <code>renameLocalFile</code> method requests a file name at the command line.
     * If the specified file name is a valid file in the currentLocalPath,
     * it requests a new file name at the command line, and attempts to rename
     * the specified file.
     */
    public void renameLocalFile() {

        LOGGER.log(Level.INFO, "Entering rename local file");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file/path for the file/directory you want to rename: ");
        String pathOld = scanner.nextLine().trim();
        File oldLocalFile = new File(currentLocalPath+File.separator+pathOld);
        if (!oldLocalFile.exists()){
            LOGGER.log(Level.WARNING, "File to rename does not exist");
            System.out.println("Error. The file you want to rename doesn't exist! Check your local directory using `dirs` or `lsl`");
            return;
        }

        System.out.println("Enter the new name to rename the file/directory as: ");
        String pathNew = scanner.nextLine().trim();
        File newLocalFileRename = new File(currentLocalPath+File.separator+pathNew);

        if (newLocalFileRename.exists()) {
            LOGGER.log(Level.WARNING, "Error while renaming local file: Name already exists");
            System.out.println("Error. Existing file: there is already a file or directory with the new name you're trying use.");
            return;
        }

        oldLocalFile.renameTo(newLocalFileRename);

        if (!oldLocalFile.exists() && newLocalFileRename.exists()) {
            LOGGER.log(Level.INFO, "File rename successful");
            System.out.println("File successfully renamed!");
        } else {
            LOGGER.log(Level.SEVERE, "Error while renaming file");
            System.out.println("There was a problem renaming the file.");
        }
    }

    /**
     * The <code>buildSuccessMessage</code> method takes a localFilePath object and a
     * remoteFilepath object, and uses them to create a success message
     * by casting these variables to a string and concatenating them with a
     * pre-created success message.
     * @param localFilePath     a String representing the target local file path
     * @param remoteFilePath    a String representing the current remote file path
     * @return
     */
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

    /**
     * The <code>downloadFileGivenNameAndPath</code> method takes an sftpChannel object (which
     * consists of an open SFTP session), a remoteFilePath string, and a localFilePath
     * string.  This method allows you to transfer a file by specifying its current
     * remoteFilePath and the localFilePath to attempt to write it to.
     * @param sftpChannel       an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     * @param remoteFilePath
     * @param localFilePath
     */
    private void downloadFileGivenNameAndPath(ChannelSftp sftpChannel, String remoteFilePath, String localFilePath) {
        LOGGER.log(Level.INFO, "Entering download file given name and path");
        InputStream remoteFile = null;
        try {
            LOGGER.log(Level.INFO, "Attempting to download file");
            remoteFile = sftpChannel.get(remoteFilePath);
        } catch (SftpException ex) {
            LOGGER.log(Level.INFO, "Error occured while trying to get remote file");
            System.err.println(ex.getMessage());
            System.out.println("An error occurred while trying to get the remote file: " + remoteFilePath);
            return;
        }
        OutputStream fileOut = null;
        File writeFile = new File(this.currentLocalPath + File.separator + localFilePath);
        try {
            LOGGER.log(Level.INFO, "Attempting to get file");
            fileOut = new FileOutputStream(writeFile);
            IOUtils.copy(remoteFile, fileOut);
            fileOut.close();
            LOGGER.log(Level.INFO, "Successfully got file");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting file");
            System.err.println(e.getMessage());
            System.out.println("error getting file: " + localFilePath);
            return;
        }
        System.out.println(buildSuccessMessage(localFilePath, remoteFilePath));
    }

    /**
     * The <code>downloadFile</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), then requests the path of the file you want
     * to download, relative to the current remote directory.  If that path
     * exists, it attempts to download the file.
     * @param sftpChannel        an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     */
    public void downloadFile(ChannelSftp sftpChannel) {
        LOGGER.log(Level.INFO, "Attempting to download file");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path to the file you want to download (relative to current remote directory): ");
        String readPath = scanner.nextLine().trim();
        if (readPath.equals("")) {
            LOGGER.log(Level.INFO, "Error: Cant get empty filename");
            System.out.println("Can't get an empty filename!");
            return;
        }
        System.out.println("Enter the path to save the file as (if not specified, will be the same as remote filepath): ");
        String writePath = scanner.nextLine().trim();
        if (writePath.equals("")) writePath = getFilenameFromPath(readPath);
        downloadFileGivenNameAndPath(sftpChannel, readPath, writePath);
    }

    /**
     * The <code>getFileNameFromPath</code>method takes a fileNameWithPath variable and
     * parses it for use in other methods (such as <code>buildSuccessMessage</code>
     * and <code>downloadFile</code>.
     * @param fileNameWithPath
     * @return
     */
    private String getFilenameFromPath(String fileNameWithPath) {
        String[] filenameWithPathArr = fileNameWithPath.split(File.separator);
        return filenameWithPathArr[filenameWithPathArr.length - 1];
    }

    /**
     * The <code>getWritePathFromGivenParams</code> takes two string arrays, representing local
     * and remote file paths, and an integer.
     * @param localF    The specified local file path as a string array
     * @param remoteF   The specified remote file path as a string array
     * @param i         The max length of a file path
     * @return
     */
    private String getWritePathFromGivenParams(String[] localF, String[] remoteF, int i) {
        if ((localF.length > i) && !(localF[i].equals(""))) {
            return localF[i];
        }
        return getFilenameFromPath(remoteF[i]);
    }

    /**
     * The <code>downloadMultipleFiles</code> method takes an sftpChannel object (which consists
     * of an open SFTP session), then requests a list of file paths, separated by
     * spaces, and relative to the current remote directory, for download.  It then
     * attempts to download all of the files at the specified file paths.  If more
     * files are specified than exist in the remote directory, the method returns
     * without transferring any files.
     * @param sftpChannel        an open ftp session as created in {@link SFTPConnection} by the <code>connect</code> method.
     */
    public void downloadMultipleFiles(ChannelSftp sftpChannel) {
        LOGGER.log(Level.INFO, "Attempting to download multiple files");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a list of space-separated paths to the file(s) you want to download (relative to current remote directory): ");
        String userInputListOfFilesRemote = scanner.nextLine().trim();
        String[] listOfFilesRemote = userInputListOfFilesRemote.split(" ");
        System.out.println("Enter the path(s) to save the file(s) as (if not specified, will be the same as remote filepath): ");
        String userInputListOfFilesLocal = scanner.nextLine().trim();
        String[] listOfFilesLocal = userInputListOfFilesLocal.split(" ");
        if (listOfFilesLocal.length > listOfFilesRemote.length) {
            System.out.println("Too many local filenames specified!");
            LOGGER.log(Level.WARNING, "Too many local filenames given");
            return;
        }
        for (int i = 0; i < listOfFilesRemote.length; i++) {
            String readPath = listOfFilesRemote[i];
            String writePath = getWritePathFromGivenParams(listOfFilesLocal, listOfFilesRemote, i);
            downloadFileGivenNameAndPath(sftpChannel, readPath, writePath);
        }
        LOGGER.log(Level.INFO, "Successfully downloaded multiple files");
    }

}


