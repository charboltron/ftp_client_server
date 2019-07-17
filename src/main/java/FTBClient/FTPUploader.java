//import java.io.PrintWriter;
//
//import org.apache.commons.net.PrintCommandListener;
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPReply;
//import org.apache.commons.net.ftp.FTPSClient;
//
//public class FTPUploader {
//
//    private FTPSClient ftp = null;
//
//    public FTPUploader(String host, String user, String pwd) throws Exception{
//        ftp = new FTPSClient(true);
//        ftp.setConnectTimeout(5000);
//        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));//Adds a new listener, if you wanna get rid of all the extra debugging lines comment this out
//        int reply;
//        ftp.connect(host);//Attempts a connection
//        System.out.println("here");
//        reply = ftp.getReplyCode();//Grabs the code(i.e 404,202,503, ect to determine if connection is successful
//        if (!FTPReply.isPositiveCompletion(reply)) {//If its not successful it throws a exeception
//            ftp.disconnect();
//            throw new Exception("Failed to connect to FTB server, check internet connection and host name");
//        }
//        System.out.println("The code is: "+reply);
//        boolean login = ftp.login(user, pwd);
//        if(login)
//        {
//            ftp.setFileType(FTP.BINARY_FILE_TYPE);
//            ftp.enterLocalPassiveMode();
//        }else{
//            throw new Exception("Failed logging into server, check username and password");
//        }
//
//    }
//
//    public static void main(String[] args) throws Exception {
//        String testHost = "linux.cecs.pdx.edu";
//        String testUser = "boltch";
//        String testPassword = "RpawkDZNT6ors2ZT";
//        System.out.println("Start");
//        try {
//            FTPUploader ftpUploader = new FTPUploader(testHost, testUser, testPassword);
//        }catch (Exception err) {
//            System.out.println("Error:" + err.getMessage());
//        }
//    }
//
//}