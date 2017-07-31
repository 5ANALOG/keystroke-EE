package keyInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import utility.*;
import main.panelWizard;

public class keyGUI extends JFrame implements ActionListener{
    private keyList KeyList;
    private trialList TrialList;

    private JPanel chartPane = new JPanel(new GridLayout(2,1));

    private JTextField userID;
    private JTextArea typingArea;
    private JLabel trial_Label;
    private JLabel displayArea;

    private JButton submitButton, clearButton, exportButton, newTrialButton, modelButton;

    private ChartPanel chart1;
    private ChartPanel chart2;

    private int num_trial = 0;
    private String input = ".angryneeson52";

    private Boolean exported = false;

    public keyGUI(){

        //Frame information
        super("Keystroke logger - By Shawn Lee");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(1000,800);

        Font font = new Font("BM HANNA 11yrs old OTF", Font.BOLD, 15);

        //Create KeyList and TrialList object
        KeyList = new keyList();
        TrialList = new trialList();

        JPanel recordPane = new JPanel(new GridLayout(1, 2));
        recordPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Key monitor class implemented inner class.
        KeyAdapter keyMonitor = new KeyAdapter(){
            public void keyPressed(KeyEvent event){
                int key = event.getKeyCode();
                if (key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_TAB){
                    event.consume();
                    displayArea.setForeground(Color.RED);
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
                            displayArea.setForeground(Color.GREEN);
                            displayArea.setText("Press enter");
                        }else{
                            displayArea.setForeground(Color.GREEN);
                            displayArea.setText("Trial accepted. proceed to the next trial");
                            nextTrial();
                            event.consume();
                        }
                    } else {
                        displayArea.setForeground(Color.RED);
                        displayArea.setText("You typed the wrong sentence. Proceed to the new trial.");
                        event.consume();
                    }
                }
                if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_BACK_SPACE && key != KeyEvent.VK_TAB) {
                    KeyList.add(0, event.getKeyChar(), event.getKeyCode(), event.getWhen());
                    //System.out.println("Released: "+event.getKeyChar());
                }
            }
        };

        chart1 = createChart("Dwell", TrialList, 0);
        chart2 = createChart("Flight", TrialList, 1);
        chart1.setVisible(true);
        chart2.setVisible(true);
        chartPane.add(chart1);
        chartPane.add(chart2);

        JPanel loginPane = new JPanel(new GridLayout(10, 1));
        JLabel userID_Label = new JLabel("User ID: ");
        userID_Label.setFont(font);
        userID = new JTextField(15);
        userID.setFont(font);
        JLabel fixedPhrase_Label = new JLabel("(User PW) Your password is:  " + input);
        fixedPhrase_Label.setFont(font);
        trial_Label = new JLabel("Num. of Trial : " + num_trial);
        trial_Label.setFont(font);
        displayArea = new JLabel("Hi, please type your designated userID and a given userPW below");
        displayArea.setFont(font);

        typingArea = new JTextArea(20,20);
        typingArea.setFont(font);
        typingArea.setWrapStyleWord(true);
        typingArea.setAutoscrolls(true);
        typingArea.setLineWrap(true);
        loginPane.add(displayArea);
        loginPane.add(userID_Label);
        loginPane.add(userID);
        loginPane.add(fixedPhrase_Label);
        loginPane.add(typingArea);


        JPanel buttonPane = new JPanel(new FlowLayout());
        clearButton = new JButton("New user");
        clearButton.setFont(font);
        clearButton.addActionListener(this);
        exportButton = new JButton("Export data");
        exportButton.setFont(font);
        exportButton.addActionListener(this);
        newTrialButton = new JButton("New trial");
        newTrialButton.setFont(font);
        newTrialButton.addActionListener(this);
        submitButton = new JButton("Submit Trial");
        submitButton.setFont(font);
        submitButton.addActionListener(this);
        modelButton = new JButton("Open Model Analysis");
        modelButton.setFont(font);
        modelButton.addActionListener(this);
        buttonPane.add(newTrialButton);
        buttonPane.add(clearButton);
        buttonPane.add(exportButton);
        buttonPane.add(modelButton);

        loginPane.add(submitButton);
        loginPane.add(trial_Label);

        loginPane.add(buttonPane);
        recordPane.add(loginPane);
        recordPane.add(chartPane);
        this.add(recordPane);
        this.setVisible(true);

        //Add keyMonitor listener to typing area
        typingArea.addKeyListener(keyMonitor);
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
            chartPane.removeAll();
            chart1 = createChart("Dwell", TrialList, 0);
            chart2 = createChart("Flight", TrialList, 1);
            chartPane.add(chart1);chartPane.add(chart2);
            chartPane.revalidate();
        }else {
            displayArea.setForeground(Color.RED);
            displayArea.setText("You typed the wrong sentence. Please click new trial button. ");
        }
    }
    private void export(){
        if (num_trial < 15){
            displayArea.setForeground(Color.RED);
            displayArea.setText("Please do 15 trials before export!");
            return;
        }
        if (!exported){
            exportUser user = new exportUser();
            if (user.writeCSV(TrialList,userID.getText())){
                exported = true;
                displayArea.setForeground(Color.GREEN);
                displayArea.setText("Succesfully Exported your trials!");
            }else{
                displayArea.setForeground(Color.RED);
                displayArea.setText("Something wrong while exporting trials!");
            }
        }else{
            displayArea.setText("You already exported your data!");
        }
    }

    private void openModel() {
        panelWizard panel = new panelWizard();
    }
    private ChartPanel createChart(String chartTitle, trialList trialData, int chartType) {
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
}