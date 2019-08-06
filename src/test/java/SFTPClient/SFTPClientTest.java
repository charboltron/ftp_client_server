package SFTPClient;

import com.jcraft.jsch.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.containsString;
import edu.pdx.cs410J.InvokeMainTestCase;

public class SFTPClientTest extends InvokeMainTestCase  {

        /**
         * Invokes the create method of {@link CommandLineInterface} with the given arguments.
         */
        private MainMethodResult invokeMain(String... args) {
            return invokeMain(CommandLineInterface.class, args);
        }

        private static final String SFTP = "sftp";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String HOSTNAME = "host";
        public static final int TIMEOUT = 500;

        @InjectMocks
        private SFTPConnection mockConnection = new SFTPConnection(USERNAME, HOSTNAME, PASSWORD);
        private JSch mockJsch = mock(JSch.class);
        private Session mockSession = mock(Session.class);
        private ChannelSftp mockChannel = mock(ChannelSftp.class);
        public static final int PORT = 22;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();


        @Before
        public void setUp() throws JSchException {

            when(mockJsch.getSession(eq(USERNAME), eq(HOSTNAME), eq(PORT))).thenReturn(mockSession);
            when(mockSession.openChannel(SFTP)).thenReturn(mockChannel);
            when(mockSession.isConnected()).thenReturn(true);
            when(mockSession.openChannel(SFTP)).thenReturn(mockChannel);
            }

        @Test
        public void connectCallsTheCorrectMethodsWithTheExpectedConnectionTimeoutParameter() throws Exception {

            mockConnection.connect(mockJsch);
            verify(mockSession).connect(TIMEOUT);
            verify(mockChannel).connect(TIMEOUT);

        }

        @Test
        public void connectMethodUsesTheUserNameAndHostThatItWasGivenToCreateSession() throws JSchException {

            mockConnection.connect(mockJsch);
            verify(mockJsch).getSession(USERNAME, HOSTNAME, 22);
        }

        @Test
        public void checkingIsConnectedCallsTheIsConnectedMethod(){

            mockConnection.connect(mockJsch);
            mockConnection.isConnected();
            verify(mockSession).isConnected();
        }

        @Test
        public void disconnectShouldDisconnect() throws JSchException{

            mockConnection.connect(mockJsch);
            mockConnection.disconnect();
            verify(mockChannel).disconnect();
            verify(mockSession).disconnect();

        }

        @Test
        public void verifyThatWhenConnectionIsMadeThePasswordIsSet() throws JSchException {

            mockConnection.connect(mockJsch);
            verify(mockSession).setPassword(PASSWORD);
        }

        @Test
        public void verifyThatWhenConnectionIsMadeThatSessionObjectSetsConfigurationAsIntended() throws JSchException {

            mockConnection.connect(mockJsch);
            verify(mockSession).setConfig("StrictHostKeyChecking", "no");

        }

        @Test
        public void anotherTestToEnsureThatSessionOpensSFTPChannel() throws JSchException {

            mockConnection.connect(mockJsch);
            verify(mockSession).openChannel(SFTP);
        }

        @Test
        public void thatGreetingIsPrintedUponStart(){

           MainMethodResult result = invokeMain("@Test_greeting");
           assertThat(result.getExitCode(), equalTo(0));
           assertThat(result.getTextWrittenToStandardOut(), containsString("Welcome to the SFTP Client interface."));

        }

        @Test
        public void helpMenuGetsPrintedIfCommandGiven()  {

            MainMethodResult result = invokeMain("@Test_help", "-help");
            assertThat(result.getExitCode(), equalTo(0));
            assertThat(result.getTextWrittenToStandardOut(), containsString("THIS IS THE MENU:"));
        }

        @Test
        public void quitCommandExitsGracefully() {

            MainMethodResult result = invokeMain("@Test_quit", "-q");
            assertThat(result.getExitCode(), equalTo(0));
            assertThat(result.getTextWrittenToStandardOut(), containsString("Goodbye!"));

        }

        @Test
        public void disconnectWithoutConnectGivesCorrectMessage() {

            MainMethodResult result = invokeMain("@Test_disconnect_no_connect", "-d");
            assertThat(result.getExitCode(), equalTo(0));
            assertThat(result.getTextWrittenToStandardOut(), containsString("No connection to disconnect."));

        }
}

