package util;

import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class FileManager {
    private static final String DEFAULT_DIR = "data";
    private static final String DEFAULT_FILE = DEFAULT_DIR + "/accounts.txt";

    private static String dataDir = DEFAULT_DIR;
    private static String dataFile = DEFAULT_FILE;

    public static void setDataFile(String dir, String file) {
        dataDir = dir;
        dataFile = file;
    }

    public static void resetToDefault() {
        dataDir = DEFAULT_DIR;
        dataFile = DEFAULT_FILE;
    }

    public static void saveBank(Collection<Account> accounts) {
        try {
            Files.createDirectories(Paths.get(dataDir));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
                for (Account acc : accounts) {
                    writer.write(String.format(Locale.US, "ACCOUNT|%s|%s|%s|%.2f%n",
                            acc.getAccountType(),
                            acc.getAccountNumber(),
                            acc.getAccountHolderName(),
                            acc.getBalance()));

                    for (Transaction t : acc.getTransactions()) {
                        writer.write("TXN|" + acc.getAccountNumber() + "|"
                                + t.toFileString() + System.lineSeparator());
                    }
                }
            }
            System.out.println("Data saved to " + dataFile);
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    public static Map<String, Account> loadBank() {
        Map<String, Account> accounts = new LinkedHashMap<>();
        File file = new File(dataFile);
        if (!file.exists()) return accounts;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 6);
                if (parts[0].equals("ACCOUNT")) {
                    String type = parts[1];
                    String accNo = parts[2];
                    String holder = parts[3];
                    double balance = Double.parseDouble(parts[4].replace(',', '.'));

                    Account acc;
                    switch (type) {
                        case "SAVINGS":
                            acc = new SavingsAccount(accNo, holder, balance);
                            break;
                        case "CURRENT":
                            acc = new CurrentAccount(accNo, holder, balance);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown type: " + type);
                    }
                    acc.clearTransactions();
                    accounts.put(accNo, acc);
                } else if (parts[0].equals("TXN")) {
                    String accNo = parts[1];
                    Account acc = accounts.get(accNo);
                    if (acc != null) {
                        String txnLine = parts[2] + "|" + parts[3] + "|"
                                + parts[4] + "|" + parts[5];
                        acc.restoreTransaction(Transaction.fromFileString(txnLine));
                    }
                }
            }
            System.out.println("Loaded " + accounts.size() + " account(s) from " + dataFile);
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
        return accounts;
    }
}