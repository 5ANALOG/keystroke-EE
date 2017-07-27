package utility;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class importUser {
    private String csvFile = "./data/";
    private ArrayList<User> userList = new ArrayList<User>();

    public importUser() {
        this.csvFile = csvFile + "all_data.csv";
        readCSV();
    }
    public ArrayList<User> getuserList(){
        return this.userList;
    }

    public void readCSV() {
        final String COMMA = ",";
        BufferedReader fileReader = null;
        String line = null;

        try {
            fileReader = new BufferedReader(new FileReader(this.csvFile));
            fileReader.readLine();
            line = fileReader.readLine();
            while (line!= null) {
                //Set User object
                User user = new User();
                String[] part = line.split(COMMA);

                user.setuserID(part[0]); //Import username
                System.out.println("------"+part[0]+"------");
                for (int i = 0; i < 15; i++) { // for 10 trials
                    System.out.println("Dwell Time");
                    for (int j = 0; j < 14; j++) { //Import dwell time data
                        System.out.print(part[j + 2]+ " ");
                        user.setdwell(i, j, Integer.parseInt(part[j + 2]));
                    }
                    System.out.println("\nFlight TIme");
                    for (int j = 0; j < 13; j++) { //Import flight time data
                        System.out.print(part[16+j]+" ");
                        user.setflight(i, j, Integer.parseInt(part[16 + j]));
                    }
                    line = fileReader.readLine();
                    if(line!= null) part = line.split(COMMA);
                    System.out.println();
                }
                userList.add(user);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
