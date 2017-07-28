package utility;


public class User {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM-1;
    private String userID;
    public int[][] dwell = new int[TRIAL_NUM][DWELL_NUM]; // 15 trials, 14 dwell time each
    public int[][] flight = new int[TRIAL_NUM][FLIGHT_NUM]; //15 trials, 13 flight time each

    public void setuserID(String ID){
        this.userID = ID;
    }
    public String getuserID(){
        return this.userID;
    }
    public void setdwell(int i, int j, int time){
        dwell[i][j] = time;
    }
    public void setflight(int i, int j , int time){
        flight[i][j] = time;
    }
}



