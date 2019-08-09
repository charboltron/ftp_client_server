package SFTPClient;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.plaf.VFSFileChooserUIAccessorIF;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;
import org.apache.commons.vfs2.VFS;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.tools.JavaFileManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.channels.FileChannel;


class GUI extends Frame {
    JFrame frame = new JFrame("MyFirst GUI");
    JButton connectButton = new JButton("connect");

    JTextField nameField = new JTextField("agilesftp");
    JTextField passwordField = new JTextField("SimpleAndSecureFileTransferProtocol", 20);
    JTextField newDirectory = new JTextField("", 20);


    JPanel loginPanel = new JPanel();
    JPanel connectPanel = new JPanel();
    SFTPConnection ourConnection;
    String host = "104.248.67.51"; //Hard-coded for now
    ByteArrayOutputStream change;

    GUI(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 700);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.connect();
            }
        });
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login"),
                BorderFactory.createEmptyBorder(3,3,3,3)
        ));

        loginPanel.add(connectButton);
        loginPanel.add(nameField);
        loginPanel.add(passwordField);
        frame.getContentPane().add(BorderLayout.NORTH, loginPanel);
        frame.setVisible(true);
    }

    private void connect() {
        ourConnection = new SFTPConnection(nameField.getText(), host, passwordField.getText());
        JSch jsch = new JSch();
        ourConnection.connect(jsch);
        if(ourConnection.isConnected()){
            change = getRemoteFiles();

            connectPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Server"),
                    BorderFactory.createEmptyBorder(3,3,3,3)
            ));

            JTextArea remoteFiles = new JTextArea(change.toString());
            JLabel remoteFileLable = new JLabel("Remote Files");
            remoteFileLable.setLabelFor(remoteFiles);
            connectPanel.add(remoteFileLable);
            connectPanel.add(remoteFiles);
            connectPanel.add(newDirectory);

            newDirectory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        ourConnection.sftpChannel.cd(newDirectory.getText());
                        change = getRemoteFiles();
                        remoteFiles.setText(change.toString());
                    }catch (SftpException exception){

                    }
                }
            });

            JTextArea remoteFilePrint = new JTextArea();
            JLabel remoteFilePrintLabel = new JLabel("Printed file");
            remoteFilePrintLabel.setLabelFor(remoteFilePrint);
            connectPanel.add(remoteFilePrint);
            frame.getContentPane().add(BorderLayout.WEST, connectPanel);


            JFileChooser fc = new JFileChooser();
            fc.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Local Directory"),
                    BorderFactory.createEmptyBorder(3,3,3,3)
            ));
            frame.getContentPane().add(BorderLayout.EAST, fc);
            frame.revalidate();
            final VFSJFileChooser remoteFileChooser = new VFSJFileChooser();
            File remoteDirectory = ourConnection.cmd.currentLocalPath;
            remoteFileChooser.setCurrentDirectory(remoteDirectory);
 //           connectPanel.add(remoteFileChooser);

        }
    }

    private ByteArrayOutputStream getRemoteFiles() {
        ByteArrayOutputStream change = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(change);
        PrintStream old = System.out;
        System.setOut(ps);
        Commands.listRemoteFiles(ourConnection.sftpChannel, "none");
        System.out.flush();
        System.setOut(old);
        return change;
    }

}

