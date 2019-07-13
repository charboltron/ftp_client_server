package FTBClient;

import java.util.Scanner;

public class CommandLineInterface {

    private String command;
    public String userName;
    public String password;
    public String menu = String.join("\n", "Welcome to the FTP Client Interface"
            , "\n\tThe following is a list of available commands:"
            , "\t-m :\tPrint menu options"
            , "\t-u :\tUpload a file to current directory"
            , "\tMORE MENU OPTIONS COMING");




    CommandLineInterface(boolean getUsernameAndPassword){
        if (getUsernameAndPassword){
            Scanner input = new Scanner(System.in);
            System.out.println("Username: ");
            userName = input.nextLine();
            System.out.println("Password: ");
            password = input.nextLine();

            input.close();
        }

    }



    public void ftpClientManager(String command){

        switch(command){
            case ("-m"):
                displayMenu();
                break;
            case ("-u"):
                System.out.println("runs FTPUploader");
                break;
            default:
                displayMenu();
                break;
        }
    }

    public void displayMenu(){
        System.out.println(this.menu);
    }

    public void setCommand(){
        Scanner getCommand = new Scanner(System.in);
        command = getCommand.nextLine();
        getCommand.close();
    }

    public String getCommand(){
        return this.command;
    }

}
