package com.n26.challenge.services;

public class ExpiredTransactionException extends RuntimeException {
    public ExpiredTransactionException(UnixEpoch unixEpoch)
    {
        super("Transaction timestamp ["+unixEpoch+"] is expired.");
    }
}
