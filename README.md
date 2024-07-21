Sure! Here's the updated README with additional sections for contributions, resources used, and more.

```markdown
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

## Contributions

Contributions to this project are welcome. You can contribute in the following ways:

1. **Reporting Bugs**: If you find any bugs, please create an issue on the [GitHub repository](https://github.com/pushkqr/personal-finance/issues).
2. **Feature Requests**: If you have ideas for new features, please share them by creating an issue.
3. **Code Contributions**: Fork the repository, make your changes, and submit a pull request.

## Resources Used

- **Java**: The primary programming language for developing the application.
- **Swing**: For building the graphical user interface.
- **SQLite**: For local database storage.
- **jBCrypt**: For secure password hashing.
- **Maven**: For project build and dependency management.
- **SiLog**: For logging and error handling.

## Acknowledgements

- Thanks to the creators of the libraries and tools used in this project.
