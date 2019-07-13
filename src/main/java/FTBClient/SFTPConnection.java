package FTBClient;

import com.jcraft.jsch.*;
import java.io.*;
//import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.*;

public class SFTPConnection {

    String username, host, pwd;
    static final int port = 22;
    Session session = null;
    ChannelSftp sftpChannel = null;
    boolean connected = false;

    SFTPConnection(String username, String host, String pwd){

        this.username = username;
        this.host = host;
        this.pwd = pwd;
    }

    public void Connect(){

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
            connected = true;

        } catch (JSchException e) {
            System.out.println(e);
        }

    }

    public void Disconnect(){

        sftpChannel.disconnect();
        session.disconnect();
        connected = false;
    }

    public boolean isConnected(){
        return connected;
    }
}