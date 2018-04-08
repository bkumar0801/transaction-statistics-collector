package com.n26.challenge.services;

import java.util.Collection;

public interface SlidingStatisticsInterface {
    void addTransaction(Transaction transaction);

    void addTransactions(Collection<Transaction> transactions);

    Statistics getStatistics();

    void slide();

    void resetStatistics();
}
