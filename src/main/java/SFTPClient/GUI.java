package SFTPClient;

import com.jcraft.jsch.SftpException;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.tools.JavaFileManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;

class GUI extends Frame {
    JFrame frame = new JFrame("MyFirst GUI");
    JButton connectButton = new JButton("connect");

    JTextField nameField = new JTextField("agilesftp");
    JTextField passwordField = new JTextField("SimpleAndSecureFileTransferProtocol", 20);
    JTextField printFile = new JTextField("Path of File");

    JPanel loginPanel = new JPanel();
    JPanel connectPanel = new JPanel();
    SFTPConnection ourConnection;
    String host = "104.248.67.51"; //Hard-coded for now

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
        ourConnection.connect();
        if(ourConnection.isConnected()){
            ByteArrayOutputStream change = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(change);
            PrintStream old = System.out;
            System.setOut(ps);
            Commands.listRemoteFiles(ourConnection.sftpChannel, "none");
            System.out.flush();
            System.setOut(old);

            connectPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Server"),
                    BorderFactory.createEmptyBorder(3,3,3,3)
            ));

            JTextArea remoteFiles = new JTextArea(change.toString());
            JLabel remoteFileLable = new JLabel("Remote Files");
            remoteFileLable.setLabelFor(remoteFiles);
            connectPanel.add(remoteFileLable);
            connectPanel.add(remoteFiles);

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
        }
    }

}

