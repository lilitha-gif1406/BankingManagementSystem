package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrentAccountTest {

    @Test
    void withdraw_withinBalance_succeeds() {
        CurrentAccount acc = new CurrentAccount("ACC1", "Bob", 500.0);
        acc.withdraw(300.0);
        assertEquals(200.0, acc.getBalance(), 0.001);
    }

    @Test
    void withdraw_intoOverdraft_succeeds() {
        CurrentAccount acc = new CurrentAccount("ACC1", "Bob", 500.0);
        acc.withdraw(1200.0);
        assertEquals(-700.0, acc.getBalance(), 0.001);
    }

    @Test
    void withdraw_exceedingOverdraft_throws() {
        CurrentAccount acc = new CurrentAccount("ACC1", "Bob", 500.0);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> acc.withdraw(1600.0));
        assertTrue(ex.getMessage().contains("overdraft"));
    }

    @Test
    void withdraw_atExactOverdraftLimit_succeeds() {
        CurrentAccount acc = new CurrentAccount("ACC1", "Bob", 500.0);
        acc.withdraw(1500.0);
        assertEquals(-1000.0, acc.getBalance(), 0.001);
    }

    @Test
    void withdraw_negativeAmount_throws() {
        CurrentAccount acc = new CurrentAccount("ACC1", "Bob", 500.0);
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(-50.0));
    }

    @Test
    void calculateInterest_returnsZero() {
        CurrentAccount acc = new CurrentAccount("ACC1", "Bob", 500.0);
        assertEquals(0.0, acc.calculateInterest(), 0.001);
    }
}