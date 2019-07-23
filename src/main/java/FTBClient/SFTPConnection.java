package FTBClient;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.logging.Level;
import java.util.logging.Logger;

//import java.lang.invoke.DirectMethodHandle$Holder;

public class SFTPConnection {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger("Options");
    String username, host, pwd;
    static final int port = 22;
    Session session = null;
    ChannelSftp sftpChannel = null;
    boolean connected = false;

    SFTPConnection(String username, String host, String pwd){
        LOGGER.log(Level.INFO, "Creating SFTP object");
        this.username = username;
        this.host = host;
        this.pwd = pwd;

    }

    public boolean Connect() {
        LOGGER.log(Level.INFO,"Attempting Connection to "+host+" : "+port);
        try {
            JSch jsch = new JSch(); //Creates a class object of JSch which allows us to access a server over sftp
            session = jsch.getSession(username, host, port); //returns a session object
            session.setPassword(pwd);
            session.setConfig("StrictHostKeyChecking", "no"); //may want to investigate this
            System.out.println("Establishing Connection with " + host + "...");
            session.connect();
            System.out.println("Connection established!");
            System.out.println("Creating SFTP Channel...");
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            System.out.println("SFTP Channel created!");
            LOGGER.log(Level.INFO,"Connected successfully");
            connected = true;


        } catch (JSchException e) {
            LOGGER.log(Level.SEVERE,"Failed to connect to server "+e.getMessage());
            System.out.println("Failure to connect: "+e.getMessage());
            connected = false;

        }
        return connected;


    }

    public void Disconnect(){
        LOGGER.log(Level.INFO,"Attempting to disconnect");
            sftpChannel.disconnect();
            session.disconnect();
        connected = false;
        LOGGER.log(Level.INFO,"Disconnected");
    }

    public boolean isConnected(){
        LOGGER.log(Level.INFO,"Checking connection status");
        if(session.isConnected() && sftpChannel.isConnected())
        {LOGGER.log(Level.INFO,"Still Connected");
            return true;

        }else{
            LOGGER.log(Level.WARNING,"No longer Connected");
            return false;
        }
    }
}
