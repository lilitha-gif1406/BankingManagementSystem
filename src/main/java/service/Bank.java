package service;

import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import util.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accounts;
    private int accountCounter = 1000;

    public Bank() {
        this.accounts = FileManager.loadBank();

        for (String accNo : accounts.keySet()) {
            try {
                int num = Integer.parseInt(accNo.replace("ACC", ""));
                if (num > accountCounter) accountCounter = num;
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public Account createAccount(String name, String type, double initialBalance) {
        String accountNumber = "ACC" + (++accountCounter);
        Account account;
        switch (type.toLowerCase()) {
            case "savings":
                account = new SavingsAccount(accountNumber, name, initialBalance);
                break;
            case "current":
                account = new CurrentAccount(accountNumber, name, initialBalance);
                break;
            default:
                throw new IllegalArgumentException("Invalid account type: " + type);
        }
        accounts.put(accountNumber, account);
        return account;
    }

    public Account findAccount(String accountNumber) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return acc;
    }

    public void deposit(String accountNumber, double amount) {
        findAccount(accountNumber).deposit(amount);
    }

    public void withdraw(String accountNumber, double amount) {
        findAccount(accountNumber).withdraw(amount);
    }

    public void transfer(String fromAcc, String toAcc, double amount) {
        Account source = findAccount(fromAcc);
        Account target = findAccount(toAcc);

        source.withdraw(amount, "Transfer to " + toAcc);
        target.deposit(amount, "Transfer from " + fromAcc);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public double getTotalBankBalance() {
        return accounts.values().stream().mapToDouble(Account::getBalance).sum();
    }

    public void applyInterestToAll() {
        accounts.values().forEach(Account::creditInterest);
    }

    public void save() {
        FileManager.saveBank(accounts.values());
    }
}