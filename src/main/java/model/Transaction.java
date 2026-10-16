package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(Type type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    public Transaction(Type type, double amount, LocalDateTime timestamp, String description) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        String sign;
        switch (type) {
            case DEPOSIT:
            case TRANSFER_IN:
            case INTEREST:
                sign = "+";
                break;
            case WITHDRAWAL:
            case TRANSFER_OUT:
                sign = "-";
                break;
            default:
                sign = " ";
        }
        return String.format(Locale.US, "[%s] %-13s %s$%,.2f  %s",
                timestamp.format(FORMATTER),
                type,
                sign,
                amount,
                description);
    }

    public String toFileString() {
        // Use Locale.US so the amount is always serialized with a dot
        String amountStr = String.format(Locale.US, "%.2f", amount);
        return type + "|" + amountStr + "|" + timestamp + "|" + description;
    }

    public static Transaction fromFileString(String line) {
        String[] parts = line.split("\\|", 4);
        return new Transaction(
                Type.valueOf(parts[0]),
                Double.parseDouble(parts[1].replace(',', '.')),
                LocalDateTime.parse(parts[2]),
                parts[3]
        );
    }
}