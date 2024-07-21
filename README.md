# Personal Finance

This Java application helps users manage their personal finances through a graphical user interface (GUI). It allows users to add transactions, view account overviews, update passwords and usernames, export transaction data, and delete accounts.

## Features

- **Login and Registration**: Secure authentication using bcrypt for password hashing.
- **Dashboard**: Overview of account balance, recent transactions, and navigation to add transactions, view transactions, and settings.
- **Add Transaction**: Form to input transaction details (amount, category, type, date) and store them securely.
- **View Transactions**: Table view of all transactions with sortable columns (ID, amount, category, type, date).
- **Settings**: Update password, update username, export transaction data to a file, and delete account.
- **Database**: Uses SQLite for local storage of user data and transactions.
- **Logging**: Integrated logging with SiLog for error handling and debugging.

## Prerequisites

- Java Development Kit (JDK) version 22 or higher
- Apache Maven (for building and packaging)

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/pushkqr/personal-finance.git
   
   cd src\main\java
   ```

## Running the application

**Using executable JAR:**

You can run the already compiled executable JAR file:

```bash
java -jar finance-app.jar
```

