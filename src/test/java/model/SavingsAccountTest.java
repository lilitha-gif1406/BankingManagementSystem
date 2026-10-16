package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void constructor_rejectsBalanceBelowMinimum() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new SavingsAccount("ACC1", "Alice", 100.0));
        assertTrue(ex.getMessage().contains("minimum"));
    }

    @Test
    void constructor_acceptsBalanceAtMinimum() {
        SavingsAccount acc = new SavingsAccount("ACC1", "Alice", 500.0);
        assertEquals(500.0, acc.getBalance(), 0.001);
    }

    @Test
    void calculateInterest_returns4Percent() {
        SavingsAccount acc = new SavingsAccount("ACC1", "Alice", 1000.0);
        assertEquals(40.0, acc.calculateInterest(), 0.001);
    }

    @Test
    void withdraw_belowMinimumBalance_throws() {
        SavingsAccount acc = new SavingsAccount("ACC1", "Alice", 1000.0);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> acc.withdraw(600.0));
        assertTrue(ex.getMessage().contains("minimum balance"));
    }

    @Test
    void withdraw_leavingExactlyMinimum_succeeds() {
        SavingsAccount acc = new SavingsAccount("ACC1", "Alice", 1000.0);
        acc.withdraw(500.0);
        assertEquals(500.0, acc.getBalance(), 0.001);
    }

    @Test
    void withdraw_logsTransaction() {
        SavingsAccount acc = new SavingsAccount("ACC1", "Alice", 1000.0);
        int before = acc.getTransactions().size();
        acc.withdraw(200.0);
        assertEquals(before + 1, acc.getTransactions().size());
        assertEquals(Transaction.Type.WITHDRAWAL,
                acc.getTransactions().get(acc.getTransactions().size() - 1).getType());
    }

    @Test
    void getAccountType_returnsSAVINGS() {
        SavingsAccount acc = new SavingsAccount("ACC1", "Alice", 500.0);
        assertEquals("SAVINGS", acc.getAccountType());
    }
}