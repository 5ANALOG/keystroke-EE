package utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class model implements ActionListener {
    private JFrame frame = new JFrame("Model");
    private JPanel pane1, pane2, pane3, pane4;
    private JComboBox combo = new JComboBox();
    private JButton button1, button2;
    private importUser userList = new importUser();
    JTextArea textArea;
    double STDThreshold = 0;
    double acceptanceThreshold = 0;
    JTextField standardDevValue;
    JTextField acceptanceValue;
    ArrayList<Integer> pickedArray = new ArrayList<>();
    ArrayList<Integer> unpickedArray = new ArrayList<>();
    ArrayList<User> USERLIST = userList.getuserList();
    User USER;
    public double[] dwellMean = new double[14];
    public double[] dwellStd = new double[14];
    public double[] flightMean = new double[13];
    public double[] flightStd = new double[13];
    int success = 0;
    int attempt = 0;

    public model() {
        frame.setResizable(false);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        combo.addItem("Statistical analysis");
        combo.addItem("Machine learning analysis");
        button1 = new JButton("Go");

        pane1 = new JPanel(new GridLayout(2, 1));

        pane1.add(combo);
        pane1.add(button1);

        frame.add(pane1, BorderLayout.NORTH);
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        pane2 = new JPanel(new GridLayout(2, 1));
        pane3 = new JPanel(new GridLayout(2, 2));
        JLabel numUserLabel = new JLabel("Number of users:");
        JLabel numTrialLabel = new JLabel("Number of Trials:");
        JLabel numUserValue = new JLabel(String.valueOf(userList.getuserList().size()));
        JLabel numTrialValue = new JLabel("15");
        pane3.add(numUserLabel);
        pane3.add(numUserValue);
        pane3.add(numTrialLabel);
        pane3.add(numTrialValue);
        pane4 = new JPanel(new GridLayout(2, 2));
        JLabel standardDevLabel = new JLabel("STD threshold:");
        JLabel acceptanceLabel = new JLabel("Acceptance threshold(%):");
        standardDevValue = new JTextField();
        acceptanceValue = new JTextField();
        pane4.add(standardDevLabel);
        pane4.add(standardDevValue);
        pane4.add(acceptanceLabel);
        pane4.add(acceptanceValue);
        pane2.add(pane3);
        pane2.add(pane4);
        frame.add(pane2, BorderLayout.WEST);

        button2 = new JButton("Start analysis");
        button2.addActionListener(this);
        button2.setActionCommand("start");
        frame.add(button2, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "start") {
            STDThreshold = Double.parseDouble(standardDevValue.getText());
            acceptanceThreshold = Double.parseDouble(acceptanceValue.getText());
            printStatus();
        }
    }

    public void printStatus() {
        textArea.append("----Raw data status----\n");
        textArea.append("Number of STD threshold: " + STDThreshold + "\n");
        textArea.append("Number of accept threshold: " + acceptanceThreshold + "%\n");
        textArea.append("----Finding FRR----\n");
        findFRR(15,10, "shlee18");
    }

    public void findFRR(int N, int R, String UserID){
        int length = USERLIST.size();
        boolean flag = false;
        int userPos = 0;
        for (int i = 0; i< length; i++){
            if ((USERLIST.get(i).getuserID()).equals(UserID)){
                flag = true;
                userPos = i;
                break;
            }
        }
        if (!flag) {
            textArea.append("Couldn't find the user");
            return;
        }
        USER = USERLIST.get(userPos);
        textArea.append("Found user : "+UserID);
        int[] arr = new int[N];
        attempt = 0;
        success = 0;
        combination(arr,0,N,R,0);

    }
    public void combination(int[] arr, int index, int n, int r, int target) {
        if (r == 0) pick(arr, index);
        else if (target == n) return;
        else {
            arr[index] = target;
            combination(arr, index + 1, n, r - 1, target + 1);
            combination(arr, index, n, r, target + 1);
        }
    }
    public void pick(int[] arr, int length) {
        for (int i = 0; i < length; i++) {
            pickedArray.add(arr[i]);
        }
        for (int i = 0; i < 15; i++) {
            boolean flag = false;
            for (int j = 0; j < 10; j++) {
                if (pickedArray.get(j) == i) flag = true;
            }
            if (!flag) unpickedArray.add(i);
        }
        textArea.append("\n--Picked Array--\n");
        for (Integer aPickedArray : pickedArray) {
            textArea.append(aPickedArray + " ");
        }
        calculate();
        compare();
        textArea.append("\n");
        pickedArray.clear();
        unpickedArray.clear();
    }
    public void calculate(){
        //dwell
        double total = 0;
        for (int i = 0; i<14; i++){
            for (int j = 0; j<10; j++) {
                total += USER.dwell[pickedArray.get(j)][i];
            }
            dwellMean[i] = total/10;
            total=0;
        }
        double stdTotal = 0;
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 10; j++) {
                stdTotal += (USER.dwell[pickedArray.get(j)][i] - dwellMean[i]) * (USER.dwell[pickedArray.get(j)][i] - dwellMean[i]);
            }
            dwellStd[i] = Math.sqrt(stdTotal / 9);
            stdTotal = 0;
        }

        for (int i = 0; i< 13; i++){
            for (int j = 0; j<10; j++) {
                total += USER.flight[pickedArray.get(j)][i];
            }
            flightMean[i] = total/10;
            total=0;
        }
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 10; j++) {
                stdTotal += (USER.flight[pickedArray.get(j)][i] - flightMean[i]) * (USER.flight[pickedArray.get(j)][i] - flightMean[i]);
            }
            flightStd[i] = Math.sqrt(stdTotal / 9);
            stdTotal = 0;
        }
    }

    public void compare(){
        //Dwell FFR
        int pass = 0;
        for (int i = 0; i < unpickedArray.size(); i++){
            pass = 0;
            for (int j = 0; j < 14; j++){
                double highest = dwellMean[j]+(dwellStd[j]*STDThreshold);
                double lowest = dwellMean[j]-(dwellStd[j]*STDThreshold);
                if (USER.dwell[unpickedArray.get(i)][j] > lowest && USER.dwell[unpickedArray.get(i)][j] < highest){
                    pass++;
                }
            }
            attempt++;
            //System.out.println("PASS: " + pass);
            //System.out.println("ACCEPTANCE: "+ 15*(acceptanceThreshold/100));
            if (pass >= (15*(acceptanceThreshold/100))){
                //System.out.println("PASS");
            }else{
                success++;
            }
            //System.out.println("SUCCESS: "+ success+" ATTEMPT: "+attempt);
            textArea.append("\n"+unpickedArray.get(i)+ " FFR RATE : "+((double)success/(double)attempt)*100+"%");
        }
    }
}
