package SFTPClient;

import com.jcraft.jsch.*;

import java.io.IOException;
//import java.lang.invoke.DirectMethodHandle$Holder;

/**
 * {@link SFTPConnection} contains the code to create a new SFTP session, connect and disconnect.  It also contains commandsManager method, which gives the client additional functionality.
 */
public class SFTPConnection {

    String username, host, pwd;
    static final int PORT = 22; //leaving static until we have a way of changing port
    Session session = null;
    ChannelSftp sftpChannel = null;
    Commands cmd;
    private final IdleTimer idleTimer = new IdleTimer(this);

    /**
     * The constructor for the {@link SFTPConnection} takes in the username, host, and password parameters in order to use them with the getSession method from the com.jcraft.jsch library.
     * @param username      the username for the account on the remote SFTP server
     * @param host          the host server's URL or IP address
     * @param pwd           the password for the account on the remote SFTP server
     */
    SFTPConnection(String username, String host, String pwd){

        this.username = username;
        this.host = host;
        this.pwd = pwd;
        this.cmd = new Commands();
    }

    /**
     *  The <code>connect</code> method creates a JSch object, then uses the
     *  .getSession() method to pass the username, host, and PORT (currently static)
     *  to the remote server. It then passes the password to the remote server,
     *  and finally makes the connection, unless an exception is received.
     */
    public void connect(JSch jsch){

        try {

            idleTimer.runIdleTimer();
            session = jsch.getSession(username, host, PORT); //returns a session object
            session.setPassword(pwd);
            session.setConfig("StrictHostKeyChecking", "no"); //may want to investigate this
            System.out.println("Establishing Connection with " + host + "...");
            session.connect(500);
            System.out.println("Connection established!");
            System.out.println("Creating SFTP Channel...");
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect(500);
            System.out.println("SFTP Channel created!");

        } catch (JSchException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * The <code>disconnect</code> method disconnects from the current session if
     * the disconnect command is given.
     */
    public void disconnect(){

        if (session.isConnected()) {
            sftpChannel.disconnect();
            session.disconnect();
        }

    }

    /**
     * The <code>isConnected</code> method provides a convenient check to see whether
     * a session is presently connected.
     * @return      isConnected returns a true/false boolean value based on the result it receives.
     */
    public boolean isConnected(){
        return session.isConnected();
    }

    /**
     * The <code>commandsManager</code> method is passed a string from the command
     * line, and parses it through a switch case to invoke the proper method being
     * called by that command.
     * @param command      any of the commands listed in the command line menu.
     * @throws SftpException    Some of the commands being invoked through commandsManager throw an SftpException from the jsch API on failure.
     * @throws IOException      Some of the commands being invoked through commandsManager throw an IOException from java.lang.Exception on failure.
     */
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
                cmd.listLocalFiles();
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

    public void idleWake(){
        idleTimer.idleWake();
    }

    public void timerCancel() {idleTimer.cancel();}
}
