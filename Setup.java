import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Setup extends JFrame implements ActionListener {
    private JRadioButton button1, button2;
    private JButton button3;
    private boolean LanguageSelected = false;
    public boolean englishSelected = false;

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
        setLocationRelativeTo(null); // Centre the window
    }

    public void createFirstPanel() {
        // Create a JPanel with vertical BoxLayout
        JPanel RadioPanel = new JPanel();
        RadioPanel.setLayout(new BoxLayout(RadioPanel, BoxLayout.Y_AXIS));
        RadioPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20)); // Add padding

        // Create and add a label
        JLabel label = new JLabel("Select a langauge/Choisissez votre langue:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        RadioPanel.add(label);
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Add some spacing

        // Create radio buttons
        button1 = new JRadioButton("English");
        button2 = new JRadioButton("Français");
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Group the buttons so only one can be selected
        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);

        //Add ActionListener
        button1.addActionListener(this);
        button2.addActionListener(this);
        // Add buttons to the panel
        RadioPanel.add(button1);
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        RadioPanel.add(button2);

        //Add confirm button
        button3 = new JButton("Continue");
        button3.setAlignmentX(Component.CENTER_ALIGNMENT);
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 350)));
        button3.addActionListener(this);
        RadioPanel.add(button3);
        // Add panel to the frame
        add(RadioPanel);

        // Make the frame visible
        setVisible(true);
    }

    public void createSecondPanel() {
        getContentPane().removeAll();
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));
        themePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel label = new JLabel("Select a theme:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        themePanel.add(label);
        themePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel label2 = new JLabel("This can be changed later if you wish");
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        themePanel.add(label2);
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(this);
        getContentPane().add(themePanel);
        revalidate();
        repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button3) { // Check for "Continue" button press
            if (button1.isSelected() && !LanguageSelected) {
                englishSelected = true;
                LanguageSelected = true;
                createSecondPanel();
            } else if (button2.isSelected() && !LanguageSelected) {
                LanguageSelected = true;
                createSecondPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Please choose an option / Veuillez choisir une option", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new Setup().createFirstPanel();
    }

}