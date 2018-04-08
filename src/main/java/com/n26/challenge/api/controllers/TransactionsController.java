package com.n26.challenge.api.controllers;

import com.n26.challenge.api.model.Event;
import com.n26.challenge.services.ExpiredTransactionException;
import com.n26.challenge.services.SlidingStatisticsInterface;
import com.n26.challenge.services.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionsController
{

    Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    private final SlidingStatisticsInterface slidingStatisticsInterface;

    @Autowired
    public TransactionsController(@Qualifier("slidingStatisticsInterface") SlidingStatisticsInterface slidingStatisticsInterface)
    {
        this.slidingStatisticsInterface = slidingStatisticsInterface;
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void transactions(@RequestBody Event event)
    {

        logger.info("Received event {}", event);

        slidingStatisticsInterface.addTransaction(
                Transaction.of(
                        event.getAmount(),
                        event.getTimestamp()
                )
        );
    }

    @ExceptionHandler(value = ExpiredTransactionException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void expiredTransaction(ExpiredTransactionException exception)
    {
        logger.error(exception.getMessage());
    }
}

