package SFTPClient;

import com.jcraft.jsch.*;

import java.io.IOException;
//import java.lang.invoke.DirectMethodHandle$Holder;


public class SFTPConnection {

    String username, host, pwd;
    static final int PORT = 22; //leaving static until we have a way of changing port
    Session session = null;
    ChannelSftp sftpChannel = null;
    Commands cmd;

    SFTPConnection(String username, String host, String pwd){

        this.username = username;
        this.host = host;
        this.pwd = pwd;
        this.cmd = new Commands();
    }

    public void connect(){

        try {
            JSch jsch = new JSch(); //Creates a class object of JSch which allows us to access a server over sftp
            session = jsch.getSession(username, host, PORT); //returns a session object
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
            System.out.println(e.getMessage());
        }

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
            case("mkdirl"):
                cmd.makeLocalDirectory();
                break;
            case("rmdirr"):
                cmd.removeRemoteDirectory(sftpChannel);
                break;
            case("rmr"):
                cmd.removeRemoteFile(sftpChannel);
                break;
            case("chmodr"):
                cmd.changeRemotePermissions(sftpChannel);
                break;
            case("mvr"):
                cmd.renameRemoteFile(sftpChannel);
                break;
            case("mvl"):
                cmd.renameLocalFile();
                break;
            case("ul"):
                cmd.uploadFiles(sftpChannel);
                break;
            case("dl"):
                cmd.downloadFile(sftpChannel);
                break;
            case("dlm"):
                cmd.downloadMultipleFiles(sftpChannel);
                break;
            default:
                System.out.println("Command not recognized, enter '-help' for a list of available options");
                break;

        }
    }
}
