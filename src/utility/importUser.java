package utility;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class importUser {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM-1;

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
                for (int i = 0; i < TRIAL_NUM; i++) { // for 15 trials
                    for (int j = 0; j < DWELL_NUM; j++) { //Import dwell time data
                        user.setdwell(i, j, Integer.parseInt(part[j + 2]));
                    }
                    for (int j = 0; j < FLIGHT_NUM; j++) { //Import flight time data
                        user.setflight(i, j, Integer.parseInt(part[2+DWELL_NUM+j]));
                    }
                    line = fileReader.readLine();
                    if(line!= null) part = line.split(COMMA);
                }
                user.normalizeDwell();
                user.normalizeFlight();
                userList.add(user);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
