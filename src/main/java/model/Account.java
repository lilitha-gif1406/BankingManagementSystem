package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    private final String accountNumber;
    private final String accountHolderName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String accountHolderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        if (initialBalance > 0) {
            transactions.add(new Transaction(
                    Transaction.Type.DEPOSIT,
                    initialBalance,
                    "Initial deposit"));
        }
    }

    public abstract String getAccountType();
    public abstract double calculateInterest();

    public void deposit(double amount) {
        deposit(amount, "Deposit");
    }

    public void deposit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.balance += amount;
        transactions.add(new Transaction(
                Transaction.Type.DEPOSIT, amount, description));
    }

    public void withdraw(double amount) {
        withdraw(amount, "Withdrawal");
    }

    public void withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        this.balance -= amount;
        transactions.add(new Transaction(
                Transaction.Type.WITHDRAWAL, amount, description));
    }

    protected void applyWithdrawal(double amount, String description,
                                   Transaction.Type type) {
        this.balance -= amount;
        transactions.add(new Transaction(type, amount, description));
    }

    public void creditInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            this.balance += interest;
            transactions.add(new Transaction(
                    Transaction.Type.INTEREST, interest, "Interest credited"));
        }
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void restoreTransaction(Transaction t) {
        transactions.add(t);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }

    protected void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("| %-12s | %-20s | %-10s | $%,.2f |",
                accountNumber, accountHolderName, getAccountType(), balance);
    }
}