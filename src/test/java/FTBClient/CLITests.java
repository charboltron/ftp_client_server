//package FTBClient;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.PrintStream;
//
///**
// * Unit tests for command line interface
// */
//public class CLITests {
//
//    // https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
//    // from /u/dfa
//
//    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//    private ByteArrayInputStream inContent;
//    private final PrintStream originalOut = System.out;
//    private final PrintStream originalErr = System.err;
//    private final InputStream systemIn = System.in;
//
//
//    @Before
//    public void setUpStreams() {
//        System.setOut(new PrintStream(outContent));
//        System.setErr(new PrintStream(errContent));
//    }
//    private void provideInput(String data) {
//        inContent = new ByteArrayInputStream(data.getBytes());
//        System.setIn(inContent);
//    }
//
//    @After
//    public void restoreStreams() {
//        System.setOut(originalOut);
//        System.setErr(originalErr);
//        System.setIn(systemIn);
//    }
//
//
//    @Test
//    public void displayMenuPrintsMenu(){
//        String menu = "THIS IS THE MENU:" +
//                "\n\t-help\t\tprints help menu" +
//                "\n\t-c\t\tconnects SFTP server" +
//                "\n\t-q\t\tquit SFTP client interface" +
//                "\n\n\tmore menu options coming soon...";
//
//        CommandLineInterface test = new CommandLineInterface();
//
//        assertThat(test.getMenu(), equalTo(menu));
//    }
//
//    @Test
//    public void clientManagerDefaultsToDisplayMenu(){
//        final String command = "";
//
//        CommandLineInterface test = new CommandLineInterface();
//
//        test.ftpClientManager(command);
//
//        assertThat(outContent.toString(), equalTo(test.menu + "\n"));
//
//    }
//
//    @Test
//    public void clientManagerReturnsMenuWithMenuFlag(){
//        final String command = "-m";
//
//        CommandLineInterface test = new CommandLineInterface();
//
//        test.ftpClientManager(command);
//
//        assertThat(outContent.toString(), equalTo(test.menu + "\n"));
//    }
//
//    // Temp test to check -u flag without FTP method
//    @Test
//    public void clientManagerReturnsUploaderTempWithMenuFlag(){
//        final String command = "-u";
//        CommandLineInterface test = new CommandLineInterface();
//
//        test.ftpClientManager(command);
//
//        assertThat(outContent.toString(), equalTo("runs FTPUploader\n"));
//    }
//
//    @Test
//    public void setCommandSetsCommand(){
//        CommandLineInterface test = new CommandLineInterface();
//        provideInput("-m");
//        test.setCommand();
//
//        assertThat(test.getCommand(), equalTo("-m"));
//
//    }
//
//    @Test
//    public void getGreetingReturnsGreetingMessage(){
//        String greeting = "Welcome to the FTP Client interface." +
//                "\n\tEnter '-help to a list of available commands" +
//                "\n\tor enter '-c' to connect...";
//        CommandLineInterface test = new CommandLineInterface();
//        assertThat(test.getGreeting(), equalTo(greeting));
//    }
//
//    @Test
//    public void setUserNameAndPasswordSetsUsernameAndPassword(){
//        CommandLineInterface test = new CommandLineInterface();
//        provideInput("username\npassword");
//        test.setUserNameAndPassword();
//        assertThat(test.getUsername(), equalTo("username"));
//        assertThat(test.getPassword(), equalTo("password"));
//
//    }
//
//
//
//
//}
