import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

public class FinanceManagementSystem extends JFrame {
    private JPanel loginPanel, mainPanel, cardPanel, menuPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private CardLayout cardLayout;
    private JButton myBalanceButton, withdrawalButton, depositButton, emiCalculatorButton, myStocksButton, transactionHistoryButton;
    private JLabel balanceLabel;
    private double balance = 10000.0;
    private JTextArea transactionHistoryArea;

    // Stack to keep track of visited pages
    private Stack<String> pageStack;

    // Map to store account balances
    private Map<String, Account> accounts;

    // JComboBox to select account type
    private JComboBox<String> accountTypeComboBox;

    public FinanceManagementSystem() {
        setTitle("Finance Management System");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize page stack
        pageStack = new Stack<>();

        // Initialize accounts
        initializeAccounts();

        // Login Panel
        // Modify the loginPanel initialization and layout
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(0, 51, 102)); // Set background color to regal blue

        // Create GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        gbc.insets = new Insets(20, 20, 20, 20); // Increase padding
        loginPanel.add(new JLabel("WELCOME TO FINANCE MANAGEMENT SYSTEM"), gbc);
        gbc.gridy++;
        // Add Username label and field
        loginPanel.add(new JLabel("USERNAME"), gbc);

        gbc.gridy++;
        usernameField = new JTextField(20); // Increase field size
        loginPanel.add(usernameField, gbc);

        // Add Password label and field
        gbc.gridy++;
        loginPanel.add(new JLabel("PASSWORD"), gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20); // Increase field size
        loginPanel.add(passwordField, gbc);

        // Add Login button
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER; // Center align the button
        gbc.fill = GridBagConstraints.NONE; // Prevent button from stretching
        loginButton = new JButton("Login");
        loginButton.setBackground(Color.WHITE); // Set button background color
        loginPanel.add(loginButton, gbc);

