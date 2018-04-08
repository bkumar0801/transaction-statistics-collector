package com.n26.challenge.spring;

import com.n26.challenge.services.AsyncTransactionUpdaterStatistics;
import com.n26.challenge.services.AutoSlidingStatistics;
import com.n26.challenge.services.FixedSizeSlidingStatistics;
import com.n26.challenge.services.SlidingStatisticsInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpringConfiguration
{
    @Value("${transaction-statistics-collector.sliding.period}")
    long slidingInterval;
    @Value("${transaction-statistics-collector.sliding.timeUnit}")
    TimeUnit slidingPeriod;

    @Value("${transaction-statistics-collector.transactionUpdater.period}")
    long transactionUpdaterInterval;
    @Value("${transaction-statistics-collector.transactionUpdater.timeUnit}")
    TimeUnit transactionUpdaterPeriod;
    @Value("${transaction-statistics-collector.transactionUpdater.bufferSize}")
    int transactionUpdaterSize;

    @Bean(name = "realSlidingStatisticsSample")
    public FixedSizeSlidingStatistics fixedSizeSlidingStatisticsSamples()
    {
        return new FixedSizeSlidingStatistics(60);
    }

    @Bean(name = "transactionUpdaterScheduledExecutorService")
    public ScheduledExecutorService transactionUpdaterScheduledExecutorService()
    {
        return Executors.newScheduledThreadPool(1, r -> new Thread(r, "TransactionUpdater"));
    }

    @Bean(name = "transactionUpdaterStatisticsSamples")
    @Autowired
    public AsyncTransactionUpdaterStatistics asyncTransactionUpdaterStatisticsSamples(
            @Qualifier("realSlidingStatisticsSample") SlidingStatisticsInterface delegate,
            @Qualifier("transactionUpdaterScheduledExecutorService") ScheduledExecutorService executorService)
    {
        return new AsyncTransactionUpdaterStatistics(delegate,
                executorService,
                transactionUpdaterInterval,
                transactionUpdaterPeriod,
                transactionUpdaterSize);
    }

    @Bean(name = "autoSlidingScheduledExecutorService")
    public ScheduledExecutorService autoSlidingScheduledExecutorService()
    {
        return Executors.newScheduledThreadPool(1, r -> new Thread(r, "SamplesSlider"));
    }

    @Bean(name = "slidingStatisticsSamples")
    @Autowired
    public AutoSlidingStatistics autoSlidingStatisticsSamples(
            @Qualifier("transactionUpdaterStatisticsSamples") SlidingStatisticsInterface delegate,
            @Qualifier("autoSlidingScheduledExecutorService") ScheduledExecutorService executorService)
    {
        return new AutoSlidingStatistics(delegate,
                executorService,
                slidingInterval,
                slidingPeriod);
    }

}
