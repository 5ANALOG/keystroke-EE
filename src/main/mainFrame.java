package main;

import keyInterface.keyGUI;
import javax.swing.*;

public class mainFrame {
    private mainFrame() {
        System.out.println("RUNNING...");
        //Create New JFrame (Main login frame)
        new keyGUI();
        //pack();
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            //UIManager.getSystemLookAndFeelClassName()
            //javax.swing.plaf.metal.MetalLookAndFeel
            //javax.swing.plaf.nimbus.NimbusLookAndFeel
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            ex.printStackTrace();
        }
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new mainFrame();
            }
        });
    }
}
