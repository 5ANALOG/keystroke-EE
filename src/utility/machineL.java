package utility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class machineL extends JPanel implements ActionListener {
    private final int TRIAL_NUM = 15;
    private final int DWELL_NUM = 14;
    private final int FLIGHT_NUM = DWELL_NUM - 1;
    private importUser userList = new importUser();
    private ArrayList<User> user_list = userList.getuserList();
    private JTextArea textArea;
    private JTextField kValue, bValue;

    double[][] totalScore = new double[user_list.size()][TRIAL_NUM];

    int varK = 1;
    double varB = 1;

    double attempt = 0;
    double success = 0;

    public machineL() {
        //Create statistics Frame
        this.setSize(1000, 800);
        this.setLayout(new BorderLayout());

        //Create textArea
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel recordPane = new JPanel(new GridLayout(2, 1));
        recordPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel statPane = new JPanel(new GridLayout(2, 2));
        TitledBorder statTab = new TitledBorder(new LineBorder(Color.black), "Trial Status");
        statPane.setBorder(statTab);
        JLabel numUserLabel = new JLabel("Number of users:");
        JLabel numTrialLabel = new JLabel("Number of Trials:");
        JLabel numUserValue = new JLabel(String.valueOf(userList.getuserList().size()));
        JLabel numTrialValue = new JLabel("15");
        statPane.add(numUserLabel);
        statPane.add(numUserValue);
        statPane.add(numTrialLabel);
        statPane.add(numTrialValue);

        JPanel varPane = new JPanel(new GridLayout(2, 2));
        TitledBorder varTab = new TitledBorder(new LineBorder(Color.black), "Variable to test");
        varPane.setBorder(varTab);
        JLabel kValueLabel = new JLabel("K - Value: ");
        JLabel BValueLabel = new JLabel("B - Value: ");
        kValue = new JTextField();
        bValue = new JTextField();
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
        if (e.getActionCommand() == "start") {
            varK = Integer.parseInt(kValue.getText());
            varB = Double.parseDouble(bValue.getText());
            Runnable runnable = new utility.machineL.ValidateThread();
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
    public void initialize() {
        textArea.setText("");
        textArea.append("----Raw data status----\n");
        textArea.append("K - value: " +varK + "\n");
        textArea.append("P - vaulue " + varB + "%\n");
        textArea.append("-----------------------\n");
        attempt = 0;
        success = 0;
    }
    class ValidateThread implements Runnable {
        public void run() {
            initialize();
            KNN();
            printResult();
        }
    }
    public void KNN(){
        User headUser = new User();
        for (int i = 0; i <user_list.size(); i++){
            headUser = user_list.get(i);
            compare(headUser,i);
        }
    }
    public void compare(User head, int userIndex){
        for (int i = 0; i < TRIAL_NUM; i++){
            textArea.append("-------------------\n");
            for (int u = 0; u<user_list.size(); u++){
                User tail = user_list.get(u);
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
                    if (head.equals(tail) && i == j) totalScore[u][j] = 99999;
                    textArea.append(head.getuserID() + " " + tail.getuserID() +" Trial: "+i+ " "+j+" Score: " + totalScore[u][j]+ "\n");
                }
            }
            kNearest(head,varK);
        }
    }
    public void kNearest(User head,int k){
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
            textArea.append("Actual user: "+head.getuserID()+" | Machine predict: "+user_list.get(biggestPos).getuserID()+" with K value of "+ k+"\n");
        }
    }
    public void printResult(){
        textArea.append("\n----RESULT----\n");
        textArea.append("Total No. Attempt: "+ attempt+ "\n");
        textArea.append("Total No. Success: "+ success+ "\n");
        textArea.append("Total No. Failure: "+ (attempt-success)+ "\n");
        textArea.append("Accuracy rate: "+ (success/attempt)*100+ "%\n");

        textArea.append("----------------\n");
        textArea.setCaretPosition(textArea.getText().length()-1);
    }
}
