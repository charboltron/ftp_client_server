package SFTPClient;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

import java.io.File;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;

public class SFTPCommandsTest  {

    @Test
    public void GetFileNameFromPathWorksWithExoticAndLongFileNames() {
        Commands cmds = new Commands();
        String singleLengthPath = "Path";
        String twoLengthPath = "Double" + File.separator + "Path";
        String threeLengthPath = "Triple" + File.separator + "Length" + File.separator + "Path";
        String doubleWithExtension = "Double" + File.separator + "Path.exe";
        String doubleWithDoubleExtension = "Double" + File.separator + "Path.tar.gz";
        assertThat(cmds.getFilenameFromPath(singleLengthPath), equalTo("Path"));
        assertThat(cmds.getFilenameFromPath(twoLengthPath), equalTo("Path"));
        assertThat(cmds.getFilenameFromPath(threeLengthPath), equalTo("Path"));
        assertThat(cmds.getFilenameFromPath(doubleWithExtension), equalTo("Path.exe"));
        assertThat(cmds.getFilenameFromPath(doubleWithDoubleExtension), equalTo("Path.tar.gz"));
    }

    @Test
    public void GetFileNameFromParamsReturnsLocalFileIfMoreLocalsThanRemotes() {
        Commands cmds = new Commands();
        String[] local = new String[] {"local1", "local2"};
        String[] remote = new String[] {"remote1"};
        assertThat(cmds.getWritePathFromGivenParams(local, remote, 1), equalTo("local2"));
        assertThat(cmds.getWritePathFromGivenParams(remote, local, 1), equalTo("local2"));
    }
}
