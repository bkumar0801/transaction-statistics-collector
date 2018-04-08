package com.n26.challenge.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AutoSlidingStatisticsTest extends SchedulableSingleWorkerBaseTest {
    @Mock
    private SlidingStatisticsInterface delegate;


    AutoSlidingStatistics autoSlidingStatistics;

    Transaction transaction = new Transaction(1d, null);
    Collection<Transaction> transactions = Collections.singleton(transaction);
    private Statistics statistics = new Statistics(12,6,6,6,2);


    @Before
    public void setup() {
        autoSlidingStatistics = new AutoSlidingStatistics(delegate,
                scheduledExecutorService,
                3,
                TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void slide() {
        autoSlidingStatistics.slide();
    }

    @Test
    public void shouldAddTransaction() {
        autoSlidingStatistics.addTransaction(transaction);

        verify(delegate).addTransaction(transaction);
    }

    @Test
    public void shouldAddTransactions() {
        autoSlidingStatistics.addTransactions(transactions);

        verify(delegate).addTransactions(transactions);
    }

    @Test
    public void shouldResetStatistics()
    {
        autoSlidingStatistics.resetStatistics();

        verify(delegate).resetStatistics();
    }


    @Test
    public void shouldGetStatistics() {

        when(delegate.getStatistics()).thenReturn(statistics);

        Statistics statisticsToTest = autoSlidingStatistics.getStatistics();

        assertThat(statisticsToTest,is(equalTo(statistics)));

        verify(delegate).getStatistics();
    }


    @Test
    public void shouldTestHappyPath() {
        autoSlidingStatistics = new AutoSlidingStatistics(delegate,
                Executors.newScheduledThreadPool(1),
                10,
                TimeUnit.MILLISECONDS);

        autoSlidingStatistics.init();

        sleepFor(60l);

        verify(delegate,atLeast(5)).slide();

        autoSlidingStatistics.destroy();
    }


    @Override protected SchedulableSingleWorker getInstance()
    {
        return autoSlidingStatistics;
    }

    @Override protected Class<? extends Runnable> getRunnableClass()
    {
        return AutoSlidingStatistics.Slider.class;
    }

    @Override protected long getInitialDelay()
    {
        return 3;
    }

    @Override protected long getPeriod()
    {
        return 3;
    }

    @Override protected TimeUnit getTimeUnit()
    {
        return TimeUnit.SECONDS;
    }
}
