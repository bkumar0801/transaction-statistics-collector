package com.n26.challenge.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoSlidingStatistics extends SchedulableSingleWorker implements SlidingStatisticsInterface
{
    private final static Logger logger = LoggerFactory.getLogger(AutoSlidingStatistics.class);

    private final SlidingStatisticsInterface delegate;

    public AutoSlidingStatistics(SlidingStatisticsInterface delegate,
                                 ScheduledExecutorService scheduledExecutorService,
                                 long interval,
                                 TimeUnit period)
    {
        super(scheduledExecutorService,
                interval,
                period);

        this.delegate = delegate;
    }

    @Override public void addTransaction(Transaction transaction)
    {
        delegate.addTransaction(transaction);
    }

    @Override public void addTransactions(Collection<Transaction> transactions)
    {
        delegate.addTransactions(transactions);
    }

    @Override public Statistics getStatistics()
    {
        return delegate.getStatistics();
    }

    @Override public void slide()
    {
        throw new UnsupportedOperationException("I am supposed to slide samples on my own :)");
    }

    @Override public void resetStatistics()
    {
        delegate.resetStatistics();
    }

    @Override protected Runnable getWorker()
    {
        return new Slider();
    }

    class Slider implements Runnable
    {
        @Override public void run()
        {
            try
            {
                delegate.slide();
            }
            catch (Exception ex)
            {
                logger.error("Error sliding samples.", ex);
            }
        }
    }

}
