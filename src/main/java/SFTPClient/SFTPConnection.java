package SFTPClient;

import com.jcraft.jsch.*;

import java.util.logging.Level;
import java.util.logging.Logger;

//import java.lang.invoke.DirectMethodHandle$Holder;


public class SFTPConnection {

    String username, host, pwd;
    static final int PORT = 22; //leaving static until we have a way of changing port
    Session session = null;
    ChannelSftp sftpChannel = null;
    boolean connected = false;
    private static final Logger LOGGER = Logger.getLogger( "SFTPConnection" );

    Commands cmd;

    SFTPConnection(String username, String host, String pwd){
        LOGGER.log( Level.INFO, "Creating SFTPConnection object");
        this.username = username;
        this.host = host;
        this.pwd = pwd;
        this.cmd = new Commands();
    }

    public void connect(){
        LOGGER.log( Level.INFO, "Attempting to connect to "+host);
        try {
            JSch jsch = new JSch(); //Creates a class object of JSch which allows us to access a server over sftp
            session = jsch.getSession(username, host, PORT); //returns a session object
            session.setPassword(pwd);
            session.setConfig("StrictHostKeyChecking", "no"); //may want to investigate this
            System.out.println("Establishing Connection with " + host + "...");
            session.connect();
            System.out.println("Connection established!");
            LOGGER.log( Level.INFO, "Successfully connected");
            System.out.println("Creating SFTP Channel...");
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            System.out.println("SFTP Channel created!");
            LOGGER.log( Level.INFO, "SFTP channel successfully connected");
            connected = true;

        } catch (JSchException e) {
            LOGGER.log( Level.SEVERE, "Error Connecting");
            LOGGER.log( Level.SEVERE, "Error"+e.getMessage());
            System.out.println(e);

    }

    public void disconnect(){

        if (session.isConnected()) {
            sftpChannel.disconnect();
            session.disconnect();
        }

    }

    public boolean isConnected(){
        return session.isConnected();
    }


    public void commandsManager(String command) throws SftpException, IOException {
   LOGGER.log( Level.INFO, "Entering optionsManager");
        switch (command){
            case ("dirs"):
                System.out.println("Remote directory: "+sftpChannel.pwd());
                System.out.println("Local  directory: "+cmd.currentLocalPath);
                break;
            case ("pwdr"):
                System.out.println(sftpChannel.pwd());
                break;
            case("pwdl"):
                System.out.println(cmd.currentLocalPath);
                break;
            case("printr"):
                cmd.printRemoteFile(sftpChannel);
                break;
            case("printl"):
                cmd.printLocalFile();
                break;
            case ("lsr"):
                cmd.listRemoteFiles(sftpChannel, "");
                break;
            case ("lsr -al"):
                cmd.listRemoteFiles(sftpChannel, "-al");
                break;
            case("cdr"):
                cmd.changeRemoteDirectory(sftpChannel);
                break;
            case("cdl"):
                cmd.changeLocalDirectory();
                break;
            case("lsl"):
                cmd.listLocalFiles(sftpChannel);
                break;
            case("mkdirr"):
                cmd.makeRemoteDirectory(sftpChannel);
                break;
            case("chmodr"):
                cmd.changeRemotePermissions(sftpChannel);
                break;
            case("mvr"):
                cmd.renameRemoteFile(sftpChannel);
                break;
            case("ul"):
                cmd.uploadFiles(sftpChannel);
                break;
            case("dl"):
                System.out.println("Unimplemented method: Download file from remote");
                break;
            default:
                System.out.println("Command not recognized, enter '-help' for a list of available options");
                break;

        }
    }
}