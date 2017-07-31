package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import utility.machineL;
import utility.statistics;

public class panelWizard extends JFrame implements ActionListener {

    public JPanel cards = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private statistics card1= new statistics();
    private machineL card2 = new machineL();
    private JComboBox combo;

    public panelWizard(){
        //Create panel card
        super();
        this.setSize(1000,800);
        this.setResizable(false);

        cards.setLayout(cardLayout);
        cards.setSize(1000, 800);
        cards.setName("Model Analysis");
        cards.add(card1, "stat");
        cards.add(card2, "ml");
        add(cards);

        combo = new JComboBox();
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
            if (str == "Statistical Analysis"){
                cardLayout.show(cards,"stat");
            }else{
                cardLayout.show(cards,"ml" );
            }
        }
    }
}
