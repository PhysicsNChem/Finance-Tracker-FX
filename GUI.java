import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI {
    public GUI(){
        JFrame frame = new JFrame("FBLA Project");
        JLabel l1 = new JLabel("Balance");
        frame.add(l1);
        frame.setSize(1280,720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
