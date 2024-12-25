import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Setup extends JFrame implements ActionListener {
    private JRadioButton button1, button2;
    private JButton button3, backButton;
    private boolean LanguageSelected = false;
    public boolean englishSelected = false;
    private boolean themeSelected = false;
    private boolean userConfirmed = false;
    private boolean darkModeSelected = false;

    public Setup() {
        // Set system look and feel. setLookAndFeel throws an exception, so try-catch is required to handle the method
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
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
        JLabel label = new JLabel("Select a langauge to begin/Choisissez votre langue pour commencer:");
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
        RadioPanel.add(Box.createRigidArea(new Dimension(0, 340)));
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
        themePanel.add(Box.createRigidArea(new Dimension(0, 340)));
        themePanel.add(label2);

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        backButton = new JButton("Back");
        backButton.addActionListener(this);
        buttonRow.add(backButton);

        button3 = new JButton("Continue");
        button3.addActionListener(this);
        buttonRow.add(button3);
        themePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        themePanel.add(buttonRow);
        getContentPane().add(themePanel);
        redraw();
    }
    public void createThirdPanel() {
        getContentPane().removeAll();
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel label = new JLabel("Now, what's your name? Add a profile picture and set a password");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(label);

        ImageIcon defaultIcon = new ImageIcon("resources/default-profile-picture.png-2731391301.png");
        JLabel defaultLabel = new JLabel(defaultIcon);
        userPanel.add(defaultLabel);

        userPanel.add(Box.createVerticalStrut(20));

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        backButton = new JButton("Back");
        backButton.addActionListener(this);
        buttonRow.add(backButton);
        button3 = new JButton("Continue");
        button3.addActionListener(this);
        buttonRow.add(button3);
        userPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        userPanel.add(buttonRow);
        getContentPane().add(userPanel);
        redraw();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button3 && LanguageSelected && !themeSelected) {
            getContentPane().removeAll();
            createThirdPanel();
            themeSelected = true;

        } else if (e.getSource() == button3 && !LanguageSelected) { // Check for "Continue" button press
            if (button1.isSelected()) {
                englishSelected = true;
                LanguageSelected = true;
                //Moves to next page
                createSecondPanel();
            } else if (button2.isSelected()) {
                LanguageSelected = true;
                createSecondPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Please choose an option / Veuillez choisir une option", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == backButton && themeSelected) {
            themeSelected = false;
            createSecondPanel();
        } else if (e.getSource() == backButton) {
            themeSelected = false;
            LanguageSelected = false;
            englishSelected = false;
            getContentPane().removeAll();
            createFirstPanel();
            redraw();
        }


    }
    public void redraw(){
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        new Setup().createFirstPanel();
    }

}