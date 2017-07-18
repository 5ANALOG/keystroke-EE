package utility;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class importUser {
    private String csvFile = "/Users/shawn/Documents/keystrokeEE/data/";
    private ArrayList<User> userList = new ArrayList<User>();


    public importUser() {
        this.csvFile = csvFile + "all_data.csv";
        readCSV();
    }

    public void readCSV() {
        final String COMMA = ",";
        BufferedReader fileReader = null;
        String line = null;

        try {
            fileReader = new BufferedReader(new FileReader(this.csvFile));
            fileReader.readLine();
            line = fileReader.readLine();
            while (line != null) {
                User user = new User();
                String[] part = line.split(COMMA);
                user.setuserID(part[0]);
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 14; j++) {
                        user.setdwell(i, j, Integer.parseInt(part[j + 2]));
                        System.out.println(part[j + 2]);
                    }
                    for (int j = 0; j < 13; j++) {
                        user.setflight(i, j, Integer.parseInt(part[2 + 13 + j]));
                    }
                    line = fileReader.readLine();
                }
                userList.add(user);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
