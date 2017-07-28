package keyInterface;
import java.util.LinkedList;

public class trialList {

    private LinkedList<keyList> trials = new LinkedList<keyList>();

    public void add(keyList key){
        trials.add(key);
    }
    public void clear(){
        while(!trials.isEmpty()){
            trials.removeFirst();
        }
    }
    public keyList getElement(int i){
        return trials.get(i);
    }
    public int getSize(){
        return trials.size();
    }
}


