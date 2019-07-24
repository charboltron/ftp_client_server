package SFTPClient;


import java.util.Timer;
import java.util.TimerTask;


public class IdleTimer {

    public SFTPConnection sftpConnection;
    private long oldTime;
    private long newTime;
    private long idleTime;
    long delay  = 1000L;
    long period = 1000L;
    private static long ACCEPTABLE_IDLE_TIME = 5000;

    IdleTimer(SFTPConnection sftpConnection){
        this.sftpConnection = sftpConnection;
        oldTime = System.currentTimeMillis();
    }

    public void runIdleTimer(){
        Timer ducks = new Timer();
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                newTime = System.currentTimeMillis();
                idleTime = newTime - oldTime;
                if (idleTime > ACCEPTABLE_IDLE_TIME){
                    sftpConnection.disconnect();
                    System.out.println("IDLE TIMEOUT: enter '-c' to reconnect or '-help' to see options.");
                    System.out.println("> ");
                    ducks.cancel();
                }

            }
        };

        ducks.scheduleAtFixedRate(repeatedTask, delay, period);
    }


    public void idleWake(){
        this.setOldTime();
    }

    private void setOldTime(){
        oldTime = System.currentTimeMillis();
    }



}