package SFTPClient;

import com.jcraft.jsch.JSch;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class SFTPConnectTest {

    @Test
    public void testConnection(){

        JSch jsch = new JSch();
        SFTPConnection s = new SFTPConnection("agilesftp", "104.248.67.51", "SimpleAndSecureFileTransferProtocol");
        s.connect(jsch);
        assertThat(s.session.isConnected(), equalTo(true));
        s.disconnect();
        assertThat(s.session.isConnected(), equalTo(false));
    }
}