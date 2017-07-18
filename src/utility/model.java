package utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class model implements KeyListener{
    private JFrame frame = new JFrame("Model");
    private JPanel pane1;
    private JComboBox combo = new JComboBox();
    private JButton button1 = new JButton("Take me");


    public model(){
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3,3));

        combo.addItem("Statistical analysis");
        combo.addItem("Machine learning analysis");

        pane1 = new JPanel(new GridLayout(2,1));

        pane1.add(combo);
        pane1.add(button1);

        frame.add(pane1);
        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
