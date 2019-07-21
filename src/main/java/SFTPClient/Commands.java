package SFTPClient;

import com.jcraft.jsch.*;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
/**
 * * @param args
 */
public class Commands {

    File curDir;

    Commands(){this.curDir = new File("/");}

    public void changeLocalDirectory() throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name: ");
        File temp = null;
        String directoryPath = scanner.nextLine().trim();
        temp = new File(curDir+"/"+directoryPath);
        if(!temp.isDirectory()) {
            System.out.println("The directory you tried to change to does not exist.");
            return;
        } else {
            curDir = new File(curDir+"/"+directoryPath);
            curDir = new File(curDir.getCanonicalPath());
        }
    }

    public static void changeRemoteDirectory(ChannelSftp sftpChannel) throws SftpException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name: ");
        String directoryPath = scanner.nextLine();
        sftpChannel.cd(directoryPath);
    }

    public static void printRemoteFile(ChannelSftp sftpChannel) throws SftpException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to see:");
        String remoteFile = scanner.nextLine();
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
        String localf = scanner.nextLine();
        File localFile = new File(curDir+"/"+localf);
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

        File[] files = curDir.listFiles();
        for (File f : files) {
            if ((f.isFile() || f.isDirectory()) && !(f.equals(".") || f.equals(".."))){
                System.out.println(f.getName());
            }
        }
    }
    //    Files.walk(Paths.get(f.getAbsolutePath())).filter(Files::isRegularFile).forEach(System.out::println);


    public void uploadFiles(ChannelSftp sftpChannel) throws SftpException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to upload: ");
        String path = scanner.nextLine();
        try {
            FileInputStream source = new FileInputStream(curDir + "/" + path);
            sftpChannel.put(source, sftpChannel.pwd() + "/" + path);
        } catch (IOException e) {
            System.err.println("Unable to find input file");
        }
    }
}

