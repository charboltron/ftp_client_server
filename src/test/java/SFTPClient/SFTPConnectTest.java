package SFTPClient;

import com.jcraft.jsch.JSch;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class SFTPConnectTest {

    JSch jsch = new JSch();

    @Test
    public void testConnection(){

        SFTPConnection s = new SFTPConnection("agilesftp", "104.248.67.51", "SimpleAndSecureFileTransferProtocol");
        s.connect(jsch);
        s.idleWake();
        assertThat(s.session.isConnected(), equalTo(true));
        s.disconnect();
        assertThat(s.session.isConnected(), equalTo(false));
    }

    //this doesn't work now that the mockito stuff has been written but it doesn't matter because there's a mockito equivalent.
    public void testQuitWithoutDisconnect(){ // I tested this on my FTP server to see whether quitting without disconnecting generated an error - it did.  I changed it to the Agile server information so the test can be run.
        SFTPConnection s = new SFTPConnection("agilesftp", "104.248.67.51", "SimpleAndSecureFileTransferProtocol");
        s.connect(jsch);
        assertThat(s.session.isConnected(), equalTo(true));
        System.exit(0);
    }
    /*  // one or the other of these tests needs to be commented out - testing will not System.exit twice.
    public void testQuitWithDisconnect(jsch){ // see above note - this one obviously should not produce any sort of error unless the method of disconnection is not working at all.
        SFTPConnection s = new SFTPConnection("agilesftp", "104.248.67.51", "SimpleAndSecureFileTransferProtocol");
        s.connect(jsch);
        assertThat(s.session.isConnected(), equalTo(true));
        s.disconnect(jsch);
        System.exit(0);
    }
    */
}
