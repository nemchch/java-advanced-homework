package ru.ifmo.rain.nemchunovich.bank;

import java.math.BigDecimal;

public class AccountImpl implements Account {
    private final String id;
    private BigDecimal amount;

    public AccountImpl(String id) {
        this.id = id;
        this.amount = new BigDecimal(0);
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public synchronized void incAmount(BigDecimal amount) {
        System.out.println("Increasing amount of money for account " + id);
        this.amount = this.amount.add(amount);
    }
}
