package app;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.sql.*;

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
        JButton manageBudgetButton = createButton("Manage Budget");
        JButton settingsButton = createButton("Settings");

        buttonPanel.add(overviewButton);
        buttonPanel.add(addTransactionButton);
        buttonPanel.add(viewTransactionsButton);
        buttonPanel.add(manageBudgetButton);
        buttonPanel.add(settingsButton);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(createOverviewPanel(), "Overview");
        contentPanel.add(createAddTransactionPanel(), "Add Transaction");
        contentPanel.add(createViewTransactionsPanel(), "View Transactions");
        contentPanel.add(createBudgetPanel(), "Manage Budget");
        contentPanel.add(createSettingsPanel(), "Settings");

        overviewButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Overview"));
        addTransactionButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Add Transaction"));
        viewTransactionsButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "View Transactions"));
        settingsButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Settings"));
        manageBudgetButton.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Manage Budget"));

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

        // Account Overview Panel
        JPanel accountOverviewPanel = new JPanel(new GridBagLayout());
        TitledBorder accountBorder = BorderFactory.createTitledBorder("Account Overview");
        accountBorder.setTitleColor(TEXT_COLOR);
        accountBorder.setTitleFont(BOLD_FONT);
        accountOverviewPanel.setBorder(accountBorder);
        accountOverviewPanel.setBackground(PANEL_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel balanceLabel = new JLabel(" \u25B2 Total Balance: Rs." + TransactionService.fetchBalance(user.getId()) +
                " | \u25BC Total Expense: Rs." + TransactionService.fetchExpense(user.getId()));
        balanceLabel.setForeground(TEXT_COLOR);
        balanceLabel.setFont(FONT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        accountOverviewPanel.add(balanceLabel, gbc);

        panel.add(accountOverviewPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        List<Transaction> transactions = TransactionService.getTransactionsByUser(user.getId());
        if (!transactions.isEmpty()) {
            JPanel recentTransactionsPanel = new JPanel(new GridBagLayout());
            TitledBorder recentTransactionsBorder = BorderFactory.createTitledBorder("Recent Transactions");
            recentTransactionsBorder.setTitleColor(TEXT_COLOR);
            recentTransactionsBorder.setTitleFont(BOLD_FONT);
            recentTransactionsPanel.setBorder(recentTransactionsBorder);
            recentTransactionsPanel.setBackground(PANEL_COLOR);

            int rowIndex = 0;
            for (Transaction transaction : transactions) {
                JLabel transactionLabel = new JLabel();
                transactionLabel.setForeground(TEXT_COLOR);
                transactionLabel.setFont(FONT);
                if (transaction.getType().equals("Income")) {
                    transactionLabel.setText("[+] [" + transaction.getCategory() + "] : Rs." + transaction.getAmount());
                } else {
                    transactionLabel.setText("[-] [" + transaction.getCategory() + "] : Rs." + transaction.getAmount());
                }

                gbc.gridx = 0;
                gbc.gridy = rowIndex++;
                recentTransactionsPanel.add(transactionLabel, gbc);
            }

            panel.add(recentTransactionsPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        }

        List<Budget> budgets = BudgetService.getBudgetsByUser(user.getId());
        if (!budgets.isEmpty()) {
            JPanel budgetSummaryPanel = new JPanel(new GridBagLayout());
            TitledBorder budgetBorder = BorderFactory.createTitledBorder("Budget Summary");
            budgetBorder.setTitleColor(TEXT_COLOR);
            budgetBorder.setTitleFont(BOLD_FONT);
            budgetSummaryPanel.setBorder(budgetBorder);
            budgetSummaryPanel.setBackground(PANEL_COLOR);

            int rowIndex = 0;
            for (Budget budget : budgets) {
                JPanel budgetItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                budgetItemPanel.setBackground(PANEL_COLOR);
                String formattedSpentPercent = String.format("%.2f", budget.getSpentPercent());

                JLabel budgetLabel = new JLabel("[" + Budget.getCategory(budget.getCategoryId()) + "] Budget: Rs." + budget.getAmount() +
                        " | Spent: Rs." + budget.getSpentAmount() + " [~" + formattedSpentPercent + "%] ");
                budgetLabel.setForeground(TEXT_COLOR);
                budgetLabel.setFont(FONT);

                JButton editButton = new JButton("Edit");
                editButton.addActionListener(e -> {
                    showEditBudgetDialog(budget);
                });

                budgetItemPanel.add(budgetLabel);
                budgetItemPanel.add(editButton);

                gbc.gridx = 0;
                gbc.gridy = rowIndex++;
                budgetSummaryPanel.add(budgetItemPanel, gbc);
            }

            panel.add(budgetSummaryPanel);
        }

        return panel;
    }

    private void showEditBudgetDialog(Budget budget) {
        JDialog dialog = new JDialog((Frame) null, "Edit Budget", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(300, 200);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel categoryLabel = new JLabel("Category:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(categoryLabel, gbc);

        JComboBox<String> categoryComboBox = new JComboBox<>();
        populateBudgetCategories(categoryComboBox);
        gbc.gridx = 1;
        dialog.add(categoryComboBox, gbc);


        JLabel amountLabel = new JLabel("Amount:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(amountLabel, gbc);

        JTextField amountField = new JTextField(20);
        amountField.setText(String.valueOf(budget.getAmount()));
        gbc.gridx = 1;
        dialog.add(amountField, gbc);

        JButton updateButton = new JButton("Update");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(updateButton, gbc);


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newCategory = (String) categoryComboBox.getSelectedItem();
                double newAmount;
                try {
                    newAmount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                budget.setCategoryID(Budget.fetchCategoryID(newCategory));
                budget.setAmount(newAmount);

                if(BudgetService.updateBudget(budget)){
                    refreshOverviewPanel();
                    JOptionPane.showMessageDialog(dialog, "Budget updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Invalid Value(s).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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

        JComboBox<String> categoryComboBox = new JComboBox<>();
        populateCategories(categoryComboBox);
        gbc.gridx = 1;
        panel.add(categoryComboBox, gbc);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setFont(FONT);
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(typeLabel, gbc);

        JComboBox<String> typeDropdown = new JComboBox<>();
        populateTransactionTypes(typeDropdown);
        gbc.gridx = 1;
        panel.add(typeDropdown, gbc);

        JLabel dateLabel = new JLabel("Date (dd-mm-yyyy):");
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setFont(FONT);
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(dateLabel, gbc);

        JFormattedTextField dateField = new JFormattedTextField(createDateFormatter());
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        JLabel noteLabel = new JLabel("Note:");
        noteLabel.setForeground(TEXT_COLOR);
        noteLabel.setFont(FONT);
        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(noteLabel, gbc);

        JTextField noteField = new JTextField();
        gbc.gridx = 1;
        panel.add(noteField, gbc);

        JButton addButton = createButton("Add");
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String amount = amountField.getText();
            String category = (String) categoryComboBox.getSelectedItem();
            String type = (String) typeDropdown.getSelectedItem();
            String date = dateField.getText();
            int category_id = TransactionService.fetchCategoryId(category);
            int type_id = TransactionService.fetchTransactionTypeId(type);
            String note = noteField.getText();

            if ((isValidDate(date) && isValidAmount(amount)) || note.isBlank()) {
                if (!isValidTransaction(category, type)) {
                    JOptionPane.showMessageDialog(this, "Invalid transaction: '" + type + "' cannot be paired with '" + category + "'.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transaction transaction = new Transaction(user.getId(), Double.parseDouble(amount), category, type, date, category_id, type_id, note);
                TransactionService.addTransaction(transaction);
                JOptionPane.showMessageDialog(this, "Transaction added successfully.", "Add Transaction", JOptionPane.INFORMATION_MESSAGE);
                clearFields(amountField, dateField);
                refreshOverviewPanel();
                refreshViewTransactionsPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check your entries.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
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

        String[] columns = {"ID", "Amount", "Category", "Type", "Date", "Note"};
        Object[][] data = new Object[transactions.size()][6];

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            data[i][0] = transaction.getId();
            data[i][1] = transaction.getAmount();
            data[i][2] = transaction.getCategory();
            data[i][3] = transaction.getType();
            data[i][4] = transaction.getDate();
            data[i][5] = transaction.getNote();
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
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

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

    private JPanel createBudgetPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Manage Budgets");
        titleLabel.setFont(BOLD_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;


        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setFont(FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(categoryLabel, gbc);

        JComboBox<String> categoryComboBox = new JComboBox<>();
        populateBudgetCategories(categoryComboBox);
        gbc.gridx = 1;
        panel.add(categoryComboBox, gbc);

        JLabel amountLabel = new JLabel("Budget Amount:");
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setFont(FONT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        JLabel dateRangeLabel = new JLabel("Time Period (Optional):");
        dateRangeLabel.setForeground(TEXT_COLOR);
        dateRangeLabel.setFont(FONT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(dateRangeLabel, gbc);

        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 4;
        panel.add(startDateLabel, gbc);

        JFormattedTextField startDateField = new JFormattedTextField(createDateFormatter());
        gbc.gridx = 1;
        panel.add(startDateField, gbc);

        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(endDateLabel, gbc);

        JFormattedTextField endDateField = new JFormattedTextField(createDateFormatter());
        gbc.gridx = 1;
        panel.add(endDateField, gbc);

        JButton saveBudgetButton = new JButton("Save Budget");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(saveBudgetButton, gbc);

        saveBudgetButton.addActionListener(e -> {
            String category = (String) categoryComboBox.getSelectedItem();
            String amount = amountField.getText();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            int categoryId = TransactionService.fetchCategoryId(category);

            if (isValidAmount(amount)) {
                Budget budget = new Budget(user.getId(), categoryId, Double.parseDouble(amount), startDate, endDate);
                BudgetService.addBudget(budget);
                JOptionPane.showMessageDialog(this, "Budget saved successfully.", "Budget", JOptionPane.INFORMATION_MESSAGE);
                refreshOverviewPanel();
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

    private boolean isValidTransaction(String category, String type) {
        if ("Income".equals(type) && !"Salary".equals(category)) {
            return false;
        } else if("Expense".equals(type) && "Salary".equals(category)) {
            return false;
        }
        return true;
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

    private void populateCategories(JComboBox<String> comboBox) {
            try(Connection conn = Database.Connect()){
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT category_name FROM categories");

                while (rs.next()) {
                    comboBox.addItem(rs.getString("category_name"));
                }
            }catch (SQLException e){
                SiLog.Message("Could not fetch categories: " + e.getMessage());
            }
        }

    private void populateBudgetCategories(JComboBox<String> comboBox) {
        try(Connection conn = Database.Connect()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT category_name FROM categories");

            while (rs.next()) {
                String category = rs.getString("category_name");

                if(category.equals("Salary"))
                    continue;

                comboBox.addItem(category);
            }
        }catch (SQLException e){
            SiLog.Message("Could not fetch categories: " + e.getMessage());
        }
    }


    private void populateTransactionTypes(JComboBox<String> dropDown){
            try(Connection conn = Database.Connect()){
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT type_name FROM transaction_types");

                while (rs.next()) {
                    dropDown.addItem(rs.getString("type_name"));
                }
            }catch (SQLException e){
                SiLog.Message("Could not fetch categories: " + e.getMessage());
            }
        }
}

