package SFTPClient;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class SFTPConnectTest {

    @Test
    public void testConnection(){

        SFTPConnection s = new SFTPConnection("agilesftp", "104.248.67.51", "SimpleAndSecureFileTransferProtocol");
        s.connect();
        assertThat(s.isConnected(), equalTo(true));
        s.disconnect();
        assertThat(s.isConnected(), equalTo(false));
    }
}