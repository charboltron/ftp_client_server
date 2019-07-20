package SFTPClient;

import com.jcraft.jsch.*;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
/**
 * * @param args
 */
public class Commands {


    public static void changeDirectory(ChannelSftp sftpChannel) throws SftpException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter directory name: ");
        String directoryPath = scanner.nextLine();
        sftpChannel.cd(directoryPath);
    }

    public static void printFile(ChannelSftp sftpChannel) throws SftpException {

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
        }
    }

    public static void listFiles(ChannelSftp sftpChannel) {
        try {
            String workingDir = sftpChannel.pwd();
            Vector fileList = sftpChannel.ls(workingDir);
            for (int i = 0; i < fileList.size(); i++) {
                System.out.println(fileList.get(i).toString());
            }
        } catch (SftpException e) {
            System.out.println(e);
        }
    }

    public static void uploadFiles(ChannelSftp sftpChannel) throws SftpException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file you want to upload: ");
        String path = scanner.nextLine();
        try {
            FileInputStream source = new FileInputStream(sftpChannel.lpwd() + "/" + path);
            sftpChannel.put(source, sftpChannel.pwd() + "/" + path);
        } catch (IOException e) {
            System.err.println("Unable to find input file");
        }
    }
}

