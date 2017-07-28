package keyInterface;
import java.util.LinkedList;

public class keyList {

    //KeyList store key sequences
    private LinkedList<Node> completeKeys = new LinkedList<>();
    private LinkedList<Node> incompleteKeys = new LinkedList<>();

    public void add(int keyType, char keyChar, int keyCode, long keyTime){ //ADD key to list

        //If key is pressed
        if (keyType == 1){
            Node newNode = new Node(keyType, keyChar, keyCode, keyTime);
            incompleteKeys.add(newNode);//ADD key to incompleteKeys linkedlist

        }else if (keyType == 0){ //when key is released
            int count = 1;
            Node n = incompleteKeys.getFirst();
            while(n.getCode() != keyCode && count < incompleteKeys.size()){ //Find released key from incompleteKeys
                n = incompleteKeys.get(count);
                count++;
            }
            if (n.getCode() == keyCode){ //Push released key to completeKeys
                incompleteKeys.remove(count-1);
                n.setReleasedTime(keyTime);
                completeKeys.add(n);
            }
            else{
                System.err.println("What? couldn't enter the key for some reason");
            }
        }
    }
    public void add(char keyChar, int keyCode, long pressedTime, long releasedTime){
        Node node = new Node(keyChar,keyCode,pressedTime,releasedTime);
        completeKeys.add(node);
    }
    public void clear(){ //clear keylist
        while (!incompleteKeys.isEmpty()){
            incompleteKeys.removeFirst();
        }
        while(!completeKeys.isEmpty()){
            completeKeys.removeFirst();
        }
    }

    public Node getElement(int i){ //get specific key (node)
        return completeKeys.get(i);
    }

    public int getSize(){
        return completeKeys.size(); //get size of the key sequence
    }

    public long getFlight(int i){ //calculate flight time
        try {
            return completeKeys.get(i+1).getPressedTime() - completeKeys.get(i).getReleasedTime();
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
    }
    public long getDwell(int i){
        try {
            return completeKeys.get(i).getReleasedTime()-completeKeys.get(i).getPressedTime();
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
    }
}

