package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void constructor_setsAllFields() {
        Transaction t = new Transaction(Transaction.Type.DEPOSIT, 500.0, "Test deposit");
        assertEquals(Transaction.Type.DEPOSIT, t.getType());
        assertEquals(500.0, t.getAmount(), 0.001);
        assertEquals("Test deposit", t.getDescription());
        assertNotNull(t.getTimestamp());
    }

    @Test
    void fileString_roundTripPreservesData() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 15, 10, 30, 0);
        Transaction original = new Transaction(
                Transaction.Type.WITHDRAWAL, 250.75, fixedTime, "Test withdrawal");

        String line = original.toFileString();
        Transaction restored = Transaction.fromFileString(line);

        assertEquals(original.getType(), restored.getType());
        assertEquals(original.getAmount(), restored.getAmount(), 0.001);
        assertEquals(original.getTimestamp(), restored.getTimestamp());
        assertEquals(original.getDescription(), restored.getDescription());
    }

    @Test
    void fileString_handlesPipeInDescription() {
        Transaction t = new Transaction(
                Transaction.Type.DEPOSIT, 100.0, "Note | with | pipes");
        Transaction restored = Transaction.fromFileString(t.toFileString());
        assertEquals("Note | with | pipes", restored.getDescription());
    }

    @Test
    void toString_includesSignForDepositAndWithdrawal() {
        Transaction deposit = new Transaction(Transaction.Type.DEPOSIT, 100.0, "d");
        Transaction withdrawal = new Transaction(Transaction.Type.WITHDRAWAL, 50.0, "w");
        assertTrue(deposit.toString().contains("+"));
        assertTrue(withdrawal.toString().contains("-"));
    }
}