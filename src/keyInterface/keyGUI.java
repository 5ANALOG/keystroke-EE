package keyInterface;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import utility.*;

public class keyGUI extends JFrame implements ActionListener{

    private keyList KeyList;
    private trialList TrialList;

    private JPanel pane = new JPanel(new GridLayout(1,2));
    private JPanel pane2 = new JPanel(new GridLayout(10,1));
    private JPanel pane3 = new JPanel(new FlowLayout());
    private JPanel pane4 = new JPanel(new GridLayout(2,1));

    private JTextField userID;
    private JTextArea typingArea;
    private JLabel userID_Label, fixedPhrase_Label, trial_Label, displayArea;
    private JScrollPane scroll;

    private JButton submitButton, clearButton, exportButton, newTrialButton, modelButton;

    private ChartPanel chart1;
    private ChartPanel chart2;

    private int num_trial = 0;
    private String input = ".angryneeson52";

    private Boolean exported = false;


    private keyGUI(){
        super("Key Collector");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(900,600);

        pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        KeyList = new keyList();
        TrialList = new trialList();

        clearButton = new JButton("New user");
        clearButton.addActionListener(this);

        exportButton = new JButton("Export data");
        exportButton.addActionListener(this);

        newTrialButton = new JButton("New trial");
        newTrialButton.addActionListener(this);

        submitButton = new JButton("Sign in");
        submitButton.addActionListener(this);


        modelButton = new JButton("Model Analysis");
        modelButton.addActionListener(this);

        userID_Label = new JLabel("User ID: ");
        userID = new JTextField(15);

        fixedPhrase_Label = new JLabel("(User PW) Your password is:  "+ input);
        trial_Label = new JLabel("Num. of Trial : " + num_trial);

        displayArea = new JLabel("Hi, please type your designated userID and a given userPW below");
        typingArea = new JTextArea(20,20);
        scroll = new JScrollPane(typingArea);
        typingArea.setWrapStyleWord(true);
        typingArea.setAutoscrolls(true);
        typingArea.setLineWrap(true);

        //Key monitor class implemented inner class.
        KeyAdapter keyMonitor = new KeyAdapter(){
            public void keyTyped(){
            //Not using it.
            }
            public void keyPressed(KeyEvent event){
                int key = event.getKeyCode();
                if (key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_TAB){
                    event.consume();
                    displayArea.setText("Delete key or tab is not accepted in this environment. Click new trial to continue. ");
                }else if (key == KeyEvent.VK_ENTER){
                    event.consume();
                }
                else{
                    KeyList.add(1,event.getKeyChar(),event.getKeyCode(),event.getWhen());
                }
            }

            public void keyReleased(KeyEvent event){
                int key = event.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (typingArea.getText().equals(input)) {
                        if (KeyList.getSize() != input.length()){
                            event.consume();
                            displayArea.setText("Press enter");
                        }else{
                            displayArea.setText("Trial accepted. proceed to the next trial");
                            nextTrial();
                            event.consume();
                        }
                    } else {
                        displayArea.setText("You typed the wrong sentence. Proceed to the new trial.");
                        event.consume();
                    }
                }
                if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_BACK_SPACE && key != KeyEvent.VK_TAB) {
                    KeyList.add(0, event.getKeyChar(), event.getKeyCode(), event.getWhen());
                    System.out.println("Released: "+event.getKeyChar());
                }
            }
        };

        typingArea.addKeyListener(keyMonitor);

        chart1 = createChart("Dwell", TrialList, 0);
        chart2 = createChart("Flight", TrialList, 1);

        chart1.setVisible(true);
        chart2.setVisible(true);

        pane4.add(chart1);
        pane4.add(chart2);

        pane2.add(displayArea);
        pane2.add(userID_Label);
        pane2.add(userID);
        pane2.add(fixedPhrase_Label);
        pane2.add(typingArea);
        pane2.add(submitButton);
        pane2.add(trial_Label);
        pane3.add(newTrialButton);
        pane3.add(clearButton);
        pane3.add(exportButton);
        pane3.add(modelButton);
        pane2.add(pane3);
        pane.add(pane2);
        pane.add(pane4);

        this.add(pane);
        this.setVisible(true);
    }

    public  void actionPerformed(ActionEvent event){
        if (event.getSource().equals(clearButton)){
            initialize();
        }else if (event.getSource().equals(newTrialButton)){
            newTrial();
        }else if (event.getSource().equals(exportButton)){
            export();
        }else if (event.getSource().equals(modelButton)) {
            openModel();
        }else if (event.getSource().equals(submitButton)){
            nextTrial();
        }
    }
    private void newTrial(){
        typingArea.setText("");
        displayArea.setText("New trial! Please type a given phrase above");
        KeyList.clear();
        typingArea.requestFocus();

    }

    private void initialize(){
        this.exported = false;
        num_trial = 0;
        trial_Label.setText("Num. of Trial : " + num_trial);
        userID.setText("");
        typingArea.setText("");
        TrialList.clear();
        KeyList.clear();
        displayArea.setText("Hi, please type your designated userID and a given userPW below");
        userID.requestFocus();
    }

    private void nextTrial(){
        if (num_trial >= 15){
            displayArea.setText("15 Trials done. Please export the trial");
            return;
        }
        if (typingArea.getText().equals(input)) {
            num_trial++;
            TrialList.add(KeyList);
            KeyList = new keyList();
            newTrial();
            trial_Label.setText("Num. of Trial : " + num_trial);
            pane4.removeAll();
            chart1 = createChart("Dwell", TrialList, 0);
            chart2 = createChart("Flight", TrialList, 1);
            pane4.add(chart1);pane4.add(chart2);
            pane4.revalidate();
        }else {
            displayArea.setText("You typed the wrong sentence. Please click new trial to restart typing");
        }
    }

    private void export(){
        if (!exported){
            printResult(TrialList);
            exportUser user = new exportUser();
            if (user.writeCSV(TrialList,userID.getText())){
                exported = true;
            }
        }else{
            displayArea.setText("You already exported your data!");
        }
    }

    public void printResult(trialList TrialList){
        System.out.println(String.valueOf(TrialList.getSize()));
        for (int i = 0; i < TrialList.getSize(); i++) {
            keyList key = TrialList.getElement(i);
            for (int j = 0; j < key.getSize(); j++) {
                System.out.print(key.getElement(j).getChar());
            }
            System.out.println();
        }
    }

    public void openModel(){
        model Model = new model();
    }

    public ChartPanel createChart(String chartTitle, trialList trialData, int chartType) {
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Character", "Time",
                createDataSet(trialData, chartType),
                PlotOrientation.VERTICAL,
                true, true, false);
        ChartPanel chart = new ChartPanel(lineChart);
        chart.setPreferredSize(new java.awt.Dimension(100, 100));
        return chart;
    }

    private DefaultCategoryDataset createDataSet(trialList trialData, int chartType) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < trialData.getSize(); i++) {
            keyList key = trialData.getElement(i);
            for (int j = 0; j < key.getSize(); j++) {
                if (chartType == 0) {
                    dataset.addValue(key.getDwell(j), "trial " + String.valueOf(i + 1), String.valueOf(j));
                } else {
                    if (key.getFlight(j) != -1) {
                        dataset.addValue(key.getFlight(j), "trial " + String.valueOf(i + 1), String.valueOf(j));
                    }
                }
            }
        }
        return dataset;
    }
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception e){}

        new keyGUI();

    }


}