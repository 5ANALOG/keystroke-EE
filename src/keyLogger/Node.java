package keyLogger;


public class Node{
    private char keyChar;
    private int keyCode;
    private long timePressed;
    private long timeReleased;

    public Node(int keyType, char keyChar, int keyCode, long keyTime) {
        this.keyChar = keyChar;
        this.keyCode = keyCode;
        this.timePressed = keyTime;
    }
    public Node(char keyChar, int keyCode, long timePressed,long timeReleased){
        this.keyChar = keyChar;
        this.keyCode = keyCode;
        this.timePressed = timePressed;
        this.timeReleased = timeReleased;
    }
    public char getChar(){
        return keyChar;
    }
    public int getCode(){
        return keyCode;
    }
    public long getPressedTime(){
        return timePressed;
    }
    public long getReleasedTime(){
        return timeReleased;
    }
    public void setReleasedTime(long time){
        this.timeReleased = time;
    }
}
