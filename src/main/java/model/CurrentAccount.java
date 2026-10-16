package model;

public class CurrentAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 1000.0;

    public CurrentAccount(String accountNumber, String accountHolderName, double initialBalance) {
        super(accountNumber, accountHolderName, initialBalance);
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }

    @Override
    public double calculateInterest() {
        return 0.0;
    }

    @Override
    public void withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > getBalance() + OVERDRAFT_LIMIT) {
            throw new IllegalArgumentException(
                    "Exceeds overdraft limit of $" + OVERDRAFT_LIMIT);
        }
        applyWithdrawal(amount, description, Transaction.Type.WITHDRAWAL);
    }
}