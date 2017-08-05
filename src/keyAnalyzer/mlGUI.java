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

public class mlGUI extends JPanel implements ActionListener {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM - 1;
    private importUser userList;
    private ArrayList<user> user_list;

    private JTextArea textArea;
    private JTextField kValue, bValue;
    private JCheckBox normalizeCheck;

    private double[][] totalScore;

    private int varK = 1;
    private double varB = 1;

    private double attempt = 0;
    private double success = 0;

    public mlGUI() {
        userList = new importUser(false);
        user_list = userList.getuserList();
        //Create statisticsGUI Frame
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
        TitledBorder statTab = new TitledBorder(new LineBorder(Color.black), "Simulation Status");
        statPane.setBorder(statTab);
        JLabel numUserLabel = new JLabel(" Number of users: "+ String.valueOf(userList.getuserList().size()));
        JLabel numTrialLabel = new JLabel(" Number of Trials: "+ TRIAL_NUM);
        statPane.add(numUserLabel);
        statPane.add(numTrialLabel);

        JPanel varPane = new JPanel(new GridLayout(3, 2));
        varPane.setPreferredSize(new Dimension(250, 200));
        TitledBorder varTab = new TitledBorder(new LineBorder(Color.black), "Variables Setting");
        varPane.setBorder(varTab);
        JLabel normalizeLabel = new JLabel(" RESCALE: ");
        JLabel kValueLabel = new JLabel(" K VALUE: ");
        JLabel BValueLabel = new JLabel(" B VALUE: ");
        normalizeCheck = new JCheckBox();
        kValue = new JTextField();
        bValue = new JTextField();
        varPane.add(normalizeLabel);
        varPane.add(normalizeCheck);
        varPane.add(kValueLabel);
        varPane.add(kValue);
        varPane.add(BValueLabel);
        varPane.add(bValue);

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
            try{
                varK = Integer.parseInt(kValue.getText());
                varB = Double.parseDouble(bValue.getText());
            }catch (NumberFormatException error){
                error.printStackTrace();
                textArea.append("\n ### Error Occurred. Please Enter appropriate values in the variable ###");
                return;
            }
            Runnable runnable = new mlGUI.ValidateThread();
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
    private void initialize() {
        textArea.setText("");
        textArea.append("----- VARIABLE SETTING STATUS -----\n");
        textArea.append("K - value: " +varK + "\n");
        textArea.append("P - value: " + varB + "%\n");
        attempt = 0;
        success = 0;
        totalScore = new double[user_list.size()][TRIAL_NUM];
    }
    class ValidateThread implements Runnable {
        public void run() {
            initialize();
            KNN();
            printResult();
        }
    }
    private void KNN(){
        new user();
        user headUser;
        for (user anUser_list : user_list) {
            headUser = anUser_list;
            compare(headUser);
        }
    }
    private void compare(user head){
        for (int i = 0; i < TRIAL_NUM; i++){
            //textArea.append("-------------------\n");
            for (int u = 0; u<user_list.size(); u++){
                user tail = user_list.get(u);
                for (int j = 0; j <TRIAL_NUM; j++) {
                    double dwellScore = 0;
                    double flightScore = 0;
                    for (int c = 0; c < DWELL_NUM; c++) {
                        dwellScore += Math.abs(head.dwell[i][c] - tail.dwell[j][c]);
                    }
                    for (int c = 0; c <FLIGHT_NUM; c++){
                        flightScore += Math.abs(head.flight[i][c]-tail.flight[j][c]);
                    }
                    totalScore[u][j] = varB*(dwellScore)+(1-varB)*flightScore;
                    if (head.equals(tail) && i == j) totalScore[u][j] = -2;
                    //textArea.append(head.getuserID() + " " + tail.getuserID() +" Trial: "+i+ " "+j+" Score: " + totalScore[u][j]+ "\n");
                }
            }
            kNearest(head,varK);
        }
    }
    private void kNearest(user head, int k){
        int[] nearest = new int[user_list.size()];
        double superSmallest = -1;
        for (int i = 0; i < k; i++){
            double nearestVal = 99999;
            int[][] nearestIndex = new int[1][2];
            for (int j = 0; j< user_list.size(); j++){
                for (int c = 0; c<TRIAL_NUM; c++){
                    if (totalScore[j][c] < nearestVal && totalScore[j][c] > superSmallest){
                        nearestVal = totalScore[j][c];
                        nearestIndex[0][0] = j;
                        nearestIndex[0][1] = c;
                    }
                }
            }
            superSmallest = totalScore[nearestIndex[0][0]][nearestIndex[0][1]];
            nearest[nearestIndex[0][0]]++;
        }
        int biggest = -1;
        int biggestPos = -1;
        for (int i = 0; i <user_list.size() ; i++){
            if (nearest[i] > biggest){
                biggest = nearest[i];
                biggestPos = i;
            }
        }
        int duplicate = -1;
        for (int i = 0; i<user_list.size(); i++){
            if (nearest[i] == biggest){
                duplicate++;
            }
        }
        if (duplicate>0){
            kNearest(head,k-1);
        }else{
            attempt++;
            if(head.getuserID().equals(user_list.get(biggestPos).getuserID())){
                success++;
            }
            textArea.append("ACTUAL USER: "+head.getuserID()+" | MACHINE PREDICT: "+user_list.get(biggestPos).getuserID()+" WITH K VALUE OF "+ k+"\n");
        }
    }
    private void printResult(){
        textArea.append("\n----- SIMULATION RESULT -----\n");
        textArea.append("Total No. Attempt: "+ attempt+ "\n");
        textArea.append("Total No. Success: "+ success+ "\n");
        textArea.append("Total No. Failure: "+ (attempt-success)+ "\n");
        textArea.append("Accuracy rate: "+ (success/attempt)*100+ "%\n");
        textArea.setCaretPosition(textArea.getText().length()-1);
    }
}
