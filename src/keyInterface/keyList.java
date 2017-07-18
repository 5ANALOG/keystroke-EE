package keyInterface;


import java.util.LinkedList;


public class keyList {

    LinkedList<Node> completekeys = new LinkedList<Node>();
    LinkedList<Node> incompletekeys = new LinkedList<Node>();

    public void add(int keyType, char keyChar, int keyCode, long keyTime){
        if (keyType == 1){
            Node newNode = new Node(keyType, keyChar, keyCode, keyTime);
            incompletekeys.add(newNode);

        }else if (keyType == 0){ //when released
            int count = 1;
            Node n = incompletekeys.getFirst();
            while(n.getCode() != keyCode && count < incompletekeys.size()){
                n = incompletekeys.get(count);
                count++;
            }
            if (n.getCode() == keyCode){
                incompletekeys.remove(count-1);
                n.setReleasedTime(keyTime);
                completekeys.add(n);
            }
            else{
                System.err.println("What? couldn't enter the key for some reason");
            }
        }
    }
    public void add(char keyChar, int keyCode, long pressedTime, long releasedTime){
        Node node = new Node(keyChar,keyCode,pressedTime,releasedTime);
        completekeys.add(node);
    }
    public void clear(){
        while (!incompletekeys.isEmpty()){
            incompletekeys.removeFirst();
        }
        while(!completekeys.isEmpty()){
            completekeys.removeFirst();
        }
    }

    public Node getElement(int i){
        return completekeys.get(i);
    }
    public int getSize(){
        return completekeys.size();
    }

    public long getFlight(int i){
        try {
            long flight = completekeys.get(i+1).getPressedTime() - completekeys.get(i).getReleasedTime();
            return flight;
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
    }
    public long getDwell(int i){
        try {
            long dwell = completekeys.get(i).getReleasedTime()-completekeys.get(i).getPressedTime();
            return dwell;
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
    }
}

