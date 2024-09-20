package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Objects;

public class RegistrationPanel extends JPanel {
    private AppUI parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Font customFont;

    public RegistrationPanel(AppUI parentFrame) {
        this.parentFrame = parentFrame;
        loadCustomFont();
        initializeUI();
    }

    private void loadCustomFont() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/oswald1.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
        } catch (Exception e) {
            SiLog.Message(e.getMessage());
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(238, 238, 238));

        // Logo panel
        JPanel logoPanel = createLogoPanel();
        add(logoPanel, BorderLayout.NORTH);

        // Registration form panel
        JPanel registrationPanel = createRegistrationPanel();
        add(registrationPanel, BorderLayout.CENTER);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        logoPanel.setBackground(new Color(238,238,238));

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/file.png")));
            Image image = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            SiLog.Message(e.getMessage());
        }

        logoPanel.add(logoLabel);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Reduce gap from top
        return logoPanel;
    }

    private JPanel createRegistrationPanel() {
        JPanel registrationPanel = new JPanel(new GridBagLayout());
        registrationPanel.setBackground(new Color(238,238,238));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Choose a Username:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 0;
        constraints.gridy = 0;
        registrationPanel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        usernameField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 1;
        registrationPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Choose a Password:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 0;
        constraints.gridy = 1;
        registrationPanel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 1;
        constraints.gridy = 1;
        registrationPanel.add(passwordField, constraints);

        JButton registerButton = createStyledButton("Register");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        registrationPanel.add(registerButton, constraints);

        JButton backButton = createStyledButton("Back to Login");
        constraints.gridx = 0;
        constraints.gridy = 3;
        registrationPanel.add(backButton, constraints);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty() || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "Invalid Input(s).",
                            "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
                else if (UserService.usernameExists(username)) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "Username already exists. Please choose another one.",
                            "Registration Failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    User newUser = new User(username, password);
                    UserService.registerUser(newUser);
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "User registered successfully. You can now login.",
                            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

                    usernameField.setText("");
                    passwordField.setText("");

                    parentFrame.showLoginScreen();
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showLoginScreen();
            }
        });

        return registrationPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 144, 255));
            }
        });

        return button;
    }

}
