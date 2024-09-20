package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Objects;

public class LoginPanel extends JPanel {
    private AppUI parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Font customFont;

    public LoginPanel(AppUI parentFrame) {
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
            SiLog.Message("Could not load custom font: " + e.getMessage());
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0));

        // Top panel for title and logo
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel for login form
        JPanel loginPanel = createLoginPanel();
        add(loginPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(238,238,238));

        // Logo panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        logoPanel.setBackground(new Color(238,238,238));
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/file.png")));
            Image image = icon.getImage().getScaledInstance(210, 180, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            SiLog.Message("Could not load logo for login screen: " + e.getMessage());
        }
        logoPanel.add(logoLabel);
        topPanel.add(logoPanel, BorderLayout.CENTER);

        return topPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(238,238,238));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 20, 10, 20);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(usernameLabel, constraints);

        usernameField = new JTextField(15);
        usernameField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 1;
        loginPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(15);
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        constraints.gridx = 1;
        constraints.gridy = 1;
        loginPanel.add(passwordField, constraints);

        JButton loginButton = createStyledButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(20, 20, 20, 20);
        loginPanel.add(loginButton, constraints);

        JButton registerButton = createStyledButton("Register");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        loginPanel.add(registerButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                User loggedInUser = UserService.loginUser(username, password);
                if (loggedInUser != null) {
                    parentFrame.showDashboard(loggedInUser);
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this,
                            "Invalid username or password. Please try again.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showRegistration();
            }
        });

        return loginPanel;
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
