package com.n26.challenge.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AsyncTransactionUpdaterStatisticsTest extends SchedulableSingleWorkerBaseTest {
    AsyncTransactionUpdaterStatistics asyncTransactionUpdaterStatisticsSamples;
    @Mock
    private SlidingStatisticsInterface delegate;

    private Statistics statistics = new Statistics(12, 6, 6, 6, 2);

    @Before
    public void setup()
    {
        asyncTransactionUpdaterStatisticsSamples =
                new AsyncTransactionUpdaterStatistics(
                        delegate,
                        scheduledExecutorService,
                        45,
                        TimeUnit.MILLISECONDS,
                        50
                );
    }

    @Test
    public void getStatistics()
    {

        when(delegate.getStatistics()).thenReturn(statistics);

        Statistics statisticsToTest = asyncTransactionUpdaterStatisticsSamples.getStatistics();

        assertThat(statisticsToTest, is(equalTo(statistics)));

        verify(delegate).getStatistics();
    }

    @Test
    public void slide()
    {
        asyncTransactionUpdaterStatisticsSamples.slide();

        verify(delegate).slide();
    }

    @Test
    public void resetStatistics()
    {
        asyncTransactionUpdaterStatisticsSamples.resetStatistics();

        verify(delegate).resetStatistics();
    }


    @Test
    public void addTransaction()
    {
        givenARealUpdater();

        Transaction first = Transaction.forAmount(12);
        Transaction second = Transaction.forAmount(12);
        Transaction third = Transaction.forAmount(12);

        asyncTransactionUpdaterStatisticsSamples.addTransactions(
                asList(
                        first,
                        second,
                        third
                ));

        sleepFor(60l);

        verify(delegate).addTransaction(first);
        verify(delegate).addTransaction(second);
        verify(delegate).addTransaction(third);

        asyncTransactionUpdaterStatisticsSamples.destroy();
    }

    @Test
    public void addTransactions()
    {
        givenARealUpdater();

        Transaction first = Transaction.forAmount(12);
        asyncTransactionUpdaterStatisticsSamples.addTransaction(first);

        Transaction second = Transaction.forAmount(12);
        asyncTransactionUpdaterStatisticsSamples.addTransaction(second);

        Transaction third = Transaction.forAmount(12);
        asyncTransactionUpdaterStatisticsSamples.addTransaction(third);

        sleepFor(60l);

        verify(delegate).addTransaction(first);
        verify(delegate).addTransaction(second);
        verify(delegate).addTransaction(third);

        asyncTransactionUpdaterStatisticsSamples.destroy();
    }

    private void givenARealUpdater()
    {
        asyncTransactionUpdaterStatisticsSamples =
                new AsyncTransactionUpdaterStatistics(delegate,
                        Executors
                                .newScheduledThreadPool(
                                        1),
                        10,
                        TimeUnit.MILLISECONDS,
                        10);

        asyncTransactionUpdaterStatisticsSamples.init();
    }

    @Override protected SchedulableSingleWorker getInstance()
    {
        return asyncTransactionUpdaterStatisticsSamples;
    }

    @Override protected Class<? extends Runnable> getRunnableClass()
    {
        return AsyncTransactionUpdaterStatistics.TransactionUpdater.class;
    }

    @Override protected long getInitialDelay()
    {
        return 45;
    }

    @Override protected long getPeriod()
    {
        return 45;
    }

    @Override protected TimeUnit getTimeUnit()
    {
        return TimeUnit.MILLISECONDS;
    }
}
