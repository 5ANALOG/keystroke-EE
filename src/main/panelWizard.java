package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import keyAnalyzer.mlGUI;
import keyAnalyzer.statisticsGUI;

public class panelWizard extends JFrame implements ActionListener {

    private JPanel cards = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private statisticsGUI card1= new statisticsGUI();
    private mlGUI card2 = new mlGUI();
    private JComboBox<String> combo;

    public panelWizard(){
        //Create panel card
        super("Keystroke model analyzer by Shawn Lee");
        this.setSize(800,600);
        this.setResizable(false);

        cards.setLayout(cardLayout);
        cards.setSize(800, 600);
        cards.setName("Model Analysis");
        cards.add(card1, "stat");
        cards.add(card2, "ml");
        add(cards);

        combo = new JComboBox<>();
        combo.addItem("Statistical Analysis");
        combo.addItem("Machine Learning");
        combo.setEditable(false);
        combo.addActionListener(this);
        JPanel topPane = new JPanel();
        topPane.add(combo);
        add(topPane, BorderLayout.NORTH);
        setVisible(true);
    }
    //Action Listener
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == combo) {
            String str = (String) combo.getSelectedItem();
            if (Objects.equals(str, "Statistical Analysis")){
                cardLayout.show(cards,"stat");
            }else{
                cardLayout.show(cards,"ml" );
            }
        }
    }
}
