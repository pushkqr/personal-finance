package app;

import org.apache.batik.swing.JSVGCanvas;

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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(238, 238, 238));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        logoPanel.setBackground(new Color(238, 238, 238));

        JSVGCanvas svgCanvas = new JSVGCanvas();
        svgCanvas.setBackground(new Color(0, 0, 0, 0));
        try {

            String svgPath = Objects.requireNonNull(getClass().getResource("/file.svg")).toString();
            svgCanvas.setURI(svgPath);
        } catch (Exception e) {
            SiLog.Message("Could not load SVG logo for login screen: " + e.getMessage());
        }

        svgCanvas.setPreferredSize(new Dimension(210, 180));
        logoPanel.add(svgCanvas);
        topPanel.add(logoPanel, BorderLayout.CENTER);

        return topPanel;
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
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        registrationPanel.add(registerButton, constraints);

        JButton backButton = createStyledButton("Back to Login");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
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
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 20 is the radius for round corners
                super.paintComponent(g);
            }
        };

        button.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false); // Set to false to let the custom painting work
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
