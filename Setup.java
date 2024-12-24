import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Setup extends JFrame implements ActionListener {
    private JRadioButton button1, button2;

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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create and add a label
        JLabel label = new JLabel("Select an option:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing

        // Create radio buttons
        button1 = new JRadioButton("Option 1: Bla");
        button2 = new JRadioButton("Option 2: Blb");
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
        panel.add(button1);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        panel.add(button2);

        //Add confirm button
        JButton button3 = new JButton();
        panel.add(button3);

        // Add panel to the frame
        add(panel);

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        new Setup();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            JOptionPane.showMessageDialog(this, "Option 1 selected!");
        } else if (e.getSource() == button2) {
            JOptionPane.showMessageDialog(this, "Option 2 selected!");
        }
    }
}
