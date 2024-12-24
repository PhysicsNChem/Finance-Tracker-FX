import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Setup extends JFrame implements ActionListener {
    private JRadioButton button1, button2;
    private JButton button3;
    private static boolean LanguageSelected = false;
    public static boolean englishSelected = false;

    public Setup() {
        // Set system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up JFrame
        setTitle("FBLA Project Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null); // Center the window

        // Create a JPanel with vertical BoxLayout
        JPanel RadioPanel = new JPanel();
        RadioPanel.setLayout(new BoxLayout(RadioPanel, BoxLayout.Y_AXIS));
        RadioPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create and add a label
        JLabel label = new JLabel("Select a langauge/Choissez votre langue:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        RadioPanel.add(label);
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing

        // Create radio buttons
        button1 = new JRadioButton("English");
        button2 = new JRadioButton("Français");
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Group the buttons so only one can be selected
        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);

        // Add ActionListeners
        button1.addActionListener(this);
        button2.addActionListener(this);

        // Add buttons to the panel
        RadioPanel.add(button1);
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        RadioPanel.add(button2);

        //Add confirm button
        JButton button3 = new JButton("Continue");
        button3.addActionListener(this);
        button3.setAlignmentX(Component.CENTER_ALIGNMENT);
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 250)));
        RadioPanel.add(button3);
        // Add panel to the frame
        add(RadioPanel);

        // Make the frame visible
        setVisible(true);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            JOptionPane.showMessageDialog(this, "Option 1 selected!");
        } else if (e.getSource() == button2) {
            JOptionPane.showMessageDialog(this, "Option 2 selected!");
        }
    }
    public static void main(String[] args) {
        new Setup();
    }
}
