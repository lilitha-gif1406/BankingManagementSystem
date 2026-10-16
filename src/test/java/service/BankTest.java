package service;

import model.Account;
import model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FileManager;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    private Path tempDir;
    private Bank bank;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("bank-test-");
        FileManager.setDataFile(tempDir.toString(), tempDir.resolve("accounts.txt").toString());
        bank = new Bank();
    }

    @AfterEach
    void tearDown() throws Exception {
        FileManager.resetToDefault();
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> p.toFile().delete());
        }
    }

    // -------- Account Creation --------

    @Test
    void createAccount_savings_succeeds() {
        Account acc = bank.createAccount("Alice", "savings", 1000.0);
        assertNotNull(acc);
        assertEquals("SAVINGS", acc.getAccountType());
        assertEquals(1000.0, acc.getBalance(), 0.001);
        assertTrue(acc.getAccountNumber().startsWith("ACC"));
    }

    @Test
    void createAccount_current_succeeds() {
        Account acc = bank.createAccount("Bob", "current", 500.0);
        assertEquals("CURRENT", acc.getAccountType());
    }

    @Test
    void createAccount_invalidType_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> bank.createAccount("X", "credit", 100.0));
    }

    @Test
    void createAccount_numbersAreUnique() {
        Account a = bank.createAccount("Alice", "savings", 1000.0);
        Account b = bank.createAccount("Bob", "savings", 1000.0);
        assertNotEquals(a.getAccountNumber(), b.getAccountNumber());
    }

    // -------- Lookup --------

    @Test
    void findAccount_existing_returnsAccount() {
        Account created = bank.createAccount("Alice", "savings", 1000.0);
        Account found = bank.findAccount(created.getAccountNumber());
        assertSame(created, found);
    }

    @Test
    void findAccount_missing_throws() {
        assertThrows(IllegalArgumentException.class, () -> bank.findAccount("ACC9999"));
    }

    // -------- Deposit --------

    @Test
    void deposit_increasesBalance() {
        Account acc = bank.createAccount("Alice", "savings", 1000.0);
        bank.deposit(acc.getAccountNumber(), 500.0);
        assertEquals(1500.0, acc.getBalance(), 0.001);
    }

    @Test
    void deposit_negativeAmount_throws() {
        Account acc = bank.createAccount("Alice", "savings", 1000.0);
        assertThrows(IllegalArgumentException.class,
                () -> bank.deposit(acc.getAccountNumber(), -100.0));
    }

    // -------- Withdraw --------

    @Test
    void withdraw_decreasesBalance() {
        Account acc = bank.createAccount("Alice", "savings", 1000.0);
        bank.withdraw(acc.getAccountNumber(), 200.0);
        assertEquals(800.0, acc.getBalance(), 0.001);
    }

    @Test
    void withdraw_insufficientFunds_throws() {
        Account acc = bank.createAccount("Alice", "savings", 1000.0);
        assertThrows(IllegalArgumentException.class,
                () -> bank.withdraw(acc.getAccountNumber(), 800.0));
    }

    // -------- Transfer --------

    @Test
    void transfer_movesMoneyBetweenAccounts() {
        Account from = bank.createAccount("Alice", "savings", 1000.0);
        Account to = bank.createAccount("Bob", "current", 500.0);

        bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 300.0);

        assertEquals(700.0, from.getBalance(), 0.001);
        assertEquals(800.0, to.getBalance(), 0.001);
    }

    @Test
    void transfer_insufficientFunds_throwsAndDoesNotMutate() {
        Account from = bank.createAccount("Alice", "savings", 1000.0);
        Account to = bank.createAccount("Bob", "current", 500.0);

        assertThrows(IllegalArgumentException.class,
                () -> bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 900.0));

        assertEquals(1000.0, from.getBalance(), 0.001);
        assertEquals(500.0, to.getBalance(), 0.001);
    }

    @Test
    void transfer_logsTransactionsOnBothAccounts() {
        Account from = bank.createAccount("Alice", "savings", 1000.0);
        Account to = bank.createAccount("Bob", "current", 500.0);

        bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 300.0);

        Transaction lastFrom = from.getTransactions()
                .get(from.getTransactions().size() - 1);
        Transaction lastTo = to.getTransactions()
                .get(to.getTransactions().size() - 1);

        assertEquals(Transaction.Type.WITHDRAWAL, lastFrom.getType());
        assertEquals(Transaction.Type.DEPOSIT, lastTo.getType());
        assertTrue(lastFrom.getDescription().contains("Transfer to"));
        assertTrue(lastTo.getDescription().contains("Transfer from"));
    }

    // -------- Aggregates --------

    @Test
    void getTotalBankBalance_sumsAllAccounts() {
        bank.createAccount("Alice", "savings", 1000.0);
        bank.createAccount("Bob", "current", 500.0);
        assertEquals(1500.0, bank.getTotalBankBalance(), 0.001);
    }

    @Test
    void getTotalBankBalance_emptyBank_returnsZero() {
        assertEquals(0.0, bank.getTotalBankBalance(), 0.001);
    }

    // -------- Interest --------

    @Test
    void applyInterest_creditsSavingsNotCurrent() {
        Account savings = bank.createAccount("Alice", "savings", 1000.0);
        Account current = bank.createAccount("Bob", "current", 1000.0);

        bank.applyInterestToAll();

        assertEquals(1040.0, savings.getBalance(), 0.001);
        assertEquals(1000.0, current.getBalance(), 0.001);
    }

    // -------- Persistence --------

    @Test
    void saveAndLoad_restoresAccountsAndTransactions() {
        Account acc = bank.createAccount("Alice", "savings", 1000.0);
        bank.deposit(acc.getAccountNumber(), 500.0);
        bank.withdraw(acc.getAccountNumber(), 200.0);
        bank.save();

        Bank reloaded = new Bank();
        Account restored = reloaded.findAccount(acc.getAccountNumber());

        assertEquals("Alice", restored.getAccountHolderName());
        assertEquals("SAVINGS", restored.getAccountType());
        assertEquals(1300.0, restored.getBalance(), 0.001);
        assertEquals(3, restored.getTransactions().size());
    }

    @Test
    void saveAndLoad_counterDoesNotCollideOnReload() {
        Account first = bank.createAccount("Alice", "savings", 1000.0);
        bank.save();

        Bank reloaded = new Bank();
        Account second = reloaded.createAccount("Bob", "savings", 1000.0);

        assertNotEquals(first.getAccountNumber(), second.getAccountNumber());
    }

    @Test
    void load_whenFileMissing_returnsEmptyBank() {
        Bank fresh = new Bank();
        assertTrue(fresh.getAllAccounts().isEmpty());
    }
}