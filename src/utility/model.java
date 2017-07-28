package utility;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class model implements ActionListener {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM-1;
    private importUser userList = new importUser();
    private ArrayList<User> user_list = userList.getuserList();
    private User user;
    private JTextArea textArea;
    private double STDThreshold = 0;
    private double acceptanceThreshold = 0;
    private JTextField standardDevValue;
    private JTextField acceptanceValue;
    private JTextField nameUserValue;
    private ArrayList<Integer> pickedArray = new ArrayList<>();
    private ArrayList<Integer> unpickedArray = new ArrayList<>();
    private double[] dwellMean = new double[DWELL_NUM];
    private double[] dwellStd = new double[DWELL_NUM];
    private double[] flightMean = new double[FLIGHT_NUM];
    private double[] flightStd = new double[FLIGHT_NUM];

    private int FRRfail_dwell = 0;
    private int FRRattempt_dwell = 0;
    private int FRRfail_flight = 0;
    private int FRRattempt_flight = 0;
    private int FARfail_dwell = 0;
    private int FARattempt_dwell = 0;
    private int FARfail_flight = 0;
    private int FARattempt_flight = 0;

    public model() {
        //Create model Frame
        JFrame frame = new JFrame("Model");
        frame.setResizable(false);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //Create combobox
        JComboBox combo = new JComboBox();
        combo.addItem("Statistical analysis");
        combo.addItem("Machine learning analysis");
        JButton button1 = new JButton("Go");
        JPanel topPane = new JPanel(new GridLayout(2, 1));
        topPane.add(combo);
        topPane.add(button1);
        frame.add(topPane, BorderLayout.NORTH);

        //Create textArea
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel recordPane = new JPanel(new GridLayout(2, 1));
        recordPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel statPane = new JPanel(new GridLayout(2, 2));
        TitledBorder statTab = new TitledBorder(new LineBorder(Color.black),"Trial Status");
        statPane.setBorder(statTab);
        JLabel numUserLabel = new JLabel("Number of users:");
        JLabel numTrialLabel = new JLabel("Number of Trials:");
        JLabel numUserValue = new JLabel(String.valueOf(userList.getuserList().size()));
        JLabel numTrialValue = new JLabel("15");
        statPane.add(numUserLabel);
        statPane.add(numUserValue);
        statPane.add(numTrialLabel);
        statPane.add(numTrialValue);

        JPanel varPane = new JPanel(new GridLayout(3, 2));
        TitledBorder varTab = new TitledBorder(new LineBorder(Color.black),"Variable to test");
        varPane.setBorder(varTab);
        JLabel standardDevLabel = new JLabel("STD threshold:");
        JLabel acceptanceLabel = new JLabel("Acceptance threshold(%):");
        JLabel nameUserLabel = new JLabel("Name of user to find: ");
        standardDevValue = new JTextField();
        acceptanceValue = new JTextField();
        nameUserValue = new JTextField();
        varPane.add(standardDevLabel);
        varPane.add(standardDevValue);
        varPane.add(acceptanceLabel);
        varPane.add(acceptanceValue);
        varPane.add(nameUserLabel);
        varPane.add(nameUserValue);

        recordPane.add(statPane);
        recordPane.add(varPane);

        frame.add(recordPane, BorderLayout.WEST);

        //Start button
        JButton button2 = new JButton("Start analysis");
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
            initialize();
            Runnable runnable = new ValidateThread();
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
    class ValidateThread implements Runnable {
        public void run() {
            findFRR(TRIAL_NUM,10, nameUserValue.getText());
            findFAR(TRIAL_NUM,13, nameUserValue.getText());
            printResult(nameUserValue.getText());
        }
    }
    public void initialize() {
        textArea.setText("");
        textArea.append("----Raw data status----\n");
        textArea.append("Number of STD threshold: " + STDThreshold + "\n");
        textArea.append("Number of accept threshold: " + acceptanceThreshold + "%\n");
        FRRattempt_flight = 0;
        FRRattempt_dwell = 0;
        FRRfail_flight = 0;
        FRRfail_dwell = 0;
        FARattempt_dwell = 0;
        FARattempt_flight = 0;
        FARfail_dwell = 0;
        FARfail_flight = 0;
    }
    public void printResult(String userId){
        textArea.append("\n----RESULT----\n");
        textArea.append("User selectes: "+userId+"\n");
        textArea.append("\n----False Negative Test----\n");
        textArea.append("Total No. Comparison: "+ (FRRattempt_dwell+FRRattempt_flight)+ "\n");
        textArea.append("Dwell No. Fail Attempt: "+ FRRfail_dwell+ "\n");
        textArea.append("Flight No. Fail Attempt: "+ FRRfail_flight + "\n");
        textArea.append("Dwell False Negative Rate: "+ ((double)FRRfail_dwell/(double)FRRattempt_dwell)*100+"%\n");
        textArea.append("Flight False Negative Rate: "+ ((double)FRRfail_flight/(double)FRRattempt_flight)*100+"%\n");
        textArea.append("\n----False Acceptance Test----\n");
        textArea.append("Total No. Comparison: "+ (FARattempt_dwell+FARattempt_flight)+ "\n");
        textArea.append("Dwell No. Success Attempt: "+ FARfail_dwell+ "\n");
        textArea.append("Flight No. Suecces Attempt: "+ FARfail_flight + "\n");
        textArea.append("Dwell False Acceptance Rate: "+ ((double)FARfail_dwell/(double)FARattempt_dwell)*100+"%\n");
        textArea.append("Flight False Acceptance Rate: "+ ((double)FARfail_flight/(double)FARattempt_flight)*100+"%\n");
        textArea.append("----------------\n");
        textArea.setCaretPosition(textArea.getText().length()-1);
    }

    private void findFRR(int N, int R, String UserID){
        textArea.append("----Finding FRR----\n");
        if(user_list.isEmpty()){
            textArea.append("NO user trial available!");
            return;
        }
        int length = user_list.size();
        boolean flag = false;
        int userPos = 0;
        for (int i = 0; i< length; i++){
            if ((user_list.get(i).getuserID()).equals(UserID)){
                flag = true;
                userPos = i;
                break;
            }
        }
        if (!flag) {
            textArea.append("Couldn't find the user: "+ UserID);
            return;
        }
        user = user_list.get(userPos);
        textArea.append("Found user : "+UserID);
        int[] arr = new int[N];
        findCombination(arr,0,N,R,0, true);

    }
    private void findFAR(int N, int R, String UserID){
        textArea.append("\n----Finding FAR----\n");
        if(user_list.isEmpty()){
            textArea.append("NO user trial available!");
            return;
        }
        int length = user_list.size();
        boolean flag = false;
        int userPos = 0;
        for (int i = 0; i< length; i++){
            if ((user_list.get(i).getuserID()).equals(UserID)){
                flag = true;
                userPos = i;
                break;
            }
        }
        if (!flag) {
            textArea.append("Couldn't find the user: "+ UserID);
            return;
        }
        user = user_list.get(userPos);
        textArea.append("Found user : "+UserID);
        int[] arr = new int[N];
        findCombination(arr,0,N,R,0, false); //FAR
    }

    private void calculateFRR(int r){
        //DWELL TIME
        double total = 0;
        for (int i = 0; i<DWELL_NUM; i++){
            for (int j = 0; j<r; j++) {
                total += user.dwell[pickedArray.get(j)][i];
            }
            dwellMean[i] = total/r;
            total=0;
        }
        double stdTotal = 0;
        for (int i = 0; i < DWELL_NUM; i++) {
            for (int j = 0; j < r; j++) {
                stdTotal += (user.dwell[pickedArray.get(j)][i] - dwellMean[i]) * (user.dwell[pickedArray.get(j)][i] - dwellMean[i]);
            }
            dwellStd[i] = Math.sqrt(stdTotal / (r-1));
            stdTotal = 0;
        }
        //FLIGHT TIME
        for (int i = 0; i< FLIGHT_NUM; i++){
            for (int j = 0; j<r; j++) {
                total += user.flight[pickedArray.get(j)][i];
            }
            flightMean[i] = total/r;
            total=0;
        }
        for (int i = 0; i < FLIGHT_NUM; i++) {
            for (int j = 0; j < r; j++) {
                stdTotal += (user.flight[pickedArray.get(j)][i] - flightMean[i]) * (user.flight[pickedArray.get(j)][i] - flightMean[i]);
            }
            flightStd[i] = Math.sqrt(stdTotal / (r-1));
            stdTotal = 0;
        }
        for (int i = 0; i < unpickedArray.size(); i++) {
            int pass = 0;
            for (int j = 0; j < DWELL_NUM; j++) {
                double highest = dwellMean[j] + (dwellStd[j] * STDThreshold);
                double lowest = dwellMean[j] - (dwellStd[j] * STDThreshold);
                if (user.dwell[unpickedArray.get(i)][j] >= lowest && user.dwell[unpickedArray.get(i)][j] <= highest) {
                    pass++;
                }
            }
            FRRattempt_dwell++;
            if (pass < (DWELL_NUM * (acceptanceThreshold / 100))) {
                FRRfail_dwell++;
            }
            pass = 0;
            for (int j = 0; j < FLIGHT_NUM; j++) {
                double highest = flightMean[j] + (flightStd[j] * STDThreshold);
                double lowest = flightMean[j] - (flightStd[j] * STDThreshold);
                if (user.flight[unpickedArray.get(i)][j] >= lowest && user.flight[unpickedArray.get(i)][j] <= highest) {
                    pass++;
                }
            }
            FRRattempt_flight++;
            if (pass < (FLIGHT_NUM * (acceptanceThreshold / 100))) {
                FRRfail_flight++;
            }
            textArea.append("\n" + unpickedArray.get(i) + " DWELL - PASS RATE: "+ Math.round((double)pass/(double)DWELL_NUM*1000d)/10d + "" +
                    "%   FFR RATE : " + Math.round((double)FRRfail_dwell/(double) FRRattempt_dwell*1000d)/10d + "%");
            textArea.append(" | FLIGHT - PASS RATE: "+ Math.round((double)pass/(double)FLIGHT_NUM*1000d)/10d + "" +
                    "%   FFR RATE : " + Math.round((double)FRRfail_flight/(double) FRRattempt_flight*1000d)/10d + "%");
            textArea.setCaretPosition(textArea.getText().length()-1);
        }
    }
    private void calculateFAR(int r){
        double total = 0;
        for (int i = 0; i<DWELL_NUM; i++){
            for (int j = 0; j<r; j++) {
                total += user.dwell[pickedArray.get(j)][i];
            }
            dwellMean[i] = total/r;
            total=0;
        }
        double stdTotal = 0;
        for (int i = 0; i < DWELL_NUM; i++) {
            for (int j = 0; j < r; j++) {
                stdTotal += (user.dwell[pickedArray.get(j)][i] - dwellMean[i]) * (user.dwell[pickedArray.get(j)][i] - dwellMean[i]);
            }
            dwellStd[i] = Math.sqrt(stdTotal / (r-1));
            stdTotal = 0;
        }
        //FLIGHT TIME
        for (int i = 0; i< FLIGHT_NUM; i++){
            for (int j = 0; j<r; j++) {
                total += user.flight[pickedArray.get(j)][i];
            }
            flightMean[i] = total/r;
            total=0;
        }
        for (int i = 0; i < FLIGHT_NUM; i++) {
            for (int j = 0; j < r; j++) {
                stdTotal += (user.flight[pickedArray.get(j)][i] - flightMean[i]) * (user.flight[pickedArray.get(j)][i] - flightMean[i]);
            }
            flightStd[i] = Math.sqrt(stdTotal / (r-1));
            stdTotal = 0;
        }
        User compareUser = new User();
        for (int z = 0; z< user_list.size(); z++){
            if (!(user_list.get(z).getuserID()).equals(user.getuserID())) { //Except user itself,
                compareUser = user_list.get(z);
                textArea.append("\nCompare with : "+ compareUser.getuserID());
                for (int i = 0; i < TRIAL_NUM; i++) {
                    int pass = 0;
                    for (int j = 0; j < DWELL_NUM; j++) {
                        double highest = dwellMean[j] + (dwellStd[j] * STDThreshold);
                        double lowest = dwellMean[j] - (dwellStd[j] * STDThreshold);
                        if (compareUser.dwell[i][j] >= lowest && compareUser.dwell[i][j] <= highest) {
                            pass++;
                        }
                    }
                    FARattempt_dwell++;
                    if (pass >= (DWELL_NUM * (acceptanceThreshold / 100))) {
                        FARfail_dwell++;
                    }
                    pass = 0;
                    for (int j = 0; j < FLIGHT_NUM; j++) {
                        double highest = flightMean[j] + (flightStd[j] * STDThreshold);
                        double lowest = flightMean[j] - (flightStd[j] * STDThreshold);
                        if (compareUser.flight[i][j] >= lowest && compareUser.flight[i][j] <= highest) {
                            pass++;
                        }
                    }
                    FARattempt_flight++;
                    if (pass >= (FLIGHT_NUM * (acceptanceThreshold / 100))) {
                        FARfail_flight++;
                    }
                    textArea.append("\n" + i + " DWELL - PASS RATE: " + Math.round((double) pass / (double) DWELL_NUM * 1000d) / 10d + "" +
                            "%   FAR RATE : " + Math.round((double) FARfail_dwell / (double) FARattempt_dwell * 1000d) / 10d + "%");
                    textArea.append(" | FLIGHT - PASS RATE: " + Math.round((double) pass / (double) FLIGHT_NUM * 1000d) / 10d + "" +
                            "%   FAR RATE : " + Math.round((double) FARfail_flight / (double) FARattempt_flight * 1000d) / 10d + "%");
                }
            }
        }
    }

    private void findCombination(int[] arr, int index, int n, int r, int target, boolean type) { //Create combinations
        if (r == 0) pick(arr, index, type);
        else if (target == n) return;
        else {
            arr[index] = target;
            findCombination(arr, index + 1, n, r - 1, target + 1, type);
            findCombination(arr, index, n, r, target + 1, type);
        }
    }
    private void pick(int[] arr, int length, boolean type) {
        if (type) { //FRR
            for (int i = 0; i < length; i++) {
                pickedArray.add(arr[i]);
            }
            for (int i = 0; i < TRIAL_NUM; i++) {
                boolean flag = false;
                for (int j = 0; j < length; j++) {
                    if (pickedArray.get(j) == i) flag = true;
                }
                if (!flag) unpickedArray.add(i);
            }
            textArea.append("\n----PICKED TRIAL----\n");
            for (Integer aPickedArray : pickedArray) {
                textArea.append(aPickedArray + " ");
            }
            calculateFRR(length);
            textArea.append("\n");
        }else if (!type){ //FAR
            for (int i = 0; i < length; i++) {
                pickedArray.add(arr[i]);
            }
            textArea.append("\n----PICKED TRIAL----\n");
            for (Integer aPickedArray : pickedArray) {
                textArea.append(aPickedArray + " ");
            }
            calculateFAR(length);
            textArea.append("\n");
        }
        pickedArray.clear();
        unpickedArray.clear();
    }
}
