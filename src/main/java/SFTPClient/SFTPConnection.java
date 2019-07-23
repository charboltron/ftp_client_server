package SFTPClient;

import com.jcraft.jsch.*;

import java.util.logging.Level;
import java.util.logging.Logger;

//import java.lang.invoke.DirectMethodHandle$Holder;


public class SFTPConnection {

    String username, host, pwd;
    static final int PORT = 22;
    Session session = null;
    ChannelSftp sftpChannel = null;
    boolean connected = false;
    private static final Logger LOGGER = Logger.getLogger( "SFTPConnection" );

    SFTPConnection(String username, String host, String pwd){
        LOGGER.log( Level.INFO, "Creating SFTPConnection object");
        this.username = username;
        this.host = host;
        this.pwd = pwd;
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

    }

    public void disconnect(){

        sftpChannel.disconnect();
        session.disconnect();
        connected = false;
    }

    public boolean isConnected(){
        return connected;
    }

    public void optionsManager(String option) throws SftpException{
        LOGGER.log( Level.INFO, "Entering optionsManager");
        switch (option){
            case ("pwdr"):
                System.out.println(sftpChannel.pwd());
                break;
            case("pwdl"):
                System.out.println(sftpChannel.lpwd());
                break;
            case("print"):
                Commands.printFile(sftpChannel);
                break;
            case ("lsr"):
                Commands.listFiles(sftpChannel);
                break;
            case("cd"):
                Commands.changeDirectory(sftpChannel);
                break;
            case("lsl"):
                //Commands.listLocalFiles(sftpChannel);
                //System.out.println("Unimplemented method: List Files.");
                break;
            case("ul"):
                Commands.uploadFiles(sftpChannel);
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