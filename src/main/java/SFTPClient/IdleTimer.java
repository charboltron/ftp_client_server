package SFTPClient;


import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link IdleTimer} class includes everything that the SFTP client requires to
 * instantiate an IdleTimer object which will automatically disconnect the client
 * if it hasn't received input in a preset amount of time.  The time is stored in
 * the ACCEPTABLE_IDLE_TIME static variable, and is set to 300000 milliseconds, or
 * five minutes.
 */
public class IdleTimer {

    public SFTPConnection sftpConnection;
    private long oldTime;
    private long newTime;
    private long idleTime;
    long delay  = 1000L;
    long period = 1000L;
    private static long ACCEPTABLE_IDLE_TIME = 300000; // 300 secs (five minutes)
    private Timer ducks = new Timer();
    private static final java.util.logging.Logger LOGGER = Logger.getLogger( "Commands" );

    IdleTimer(SFTPConnection sftpConnection){

        this.sftpConnection = sftpConnection;
        oldTime = System.currentTimeMillis();
    }

    /**
     * The runIdleTimer method instantiates a new TimerTask object from the
     * java.util.TimerTask library This new object uses the run method from that
     * same library to populate itself with the appropriate variables to turn it
     * into a persistent timer.
     */
    public void runIdleTimer(){
        LOGGER.log(Level.INFO, "Starting idle time");
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                newTime = System.currentTimeMillis();
                idleTime = newTime - oldTime;
                if (idleTime > ACCEPTABLE_IDLE_TIME){
                    sftpConnection.disconnect();
                    System.out.println("IDLE TIMEOUT: enter '-c' to reconnect or '-help' to see options.");
                    System.out.printf("> ");
                    ducks.cancel();
                }

            }
        };

        ducks.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    /**
     * The <code>idleWake</code> method uses the <code>setOldTime</code> method to reset the timer when
     * any sort of input is received.
     */
    public void idleWake(){
        this.setOldTime();
    }

    /**
     * The <code>setOldTime</code> method is used in conjunction with the <code>idleWake</code> method to
     * reset the timer, by setting the oldTime variable to the current system time in Milliseconds.
     */
    private void setOldTime(){
        oldTime = System.currentTimeMillis();
    }

    /**
     * The <code>cancel</code> method is invoked automatically when the idle time exceeds the
     * maximum allowable idle time, in order to stop the timer from running in the
     * client, after the client has been disconnected from the server.
     */
    public void cancel() {
        ducks.cancel();
    }
}
