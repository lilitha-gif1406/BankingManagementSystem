package main;

import model.Account;
import model.Transaction;
import service.Bank;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Bank bank = new Bank();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   WELCOME TO JAVA BANK MANAGEMENT");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = (int) InputValidator.readPositiveDouble(sc, "Choose an option: ");

            try {
                switch (choice) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        deposit();
                        break;
                    case 3:
                        withdraw();
                        break;
                    case 4:
                        transfer();
                        break;
                    case 5:
                        viewAccount();
                        break;
                    case 6:
                        listAllAccounts();
                        break;
                    case 7:
                        viewTotalBalance();
                        break;
                    case 8:
                        viewTransactionHistory();
                        break;
                    case 9:
                        applyInterest();
                        break;
                    case 0:
                        running = false;
                        bank.save();
                        System.out.println("\nThank you for using Java Bank. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n----------- MAIN MENU -----------");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer Funds");
        System.out.println("5. View Account Details");
        System.out.println("6. List All Accounts");
        System.out.println("7. View Total Bank Balance");
        System.out.println("8. View Transaction History");
        System.out.println("9. Apply Interest to All Accounts");
        System.out.println("0. Exit & Save");
        System.out.println("---------------------------------");
    }

    private static void createAccount() {
        String name = InputValidator.readNonEmptyString(sc, "Enter account holder name: ");
        String type = InputValidator.readNonEmptyString(sc, "Account type (savings/current): ");
        double initial = InputValidator.readPositiveDouble(sc, "Initial deposit amount: $");

        Account acc = bank.createAccount(name, type, initial);
        System.out.println("Account created successfully!");
        System.out.println("   Account Number: " + acc.getAccountNumber());
    }

    private static void deposit() {
        String accNum = InputValidator.readNonEmptyString(sc, "Enter account number: ");
        double amount = InputValidator.readPositiveDouble(sc, "Enter deposit amount: $");
        bank.deposit(accNum, amount);
        System.out.println("Deposit successful.");
    }

    private static void withdraw() {
        String accNum = InputValidator.readNonEmptyString(sc, "Enter account number: ");
        double amount = InputValidator.readPositiveDouble(sc, "Enter withdrawal amount: $");
        bank.withdraw(accNum, amount);
        System.out.println("Withdrawal successful.");
    }

    private static void transfer() {
        String from = InputValidator.readNonEmptyString(sc, "From account number: ");
        String to = InputValidator.readNonEmptyString(sc, "To account number: ");
        double amount = InputValidator.readPositiveDouble(sc, "Transfer amount: $");
        bank.transfer(from, to, amount);
        System.out.println("Transfer successful.");
    }

    private static void viewAccount() {
        String accNum = InputValidator.readNonEmptyString(sc, "Enter account number: ");
        Account acc = bank.findAccount(accNum);
        System.out.println("\n--- ACCOUNT DETAILS ---");
        System.out.println("Account Number : " + acc.getAccountNumber());
        System.out.println("Holder Name    : " + acc.getAccountHolderName());
        System.out.println("Type           : " + acc.getAccountType());
        System.out.printf("Balance        : $%,.2f%n", acc.getBalance());
        System.out.printf("Interest Earned: $%,.2f%n", acc.calculateInterest());
    }

    private static void listAllAccounts() {
        var accounts = bank.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("\n---------------------------------------------------------------");
        System.out.printf("| %-12s | %-20s | %-10s | %-12s |%n",
                "Acc No", "Holder", "Type", "Balance");
        System.out.println("---------------------------------------------------------------");
        for (Account acc : accounts) {
            System.out.println(acc);
        }
        System.out.println("---------------------------------------------------------------");
    }

    private static void viewTotalBalance() {
        System.out.printf("Total deposits across all accounts: $%,.2f%n",
                bank.getTotalBankBalance());
    }

    private static void viewTransactionHistory() {
        String accNum = InputValidator.readNonEmptyString(sc, "Enter account number: ");
        Account acc = bank.findAccount(accNum);
        List<Transaction> txns = acc.getTransactions();

        System.out.println("\n--- TRANSACTION HISTORY for " + accNum + " ---");
        if (txns.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : txns) {
            System.out.println(t);
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("Current Balance: $%,.2f%n", acc.getBalance());
    }

    private static void applyInterest() {
        bank.applyInterestToAll();
        System.out.println("Interest applied to all eligible accounts.");
    }
}