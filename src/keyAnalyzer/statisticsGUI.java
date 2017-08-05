package keyAnalyzer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class statisticsGUI extends JPanel implements ActionListener {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM-1;
    private importUser userList;
    private ArrayList<keyAnalyzer.user> user_list;
    private keyAnalyzer.user user;
    private JTextArea textArea;

    private double STDThreshold = 0;
    private int acceptanceThreshold = 0;

    private JTextField standardDevValue;
    private JTextField acceptanceValue;
    private JTextField nameUserValue;
    private JCheckBox findMinimumCheck;
    private JCheckBox normalizeCheck;

    private ArrayList<Integer> pickedArray = new ArrayList<>();
    private ArrayList<Integer> unpickedArray = new ArrayList<>();

    private double[] dwellMean = new double[DWELL_NUM];
    private double[] dwellStd = new double[DWELL_NUM];
    private double[] flightMean = new double[FLIGHT_NUM];
    private double[] flightStd = new double[FLIGHT_NUM];

    private int COMBINATION_NUM = 10;

    private int FRRfail = 0;
    private int FRRattempt = 0;
    private int FARfail = 0;
    private int FARattempt = 0;

    private double minimumFAR = 0;
    private double minimumFRR = 0;
    private double minimum_STD = 0;
    private int minimum_acceptance = 0;

    private boolean findMinimum = false;
    public statisticsGUI() {
        //Create statisticsGUI Frame
        userList = new importUser(false);
        user_list = userList.getuserList();

        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        //Create textArea
        textArea = new JTextArea();
        textArea.setFont(new Font("Open Sans", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel recordPane = new JPanel(new GridLayout(2, 1));
        recordPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel statPane = new JPanel(new GridLayout(2, 1));
        TitledBorder statTab = new TitledBorder(new LineBorder(Color.black),"Simulation Status");
        statPane.setBorder(statTab);
        JLabel numUserLabel = new JLabel(" Number of users: "+String.valueOf(userList.getuserList().size()));
        JLabel numTrialLabel = new JLabel(" Number of Trials: "+ String.valueOf(TRIAL_NUM));
        statPane.add(numUserLabel);
        statPane.add(numTrialLabel);

        JPanel varPane = new JPanel(new GridLayout(5, 2));
        varPane.setPreferredSize(new Dimension(250, 200));
        TitledBorder varTab = new TitledBorder(new LineBorder(Color.black),"Variables Setting");
        varPane.setBorder(varTab);
        JLabel normalizeLabel = new JLabel(" RESCALE: ");
        JLabel findMinumumLabel = new JLabel(" FIND MINIMUM");
        JLabel standardDevLabel = new JLabel(" STD TH:");
        JLabel acceptanceLabel = new JLabel(" ACCEPTOR TH(%):");
        JLabel nameUserLabel = new JLabel(" NAME OF USER: ");
        normalizeCheck = new JCheckBox();
        findMinimumCheck = new JCheckBox();
        standardDevValue = new JTextField();
        acceptanceValue = new JTextField();
        nameUserValue = new JTextField();
        varPane.add(normalizeLabel);
        varPane.add(normalizeCheck);
        varPane.add(findMinumumLabel);
        varPane.add(findMinimumCheck);
        varPane.add(standardDevLabel);
        varPane.add(standardDevValue);
        varPane.add(acceptanceLabel);
        varPane.add(acceptanceValue);
        varPane.add(nameUserLabel);
        varPane.add(nameUserValue);

        recordPane.add(statPane);
        recordPane.add(varPane);
        this.add(recordPane, BorderLayout.WEST);

        //Start button
        JButton button2 = new JButton("Start analysis");
        button2.addActionListener(this);
        button2.setActionCommand("start");
        this.add(button2, BorderLayout.SOUTH);
        this.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(e.getActionCommand(), "start")) {
            if (normalizeCheck.isSelected()){
                userList = new importUser(true);
            }else{
                userList = new importUser(false);
            }
            user_list = userList.getuserList();
            findMinimum = findMinimumCheck.isSelected();
            if(!findMinimum){
                try{
                    STDThreshold = Double.parseDouble(standardDevValue.getText());
                    acceptanceThreshold = Integer.parseInt(acceptanceValue.getText());
                }catch (NumberFormatException error){
                    error.printStackTrace();
                    textArea.append("\n ### Error Occurred. Please Enter appropriate values in the variable ###");
                    return;
                }
            }
            Runnable runnable = new ValidateThread();
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
    class ValidateThread implements Runnable {
        public void run() {
            initialize();
            textArea.append("\n##### SEARCHING IN PROGRESS... #####\n");
            if (findMinimum) {
                double minimum = 9999;
                for (int n = 1; n <= 100; n++) {
                    double i =  (double)n/10;
                    for (int r = 0; r <= 100; r+=5) {
                        STDThreshold = i;
                        acceptanceThreshold = r;
                        findFRR(TRIAL_NUM, 10, nameUserValue.getText());
                        findFAR(TRIAL_NUM, 10, nameUserValue.getText());
                        if (Math.abs(((double)FARfail/(double)FARattempt)+((double)FRRfail/(double)FRRattempt)) < minimum){
                            minimum_acceptance = acceptanceThreshold;
                            minimum_STD = STDThreshold;
                            minimumFAR = (double)FARfail/(double)FARattempt;
                            minimumFRR = (double)FRRfail/(double)FRRattempt;
                            minimum = Math.abs(((double)FARfail/(double)FARattempt)+((double)FRRfail/(double)FRRattempt));
                        }
                        FRRattempt = 0;
                        FRRfail = 0;
                        FARattempt = 0;
                        FARfail = 0;
                    }
                }
                textArea.append("\n----- MINIMUM ERROR SEARCH RESULT -----\n");
                textArea.append("Selected user: " + nameUserValue.getText() + "\n");
                textArea.append("----- MINIMUM FAR & FRR -----\n");
                textArea.append("STD threshold: " + minimum_STD + "\n");
                textArea.append("Acceptance threshold: " + minimum_acceptance + "\n");
                textArea.append("Minimum FRR: " + minimumFRR*100 + "%\n");
                textArea.append("Minimum FAR: " + minimumFAR*100 + "%\n");
                textArea.setCaretPosition(textArea.getText().length() - 1);
            } else {
                initialize();
                findFRR(TRIAL_NUM, COMBINATION_NUM, nameUserValue.getText());
                findFAR(TRIAL_NUM, COMBINATION_NUM, nameUserValue.getText());
                printResult(nameUserValue.getText());
                textArea.setCaretPosition(textArea.getText().length()-1);
            }
        }
    }
    private void initialize() {
        textArea.setText("");
        textArea.append("----- VARIABLE SETTING STATUS -----\n");
        textArea.append("STD threshold: " + STDThreshold + "\n");
        textArea.append("Acceptance threshold: " + acceptanceThreshold + "%\n");
        FRRattempt = 0;
        FRRfail = 0;
        FARattempt = 0;
        FARfail = 0;
    }
    private void printResult(String userId){
        textArea.append("\n----- SIMULATION RESULT -----\n");
        textArea.append("Selected user: "+userId+"\n");
        textArea.append("----- FALSE REJECTION TEST (FRR) -----\n");
        textArea.append("Total No. Comparison: "+ (FRRattempt)+ "\n");
        textArea.append("Total false rejected attempt: "+ FRRfail+ "\n");
        textArea.append("False rejection rate (FRR):  "+ Math.round((double)FRRfail/(double)FRRattempt*10000d)/100d+"%\n");
        textArea.append("\n---- FALSE ACCEPTATION TEST (FAR) ----\n");
        textArea.append("Total No. Comparison: "+ (FARattempt)+ "\n"); //Dwell comparision = Flight comparison
        textArea.append("Total false accepted attempt: "+ FARfail+ "\n");
        textArea.append("False acceptance rate (FAR): "+ Math.round((double)FARfail/(double)FARattempt*10000d)/100d+"%\n");
    }
    private void findFRR(int N, int R, String UserID){
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
            textArea.append("Couldn't find the user: "+ UserID+"\n");
            return;
        }
        if (!findMinimum)textArea.append("-----FALSE REJECTION SIMULATION STARTED-----\n");
        user = user_list.get(userPos);
        //textArea.append("Found user : "+UserID);
        int[] arr = new int[N];
        findCombination(arr,0,N,R,0, true);

    }
    private void findFAR(int N, int R, String UserID){
        //textArea.append("\n----Finding FAR----\n");
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
            textArea.append("Couldn't find the user: "+ UserID+"\n");
            return;
        }
        if (!findMinimum) textArea.append("-----FALSE ACCEPTANCE SIMULATION STARTED-----\n");
        user = user_list.get(userPos);
        //textArea.append("Found user : "+UserID);
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
        for (Integer anUnpickedArray : unpickedArray) {
            int dwell_pass = 0;
            for (int j = 0; j < DWELL_NUM; j++) {
                double highest = dwellMean[j] + (dwellStd[j] * STDThreshold);
                double lowest = dwellMean[j] - (dwellStd[j] * STDThreshold);
                if (user.dwell[anUnpickedArray][j] >= lowest && user.dwell[anUnpickedArray][j] <= highest) {
                    dwell_pass++;
                }
            }
            int flight_pass = 0;
            for (int j = 0; j < FLIGHT_NUM; j++) {
                double highest = flightMean[j] + (flightStd[j] * STDThreshold);
                double lowest = flightMean[j] - (flightStd[j] * STDThreshold);
                if (user.flight[anUnpickedArray][j] >= lowest && user.flight[anUnpickedArray][j] <= highest) {
                    flight_pass++;
                }
            }
            FRRattempt++;
            if (flight_pass + dwell_pass < ((DWELL_NUM + FLIGHT_NUM) * ((double) acceptanceThreshold / 100))) {
                FRRfail++;
            }
            /*if (!findMinimum)
                textArea.append(" | PASS RATE: " + Math.round((double) (flight_pass + dwell_pass) / (double) (DWELL_NUM + FLIGHT_NUM) * 1000d) / 10d + "" +
                        "%   FFR RATE : " + Math.round((double) FRRfail / (double) FRRattempt * 1000d) / 10d + "%\n");*/
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
        keyAnalyzer.user compareUser;
        new user();
        for (keyAnalyzer.user anUser_list : user_list) {
            if (!(anUser_list.getuserID()).equals(user.getuserID())) { //Except user itself,
                compareUser = anUser_list;
                //textArea.append("\nCompare with : "+ compareUser.getuserID()+"\n");
                for (int i = 0; i < TRIAL_NUM; i++) {
                    int dwell_pass = 0;
                    for (int j = 0; j < DWELL_NUM; j++) {
                        double highest = dwellMean[j] + (dwellStd[j] * STDThreshold);
                        double lowest = dwellMean[j] - (dwellStd[j] * STDThreshold);
                        if (compareUser.dwell[i][j] >= lowest && compareUser.dwell[i][j] <= highest) {
                            dwell_pass++;
                        }
                    }
                    int flight_pass = 0;
                    for (int j = 0; j < FLIGHT_NUM; j++) {
                        double highest = flightMean[j] + (flightStd[j] * STDThreshold);
                        double lowest = flightMean[j] - (flightStd[j] * STDThreshold);
                        if (compareUser.flight[i][j] >= lowest && compareUser.flight[i][j] <= highest) {
                            flight_pass++;
                        }
                    }
                    FARattempt++;
                    if (flight_pass + dwell_pass >= ((DWELL_NUM + FLIGHT_NUM) * ((double) acceptanceThreshold / 100))) {
                        FARfail++;
                    }
                    /*if (!findMinimum)
                        textArea.append(" | PASS RATE: " + Math.round((double) (flight_pass + dwell_pass) / (double) (DWELL_NUM + FLIGHT_NUM) * 1000d) / 10d + "" +
                                "%   FAR RATE : " + Math.round((double) FARfail / (double) FARattempt * 1000d) / 10d + "%\n");*/
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
            /*textArea.append("\n----PICKED TRIAL----\n");
            for (Integer aPickedArray : pickedArray) {
                //textArea.append(aPickedArray + " ");
            }*/
            calculateFRR(length);
        }else{ //FAR
            for (int i = 0; i < length; i++) {
                pickedArray.add(arr[i]);
            }
            /*textArea.append("\n----PICKED TRIAL----\n");
            for (Integer aPickedArray : pickedArray) {
                //textArea.append(aPickedArray + " ");
            }*/
            calculateFAR(length);
        }
        pickedArray.clear();
        unpickedArray.clear();
    }
}
