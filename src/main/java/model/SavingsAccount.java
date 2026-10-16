package model;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.04;
    private static final double MIN_BALANCE = 500.0;

    public SavingsAccount(String accountNumber, String accountHolderName, double initialBalance) {
        super(accountNumber, accountHolderName, initialBalance);
        if (initialBalance < MIN_BALANCE) {
            throw new IllegalArgumentException("Savings account requires minimum $" + MIN_BALANCE);
        }
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE;
    }

    @Override
    public void withdraw(double amount, String description) {
        if (getBalance() - amount < MIN_BALANCE) {
            throw new IllegalArgumentException(
                    "Savings account must maintain minimum balance of $" + MIN_BALANCE);
        }
        super.withdraw(amount, description);
    }
}