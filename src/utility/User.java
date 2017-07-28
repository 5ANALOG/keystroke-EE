package utility;


public class User {
    private String userID;
    public int[][] dwell = new int[15][14]; // 15 trials, 14 dwell time each
    public int[][] flight = new int[15][13]; //15 trials, 13 flight time each

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



