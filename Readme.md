# Bank Management System

A console-based banking application written in Java that demonstrates
core Object-Oriented Programming principles, layered architecture,
file persistence, and automated testing with JUnit 5.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [OOP Principles Demonstrated](#oop-principles-demonstrated)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Testing](#testing)
- [Data Persistence](#data-persistence)
- [What I Learned](#what-i-learned)
- [Future Improvements](#future-improvements)
- [Tech Stack](#tech-stack)
- [License](#license)

---

## Overview

This project is a fully functional banking application that runs in the
terminal. Users can open savings or current accounts, deposit, withdraw,
transfer funds between accounts, earn interest on savings, and inspect
their full transaction history. All data is persisted to disk so it
survives between runs.

The goal of the project was to practice clean object-oriented design in
Java and to build a codebase that is testable, maintainable, and easy to
extend. It is intentionally console-based so the focus stays on the
domain model rather than the UI.

---

## Features

- Create **Savings** or **Current** accounts
- Deposit and withdraw funds with input validation
- Transfer money between accounts
- View account details, list all accounts, and view the total deposits
  held by the bank
- Apply interest to all savings accounts (4% annual, configurable)
- Full **transaction history** per account
- **Persistent storage** in a plain text file that is human-readable and
  diff-friendly
- **36 automated tests** covering models, services, and file persistence

---

## OOP Principles Demonstrated

| Principle         | Where it appears                                                                 |
|-------------------|----------------------------------------------------------------------------------|
| **Encapsulation** | Fields in `Account` are `private`; balance can only change via public methods.  |
| **Inheritance**   | `SavingsAccount` and `CurrentAccount` extend the abstract `Account` class.       |
| **Polymorphism**  | `withdraw()` and `calculateInterest()` behave differently per subclass.          |
| **Abstraction**   | `Account` is abstract and defines a contract subclasses must fulfill.            |
| **Composition**   | `Bank` *has-a* `Map<String, Account>`; `Account` *has-many* `Transaction`s.      |

See:

- `src/main/java/model/Account.java`
- `src/main/java/model/SavingsAccount.java`
- `src/main/java/model/CurrentAccount.java`
- `src/main/java/service/Bank.java`

---

## Architecture

The project is divided into four layers, each with a single
responsibility:

```
+-------------------+
|       main        |   Console UI - handles user input and display
+-------------------+
          |
+-------------------+
|      service      |   Business logic - orchestrates accounts & transfers
+-------------------+
          |
+-------------------+
|       model       |   Domain entities - Account, SavingsAccount, etc.
+-------------------+
          |
+-------------------+
|       util        |   Cross-cutting helpers - input validation, file I/O
+-------------------+
```

This separation means the `Bank` service can be reused unchanged behind a
Swing GUI, a REST controller, or a test harness.

---

## Project Structure

```
BankManagementSystem/
├── pom.xml
├── .gitignore
├── README.md
├── data/
│   └── accounts.txt              (auto-created at runtime)
└── src/
    ├── main/
    │   └── java/
    │       ├── model/
    │       │   ├── Account.java
    │       │   ├── SavingsAccount.java
    │       │   ├── CurrentAccount.java
    │       │   └── Transaction.java
    │       ├── service/
    │       │   └── Bank.java
    │       ├── util/
    │       │   ├── InputValidator.java
    │       │   └── FileManager.java
    │       └── main/
    │           └── Main.java
    └── test/
        └── java/
            ├── model/
            │   ├── TransactionTest.java
            │   ├── SavingsAccountTest.java
            │   └── CurrentAccountTest.java
            └── service/
                └── BankTest.java
```

---

## Getting Started

### Prerequisites

- **JDK 17 or newer** (JDK 21 tested)
- **Apache Maven 3.8 or newer**

Verify your installation:

```bash
java -version
mvn -version
```

### Build and Run

Clone the repository and run:

```bash
mvn clean package
java -jar target/bank-management-system-1.0.0.jar
```

The first `mvn` invocation downloads JUnit and the Maven plugins. Subsequent
builds are fast.

---

## Usage

On launch you are shown a menu:

```
----------- MAIN MENU -----------
1. Create Account
2. Deposit
3. Withdraw
4. Transfer Funds
5. View Account Details
6. List All Accounts
7. View Total Bank Balance
8. View Transaction History
9. Apply Interest to All Accounts
0. Exit & Save
---------------------------------
```

A typical session:

1. Choose `1` and open a **savings** account for `Alice` with `$1000`.
2. Choose `1` again and open a **current** account for `Bob` with `$500`.
3. Choose `2` and deposit `$300` into Alice's account.
4. Choose `4` and transfer `$200` from Alice to Bob.
5. Choose `8` and enter Alice's account number to view her history.
6. Choose `0` to save and exit.

Sample transaction history:

```
--- TRANSACTION HISTORY for ACC1001 ---
[2026-10-16 09:15:02] DEPOSIT       +$1,000.00  Initial deposit
[2026-10-16 09:15:41] DEPOSIT       +$300.00    Deposit
[2026-10-16 09:16:10] WITHDRAWAL    -$200.00    Transfer to ACC1002
[2026-10-16 09:16:32] INTEREST      +$44.00     Interest credited
--------------------------------------------------
Current Balance: $1,144.00
```

---

## Testing

The project ships with 36 unit tests written in JUnit 5.

```bash
mvn test
```

Expected output:

```
[INFO] Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### What the tests cover

| Test class              | Focus                                                                  |
|-------------------------|------------------------------------------------------------------------|
| `TransactionTest`       | Model integrity, serialization round-trips, edge cases in descriptions |
| `SavingsAccountTest`    | Minimum-balance enforcement, interest calculation, transaction logging |
| `CurrentAccountTest`    | Overdraft behavior at boundary conditions (in-limit, over-limit)       |
| `BankTest`              | Account lifecycle, transfers, aggregation, and file persistence        |

Persistence tests use a **temporary directory** (`@BeforeEach` creates one
and `@AfterEach` deletes it), so running the tests never touches your real
`data/accounts.txt`.

---

## Data Persistence

Data is stored in a plain, pipe-delimited text file at `data/accounts.txt`.

```
ACCOUNT|SAVINGS|ACC1001|Alice|1144.00
TXN|ACC1001|DEPOSIT|1000.00|2026-10-16T09:15:02.123|Initial deposit
TXN|ACC1001|DEPOSIT|300.00|2026-10-16T09:15:41.456|Deposit
TXN|ACC1001|WITHDRAWAL|200.00|2026-10-16T09:16:10.789|Transfer to ACC1002
TXN|ACC1001|INTEREST|44.00|2026-10-16T09:16:32.012|Interest credited
ACCOUNT|CURRENT|ACC1002|Bob|700.00
TXN|ACC1002|DEPOSIT|500.00|2026-10-16T09:13:00.000|Initial deposit
TXN|ACC1002|DEPOSIT|200.00|2026-10-16T09:16:10.789|Transfer from ACC1001
```

The file is loaded on startup and rewritten on exit. Because it is
line-oriented, it is safe to open in any text editor and even to edit by
hand for experimentation.

**Locale note:** all numeric values are written with `Locale.US` so the
decimal separator is always a dot, regardless of the operating system's
default locale. On load, any stray commas are normalized to dots for
backward compatibility. This was added after encountering a real
`NumberFormatException` on a `en_ZA` locale system (see below).

---

## What I Learned

- **Abstraction pays off.** Moving `withdraw()` into an overridable method
  let `CurrentAccount` implement overdrafts cleanly while
  `SavingsAccount` enforced a minimum balance, without duplicating a
  single line of code.
- **Testing forces better design.** Once `FileManager` accepted a
  configurable file path, the persistence tests became trivial. This
  refactor would not have happened without tests pushing for it.
- **Never trust the default locale for data files.** `String.format("%.2f", x)`
  produced `1000,00` on a South African machine and crashed
  `Double.parseDouble` on reload. The fix — always pass `Locale.US` when
  formatting machine-readable values — is a subtle, real-world lesson.
- **Layered architecture makes extension cheap.** Because the console UI
  is thin and the service layer holds all the logic, adding a Swing GUI
  or a REST API later requires no changes to the domain model.

---

## Future Improvements

- Replace generic `IllegalArgumentException`s with domain-specific
  exceptions (`InsufficientFundsException`, `AccountNotFoundException`, ...)
- Introduce an `AccountType` enum to replace raw strings
- Add PIN-based authentication with hashed storage
- Support CSV and JSON export of statements
- Migrate to a Spring Boot REST API with the same service layer
- Add a Swing or JavaFX GUI

---

## Tech Stack

- **Language:** Java 17+
- **Build tool:** Apache Maven
- **Testing:** JUnit 5 (Jupiter)
- **Persistence:** Plain text file
- **IDE tested with:** Visual Studio Code (Extension Pack for Java)

---

## Author

**<Your Name>**

- GitHub: @lilitha-gif1406(https://github.com/lilitha-gif1406)
- Email: dlakulilitha744@gmail.com

---

## License

This project is released under the MIT License. See `LICENSE` for details.