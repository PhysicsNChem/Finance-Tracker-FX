import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Setup extends JFrame implements ActionListener{
    public Setup()  {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        //Create JFrame
        JFrame frame1;
        frame1 = new JFrame("FBLA Project Setup");
        frame1.setLocationRelativeTo(null);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(800,600);
        frame1.setResizable(false);
        //Create main panel with a layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        //Add instruction label
        JLabel label = new JLabel("Choose a language/Choissisez votre langue");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        //Create radio buttons
        JRadioButton button1 = new JRadioButton("Bla");
        JRadioButton button2 = new JRadioButton("Blb");
        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);
        button1.addActionListener(this);
        button2.addActionListener(this);
        frame1.add(button1);
        frame1.add(button2);
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame1.add(panel);

        frame1.setVisible(true);

    }

    public static void main(String[] args) {
        Setup setup = new Setup();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
