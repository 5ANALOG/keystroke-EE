package utility;


public class User {
    private String userID;


    private int[][] dwell = new int[15][14];
    private int[][] flight = new int[15][13];

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



