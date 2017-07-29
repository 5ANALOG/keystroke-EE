package utility;


public class User {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM-1;
    private String userID;
    private Boolean normalized = false;
    public double[][] dwell = new double[TRIAL_NUM][DWELL_NUM]; // 15 trials, 14 dwell time each
    public double[][] flight = new double[TRIAL_NUM][FLIGHT_NUM]; //15 trials, 13 flight time each

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
    public void normalizeDwell(){
        for (int i = 0; i< TRIAL_NUM; i++){
            double maximum = -9999;
            double minimum = 9999;
            for (int r = 0; r< DWELL_NUM; r++) {
                if (dwell[i][r] > maximum) {
                    maximum = dwell[i][r];
                }
                if (dwell[i][r] < minimum) {
                    minimum = dwell[i][r];
                }
            }
            for (int r = 0; r < DWELL_NUM; r++){
                dwell[i][r] = (dwell[i][r]-minimum)/(maximum-minimum);
            }
        }
        normalized = true;
    }
    public void normalizeFlight(){
        for (int i = 0; i< TRIAL_NUM; i++){
            double maximum = -9999;
            double minimum = 9999;
            for (int r = 0; r< FLIGHT_NUM; r++) {
                if (flight[i][r] > maximum) {
                    maximum = flight[i][r];
                }
                if (flight[i][r] < minimum) {
                    minimum = flight[i][r];
                }
            }
            for (int r = 0; r < FLIGHT_NUM; r++){
                flight[i][r] = (flight[i][r]-minimum)/(maximum-minimum);
            }
        }
        normalized = true;
    }
}