        // Create a gray gradient border
        Border grayGradientBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2), // Outer line border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Inner empty border for margin
        );

        // Apply the gray gradient border to each component
        usernameField.setBorder(grayGradientBorder);
        passwordField.setBorder(grayGradientBorder);
        loginButton.setBorder(grayGradientBorder);


        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Add Panels to Card Panel
        cardPanel.add(loginPanel, "login");

        // Panels for different functionalities
        JPanel dashboardPanel = new JPanel(new GridLayout(7, 1)); // Increased grid size to accommodate account type selector
        myBalanceButton = new JButton("Check Balance");
        withdrawalButton = new JButton("Withdraw");
        depositButton = new JButton("Deposit");
        emiCalculatorButton = new JButton("EMI Calculator");
        myStocksButton = new JButton("My Stocks");
        transactionHistoryButton = new JButton("Transaction History");
        dashboardPanel.add(myBalanceButton);
        dashboardPanel.add(withdrawalButton);
        dashboardPanel.add(depositButton);
        dashboardPanel.add(emiCalculatorButton);
        dashboardPanel.add(myStocksButton);
        dashboardPanel.add(transactionHistoryButton);

        // Add Action Listeners for dashboard buttons
        myBalanceButton.addActionListener(e -> {
            pageStack.push("dashboard");
            cardLayout.show(cardPanel, "myBalancePage");
        });

        withdrawalButton.addActionListener(e -> {
            pageStack.push("dashboard");
            cardLayout.show(cardPanel, "withdrawalPage");
        });

        depositButton.addActionListener(e -> {
            pageStack.push("dashboard");
            cardLayout.show(cardPanel, "depositPage");
        });

        emiCalculatorButton.addActionListener(e -> {
            pageStack.push("dashboard");
            cardLayout.show(cardPanel, "emiCalculatorPage");
        });

        myStocksButton.addActionListener(e -> {
            pageStack.push("dashboard");
            cardLayout.show(cardPanel, "myStocksPage");
        });

        transactionHistoryButton.addActionListener(e -> {
            pageStack.push("dashboard");
            cardLayout.show(cardPanel, "transactionHistoryPage");
        });

        // Add Back button action listener
        JButton myBalanceBackButton = new JButton("Back");
        myBalanceBackButton.addActionListener(e -> navigateBack());
        JButton withdrawalBackButton = new JButton("Back");
        withdrawalBackButton.addActionListener(e -> navigateBack());
        JButton depositBackButton = new JButton("Back");
        depositBackButton.addActionListener(e -> navigateBack());
        JButton emiCalculatorBackButton = new JButton("Back");
        emiCalculatorBackButton.addActionListener(e -> navigateBack());
        JButton myStocksBackButton = new JButton("Back");
        myStocksBackButton.addActionListener(e -> navigateBack());
        JButton transactionHistoryBackButton = new JButton("Back");
        transactionHistoryBackButton.addActionListener(e -> navigateBack());

        // Add Back button to each page
        JPanel myBalancePage = new JPanel(new BorderLayout());
        balanceLabel = new JLabel("Balance: $" + balance);
        myBalancePage.add(balanceLabel, BorderLayout.CENTER);
        myBalancePage.add(myBalanceBackButton, BorderLayout.NORTH); // Align to top

        JPanel withdrawalPage = new JPanel(new BorderLayout());
        withdrawalPage.add(new JLabel("Withdrawal Page"), BorderLayout.NORTH);
        JTextField withdrawalAmountField = new JTextField();
        JLabel withdrawalLabel = new JLabel("ENTER AMOUNT");
        withdrawalLabel.setHorizontalAlignment(SwingConstants.CENTER); // Align at center
        withdrawalPage.add(withdrawalLabel, BorderLayout.CENTER); // Align at center
        withdrawalPage.add(withdrawalAmountField, BorderLayout.CENTER);
        JButton withdrawalConfirmButton = new JButton("Confirm Withdrawal");
        withdrawalPage.add(withdrawalConfirmButton, BorderLayout.SOUTH);
        withdrawalPage.add(withdrawalBackButton, BorderLayout.WEST); // Shifted to top left

        // Add action listener for withdrawal confirmation
        withdrawalConfirmButton.addActionListener(e -> {
            try {
                double withdrawalAmount = Double.parseDouble(withdrawalAmountField.getText());
                if (withdrawalAmount > 0) {
                    // Get the selected account type from the JComboBox
                    String selectedAccount = (String) accountTypeComboBox.getSelectedItem();
                    if (selectedAccount != null) {
                        if (withdrawalAmount <= accounts.get(selectedAccount).getBalance()) {
                            accounts.get(selectedAccount).withdraw(withdrawalAmount);
                            balanceLabel.setText("Balance: $" + accounts.get(selectedAccount).getBalance());
                            recordTransaction("Withdrawal", selectedAccount, withdrawalAmount);
                            JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Withdrawal Successful!");
                        } else {
                            JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Insufficient balance. Please enter a smaller amount.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid withdrawal amount. Please enter a positive value.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid input. Please enter a numerical value.");
            }
        });

        JPanel depositPage = new JPanel(new BorderLayout());
        depositPage.add(new JLabel("Deposit Page"), BorderLayout.NORTH);
        JTextField depositAmountField = new JTextField();
        JLabel depositLabel = new JLabel("ENTER AMOUNT");
        depositLabel.setHorizontalAlignment(SwingConstants.CENTER); // Align at center
        depositPage.add(depositLabel, BorderLayout.CENTER); // Align at center
        depositPage.add(depositAmountField, BorderLayout.CENTER);
        JButton depositConfirmButton = new JButton("Confirm Deposit");
        depositPage.add(depositConfirmButton, BorderLayout.SOUTH);
        depositPage.add(depositBackButton, BorderLayout.WEST); // Shifted to top left

        // Add action listener for deposit confirmation
        depositConfirmButton.addActionListener(e -> {
            try {
                double depositAmount = Double.parseDouble(depositAmountField.getText());
                if (depositAmount > 0) {
                    // Get the selected account type from the JComboBox
                    String selectedAccount = (String) accountTypeComboBox.getSelectedItem();
                    if (selectedAccount != null) {
                        accounts.get(selectedAccount).deposit(depositAmount);
                        balanceLabel.setText("Balance: $" + accounts.get(selectedAccount).getBalance());
                        recordTransaction("Deposit", selectedAccount, depositAmount);
                        JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Deposit Successful!");
                    }
                } else {
                    JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid deposit amount. Please enter a positive value.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid input. Please enter a numerical value.");
            }
        });

        // Rest of the code remains unchanged...


        JPanel emiCalculatorPage = new JPanel(new BorderLayout());
        emiCalculatorPage.add(new JLabel("EMI Calculator"), BorderLayout.NORTH);
        JPanel emiInputsPanel = new JPanel(new GridLayout(4, 2));
        JTextField principalField = new JTextField();
        JTextField interestRateField = new JTextField();
        JTextField timeField = new JTextField();
        JComboBox<String> timeUnitComboBox = new JComboBox<>(new String[]{"Months", "Years"});
        emiInputsPanel.add(new JLabel("Principal Amount:"));
        emiInputsPanel.add(principalField);
        emiInputsPanel.add(new JLabel("Interest Rate (%):"));
        emiInputsPanel.add(interestRateField);
        emiInputsPanel.add(new JLabel("Time:"));
        emiInputsPanel.add(timeField);
        emiInputsPanel.add(new JLabel("Time Unit:"));
        emiInputsPanel.add(timeUnitComboBox);
        emiCalculatorPage.add(emiInputsPanel, BorderLayout.CENTER);
        JButton calculateEmiButton = new JButton("Calculate EMI");
        emiCalculatorPage.add(calculateEmiButton, BorderLayout.SOUTH);
        emiCalculatorPage.add(emiCalculatorBackButton, BorderLayout.WEST); // Shifted to top left

        // Add action listener for EMI calculation
        calculateEmiButton.addActionListener(e -> {
            try {
                double principal = Double.parseDouble(principalField.getText());
                double interestRate = Double.parseDouble(interestRateField.getText());
                double time = Double.parseDouble(timeField.getText());
                String timeUnit = timeUnitComboBox.getSelectedItem().toString();
                double emi, totalInterest, totalAmount;
                if (principal > 0 && interestRate > 0 && time > 0) {
                    if (timeUnit.equals("Years")) {
                        time *= 12; // Convert years to months
                    }
                    interestRate /= 100; // Convert interest rate from percentage to decimal
                    double monthlyInterest = interestRate / 12;
                    emi = (principal * monthlyInterest * Math.pow(1 + monthlyInterest, time)) / (Math.pow(1 + monthlyInterest, time) - 1);
                    totalInterest = emi * time - principal;
                    totalAmount = principal + totalInterest;
                    // Format the results to two decimal places
                    emi = Math.round(emi * 100.0) / 100.0;
                    totalInterest = Math.round(totalInterest * 100.0) / 100.0;
                    totalAmount = Math.round(totalAmount * 100.0) / 100.0;
                    JOptionPane.showMessageDialog(FinanceManagementSystem.this, "EMI: $" + emi + "\nTotal Interest: $" + totalInterest + "\nTotal Amount: $" + totalAmount);
                } else {
                    JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid input. Please enter positive values.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid input. Please enter numerical values.");
            }
        });

        JPanel transactionHistoryPage = new JPanel(new BorderLayout());
        transactionHistoryPage.setBackground(Color.BLUE);
        transactionHistoryArea = new JTextArea(20, 40);
        transactionHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionHistoryArea);
        transactionHistoryPage.add(scrollPane, BorderLayout.CENTER);
        transactionHistoryPage.add(transactionHistoryBackButton, BorderLayout.WEST); // Shifted to top left

        cardPanel.add(myBalancePage, "myBalancePage");
        cardPanel.add(withdrawalPage, "withdrawalPage");
        cardPanel.add(depositPage, "depositPage");
        cardPanel.add(emiCalculatorPage, "emiCalculatorPage");
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(transactionHistoryPage, "transactionHistoryPage");

        // Add My Stocks page
        JPanel myStocksPage = new JPanel(new BorderLayout());
        JLabel myStocksLabel = new JLabel("My Stocks");
        JButton checkPricesButton = new JButton("Check Prices");
        JButton viewDetailsButton = new JButton("View Details");
        JButton backButton = new JButton("Back");
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(checkPricesButton);
        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(backButton);
        myStocksPage.add(myStocksLabel, BorderLayout.NORTH);
        myStocksPage.add(buttonsPanel, BorderLayout.SOUTH);
        JPanel pricesPanel = new JPanel(new BorderLayout());
        JTextArea stockPriceArea = new JTextArea(10, 20);
        pricesPanel.add(new JScrollPane(stockPriceArea), BorderLayout.CENTER);
        JPanel listPanel = new JPanel(new BorderLayout());
        JList<String> stockList = new JList<>(new String[]{"AAPL", "GOOGL", "MSFT", "AMZN", "META", "JPM", "TSLA"});
        listPanel.add(new JScrollPane(stockList), BorderLayout.CENTER);
        myStocksPage.add(pricesPanel, BorderLayout.WEST);
        myStocksPage.add(listPanel, BorderLayout.CENTER);

        checkPricesButton.addActionListener(e -> fetchStockPrices(stockList.getSelectedValuesList(), stockPriceArea));

        viewDetailsButton.addActionListener(e -> {
            String selectedStock = stockList.getSelectedValue();
            if (selectedStock != null) {
                openTradingView(selectedStock);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a stock from the list.");
            }
        });

        backButton.addActionListener(e -> {
            pageStack.pop();
            cardLayout.show(cardPanel, "dashboard");
        });

        cardPanel.add(myStocksPage, "myStocksPage");

        // Add Menu Panel
        menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(menuPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Add account type selection JComboBox to deposit and withdrawal pages
        JPanel accountTypePanel = new JPanel(new FlowLayout());
        accountTypeComboBox = new JComboBox<>(new String[]{"Savings", "Current", "Demat"});
        accountTypePanel.add(new JLabel("Account Type:"));
        accountTypePanel.add(accountTypeComboBox);

        // Add account type selection JComboBox to dashboard
        JPanel dashboardTopPanel = new JPanel(new FlowLayout());
        dashboardTopPanel.add(new JLabel("Account Type:"));
        dashboardTopPanel.add(accountTypeComboBox);

        withdrawalPage.add(accountTypePanel, BorderLayout.NORTH);
        depositPage.add(accountTypePanel, BorderLayout.NORTH);
        dashboardPanel.add(dashboardTopPanel, 0); // Add at the top

        // Apply styling changes
        applyStyles();

        // Action Listeners
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.equals("admin") && password.equals("admin")) {
                pageStack.push("login");
                cardLayout.show(cardPanel, "dashboard");
            } else {
                JOptionPane.showMessageDialog(FinanceManagementSystem.this, "Invalid username or password");
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    // Method to handle navigation back to the previous page
    private void navigateBack() {
        if (!pageStack.isEmpty()) {
            String previousPage = pageStack.pop();
            cardLayout.show(cardPanel, previousPage);
        }
    }

    private void initializeAccounts() {
        // Initialize accounts
        accounts = new HashMap<>();
        accounts.put("Savings", new Account("Savings"));
        accounts.put("Current", new Account("Current"));
        accounts.put("Demat", new Account("Demat"));
    }

    private void fetchStockPrices(java.util.List<String> selectedStocks, JTextArea stockPriceArea) {
        // Dummy stock prices
        // You can implement real-time price fetching here
        // For now, let's update with random prices
        stockPriceArea.setText(""); // Clear previous prices
        Random random = new Random();
        for (String stock : selectedStocks) {
            double price = 100 + random.nextDouble() * 50; // Random price between 100 and 150
            stockPriceArea.append(stock + ": $" + String.format("%.2f", price) + "\n");
        }
    }

    private void openTradingView(String stockSymbol) {
        String tradingViewUrl = "https://www.tradingview.com/symbols/" + stockSymbol; // Construct TradingView URL
        try {
            // Open TradingView website in default browser
            Desktop.getDesktop().browse(new URI(tradingViewUrl));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private void recordTransaction(String type, String accountType, double amount) {
        String transaction = type + ": $" + amount + " - " + accountType;
        transactionHistoryArea.append(transaction + "\n");
    }

    private void applyStyles() {
        // Set background color to black and text color to white for all components
        setComponentColors(mainPanel, Color.BLACK, Color.WHITE);
        setComponentColors(loginPanel, new Color(0, 51, 102), Color.WHITE); // Set regal blue background for login panel
        setComponentColors(cardPanel, Color.BLACK, Color.WHITE);
        setComponentColors(menuPanel, Color.BLACK, Color.WHITE);

        setComponentFont(mainPanel, 16f);
    }

    private void setComponentFont(Component component, float fontSize) {
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                setComponentFont(child, fontSize);
            }
        }
        Font currentFont = component.getFont();
        if (currentFont != null) {
            component.setFont(currentFont.deriveFont(fontSize));
        }
    }
    // Helper method to set background and foreground color for components recursively
    private void setComponentColors(Component component, Color backgroundColor, Color foregroundColor) {
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                setComponentColors(child, backgroundColor, foregroundColor);
            }
        }
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);
    }

    public static void main(String[] args) {
        new FinanceManagementSystem();
    }
}

class Account {
    private String type;
    private double balance;

    public Account(String type) {
        this.type = type;
        this.balance = 10000.0; // Initial balance
    }

    public String getType() {
        return type;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }
}