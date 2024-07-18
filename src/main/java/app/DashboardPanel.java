package app;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardPanel extends JPanel {
    private User user;
    private JPanel contentPanel;
    private static final Color BACKGROUND_COLOR = new Color(60, 63, 65);
    private static final Color PANEL_COLOR = new Color(43, 43, 43);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);

    public DashboardPanel(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBackground(PANEL_COLOR);
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUser() + "!");
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setFont(BOLD_FONT);
        userPanel.add(welcomeLabel);

        JButton logoutButton = createButton("Logout");
        logoutButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            AppUI ui = new AppUI();
            ui.start();
        });
        userPanel.add(logoutButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton overviewButton = createButton("Overview");
        JButton addTransactionButton = createButton("Add Transaction");
        JButton viewTransactionsButton = createButton("View Transactions");
        JButton settingsButton = createButton("Settings");

        buttonPanel.add(overviewButton);
        buttonPanel.add(addTransactionButton);
        buttonPanel.add(viewTransactionsButton);
        buttonPanel.add(settingsButton);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(createOverviewPanel(), "Overview");
        contentPanel.add(createAddTransactionPanel(), "Add Transaction");
        contentPanel.add(createViewTransactionsPanel(), "View Transactions");
        contentPanel.add(createSettingsPanel(), "Settings");

        overviewButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Overview"));
        addTransactionButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Add Transaction"));
        viewTransactionsButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "View Transactions"));
        settingsButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Settings"));

        add(userPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(75, 110, 175));
        button.setForeground(TEXT_COLOR);
        button.setFont(FONT);
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel overviewLabel = new JLabel("Account Overview");
        overviewLabel.setForeground(TEXT_COLOR);
        overviewLabel.setFont(BOLD_FONT);
        panel.add(overviewLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        JLabel balanceLabel = new JLabel("Total Balance: Rs." + TransactionService.fetchBalance(user.getId()));
        balanceLabel.setForeground(TEXT_COLOR);
        balanceLabel.setFont(FONT);
        panel.add(balanceLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        JLabel recentTransactionsLabel = new JLabel("Recent Transactions");
        recentTransactionsLabel.setForeground(TEXT_COLOR);
        recentTransactionsLabel.setFont(BOLD_FONT);
        panel.add(recentTransactionsLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        List<Transaction> transactions = TransactionService.getTransactionsByUser(user.getId());
        for (Transaction transaction : transactions) {
            JLabel transactionLabel = new JLabel("[" + transaction.getCategory() + "] : Rs." + transaction.getAmount());
            transactionLabel.setForeground(TEXT_COLOR);
            transactionLabel.setFont(FONT);
            panel.add(transactionLabel);
        }

        return panel;
    }

    private JPanel createAddTransactionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Add Transaction");
        titleLabel.setFont(BOLD_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setFont(FONT);
        gbc.gridx = 0;
        panel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setFont(FONT);
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(categoryLabel, gbc);

        JTextField categoryField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setFont(FONT);
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(typeLabel, gbc);

        JTextField typeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(typeField, gbc);

        JLabel dateLabel = new JLabel("Date (dd-mm-yyyy):");
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setFont(FONT);
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(dateLabel, gbc);

        JFormattedTextField dateField = new JFormattedTextField(createDateFormatter());
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        JButton addButton = createButton("Add");
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String amount = amountField.getText();
            String category = categoryField.getText();
            String type = typeField.getText();
            String date = dateField.getText();
            if (isValidDate(date) && isValidAmount(amount)) {
                Transaction transaction = new Transaction(user.getId(), Double.parseDouble(amount), category, type, date);
                TransactionService.addTransaction(transaction);
                JOptionPane.showMessageDialog(this, "Transaction added successfully.", "Add Transaction", JOptionPane.INFORMATION_MESSAGE);
                clearFields(amountField, categoryField, typeField, dateField);
                refreshOverviewPanel();
                refreshViewTransactionsPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check your entries.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private MaskFormatter createDateFormatter() {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("##-##-####");
        } catch (ParseException ex) {
            SiLog.Message(ex.getMessage());
        }
        return formatter;
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null;
        } catch (ParseException ex) {
            SiLog.Message(ex.getMessage());
            return false;
        }
    }

    private boolean isValidAmount(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException ex) {
            SiLog.Message(ex.getMessage());
            return false;
        }
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private void exportTransactionsToFile(List<Transaction> transactions, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Transaction transaction : transactions) {
                writer.println("ID:[" + transaction.getId() + "] | Amount: Rs." + transaction.getAmount() + "| Category: " + transaction.getCategory() + "| Type: " + transaction.getType() + "| Date: " + transaction.getDate());
            }
            JOptionPane.showMessageDialog(null,
                    "Transaction data exported successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error exporting transaction data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void refreshOverviewPanel() {
        JPanel overviewPanel = createOverviewPanel();
        contentPanel.add(overviewPanel, "Overview");
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Overview");
    }

    private void refreshViewTransactionsPanel() {
        JPanel viewTransactionsPanel = createViewTransactionsPanel();
        contentPanel.add(viewTransactionsPanel, "View Transactions");
    }

    private JPanel createViewTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel transactionsLabel = new JLabel("All Transactions");
        transactionsLabel.setForeground(TEXT_COLOR);
        transactionsLabel.setFont(BOLD_FONT);
        panel.add(transactionsLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        List<Transaction> transactions = TransactionService.getTransactionsByUser(user.getId());

        String[] columns = {"ID", "Amount", "Category", "Type", "Date"};
        Object[][] data = new Object[transactions.size()][5];

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            data[i][0] = transaction.getId();
            data[i][1] = transaction.getAmount();
            data[i][2] = transaction.getCategory();
            data[i][3] = transaction.getType();
            data[i][4] = transaction.getDate();
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(PANEL_COLOR);
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Settings");
            titleLabel.setForeground(TEXT_COLOR);
            titleLabel.setFont(BOLD_FONT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(titleLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

            //Update Password Panel
            JPanel updatePasswordPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            updatePasswordPanel.setBorder(BorderFactory.createTitledBorder("Update Password"));

            JLabel currentPasswordLabel = new JLabel("Current Password:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            updatePasswordPanel.add(currentPasswordLabel, gbc);

            JPasswordField currentPasswordField = new JPasswordField(20);
            gbc.gridx = 1;
            updatePasswordPanel.add(currentPasswordField, gbc);

            JLabel newPasswordLabel = new JLabel("New Password:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            updatePasswordPanel.add(newPasswordLabel, gbc);

            JPasswordField newPasswordField = new JPasswordField(20);
            gbc.gridx = 1;
            updatePasswordPanel.add(newPasswordField, gbc);

            JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            updatePasswordPanel.add(confirmPasswordLabel, gbc);

            JPasswordField confirmPasswordField = new JPasswordField(20);
            gbc.gridx = 1;
            updatePasswordPanel.add(confirmPasswordField, gbc);

            JButton updateButton = new JButton("Update");
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            updatePasswordPanel.add(updateButton, gbc);

            updateButton.addActionListener(e -> {
                String currentPassword = new String(currentPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "Please fill in all fields.", "Password Update Error", JOptionPane.ERROR_MESSAGE);
                } else if (!BCrypt.checkpw(currentPassword, UserService.fetchHash(user.getId()))) {
                    JOptionPane.showMessageDialog(panel,
                            "Current password is incorrect.", "Password Update Error", JOptionPane.ERROR_MESSAGE);
                } else if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(panel,
                            "New password and confirmation do not match.", "Password Update Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    user.setPassword(newPassword);
                    UserService.updatePassword(user, newPassword);
                    JOptionPane.showMessageDialog(panel,
                            "Password updated successfully.", "Password Updated", JOptionPane.INFORMATION_MESSAGE);

                    currentPasswordField.setText("");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                }
            });

            panel.add(updatePasswordPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

            //Update Username Panel
            JPanel updateUsernamePanel = new JPanel(new GridBagLayout());
            updateUsernamePanel.setBorder(BorderFactory.createTitledBorder("Update Username"));

            JLabel currentUsernameLabel = new JLabel("Current Username: " + user.getUser());
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            updateUsernamePanel.add(currentUsernameLabel, gbc);

            JLabel newUsernameLabel = new JLabel("New Username:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            updateUsernamePanel.add(newUsernameLabel, gbc);

            JTextField newUsernameField = new JTextField(20);
            gbc.gridx = 1;
            updateUsernamePanel.add(newUsernameField, gbc);

            JButton updateUsernameButton = new JButton("Update Username");
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            updateUsernamePanel.add(updateUsernameButton, gbc);

            updateUsernameButton.addActionListener(e -> {
                String newUsername = newUsernameField.getText().trim();
                if (newUsername.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "New username cannot be empty.", "Username Update Error", JOptionPane.ERROR_MESSAGE);
                } else if (newUsername.equals(user.getUser())) {
                    JOptionPane.showMessageDialog(panel,
                            "New username must be different from the current one.", "Username Update Error", JOptionPane.ERROR_MESSAGE);
                }else if(UserService.usernameExists(newUsername)){
                    JOptionPane.showMessageDialog(panel,
                            "Username already exists.", "Username Update Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    user.setUser(newUsername);
                    UserService.updateUserName(user, newUsername);
                    currentUsernameLabel.setText("Current Username: " + user.getUser());
                    JOptionPane.showMessageDialog(panel,
                            "Username updated successfully.", "Username Updated", JOptionPane.INFORMATION_MESSAGE);

                    newUsernameField.setText("");
                }
            });

            panel.add(updateUsernamePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

            //Export Data Panel
            JPanel exportDataPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            exportDataPanel.setBorder(BorderFactory.createTitledBorder("Export Data"));

            JButton exportDataButton = new JButton("Export Data");
            exportDataButton.addActionListener(e -> {
                String filePath = "transactions.txt";
                List<Transaction> transactions = TransactionService.getTransactionsByUser(user.getId());
                exportTransactionsToFile(transactions, filePath);
            });
            exportDataPanel.add(exportDataButton);

            panel.add(exportDataPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

            // Account Deletion Panel
            JPanel deleteAccountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            deleteAccountPanel.setBorder(BorderFactory.createTitledBorder("Account Deletion"));

            JButton deleteAccountButton = new JButton("Delete Account");
            deleteAccountButton.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(panel,
                        "Are you sure you want to delete your account?\nThis action cannot be undone.",
                        "Confirm Account Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    UserService.deleteUser(user);
                    JOptionPane.showMessageDialog(panel,
                            "Your account has been deleted.", "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(1);
                }
            });
            deleteAccountPanel.add(deleteAccountButton);

            panel.add(deleteAccountPanel);

            return panel;

        }

}

