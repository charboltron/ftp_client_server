package FTBClient;

import com.jcraft.jsch.*;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;
/**
 * * @param args
 */
public class ListFiles {

    public static void main(String[] args) throws SftpException {
        Scanner scanner = new Scanner(System.in);  // Reading from System.in
        Console console = null;

        System.out.println("Enter your username: ");
        String user = scanner.nextLine(); // Scans the next token of the input as an int once finished
        System.out.println(("Enter a hostname: "));
        String host = scanner.nextLine();
        System.out.println("Enter your password (It will not be masked!): ");
        String pwd = scanner.nextLine();
        int port = 22;
        //text file in my directory

        Session session = null;
        ChannelSftp sftpChannel = null;

        try {
            JSch jsch = new JSch(); //Creates a class object of JSch which allows us to access a server over sftp
            session = jsch.getSession(user, host, port); //returns a session object
            session.setPassword(pwd);
            session.setConfig("StrictHostKeyChecking", "no"); //may want to investigate this
            System.out.println("Establishing Connection with " + host + "...");
            session.connect();
            System.out.println("Connection established!");
            System.out.println("Creating SFTP Channel...");
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            System.out.println("SFTP Channel created!");

        } catch (JSchException e) {
            System.out.println(e);
        }

        while (true) {
            //cd lets you enter a directory. ls lists the files in that directory, and open will open a file
            //in current working directory.
            System.out.println("What do you want to do? (pwdr) (pwdl) (cd) (ls) (print) (q)");
            String choice = scanner.nextLine();
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
        scanner.close();
        sftpChannel.disconnect();
        session.disconnect();
    }

    private static void changeDirectory(ChannelSftp sftpChannel, Scanner scanner) throws SftpException {
        System.out.println("Enter directory name: ");
        String directoryPath = scanner.nextLine();
        sftpChannel.cd(directoryPath);
    }

    private static void printFile(ChannelSftp sftpChannel, Scanner scanner) throws SftpException {

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

    private static void listFiles(ChannelSftp sftpChannel) {
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
}

