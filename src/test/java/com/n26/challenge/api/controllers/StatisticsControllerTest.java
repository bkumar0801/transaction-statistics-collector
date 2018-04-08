package com.n26.challenge.api.controllers;

import com.n26.challenge.services.SlidingStatisticsInterface;
import com.n26.challenge.services.Transaction;
import com.n26.challenge.api.model.Statistics;
import com.n26.challenge.spring.TransactionStatisticsCollectorApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionStatisticsCollectorApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("slidingStatisticsSamples")
    private SlidingStatisticsInterface slidingStatisticsSamples;

    @Test
    public void shouldTestEmptyStatistics()
    {
        Statistics statistics = restTemplate.getForObject("/statistics", Statistics.class);
        assertThat(statistics, is(notNullValue()));
        assertThat(statistics.getCount(), is(0));
        assertThat(statistics.getSum(), is(0.0));
        assertThat(statistics.getAvg(), is(0.0));
        assertThat(statistics.getMin(), is(0.0));
        assertThat(statistics.getMax(), is(0.0));
    }

    @Test
    public void shouldTestStatisticsAfterSomeTransactions()
    {

        slidingStatisticsSamples.addTransaction(Transaction.forAmount(123.12));
        slidingStatisticsSamples.addTransaction(Transaction.forAmount(23.00));
        slidingStatisticsSamples.addTransaction(Transaction.forAmount(523.12));

        sleepFor(150);

        Statistics statistics = restTemplate.getForObject("/statistics", Statistics.class);
        assertThat(statistics, is(notNullValue()));
        assertThat(statistics.getCount(), is(3));
        assertThat(statistics.getSum(), is(669.24));
        assertThat(statistics.getAvg(), is(223.08));
        assertThat(statistics.getMin(), is(23.00));
        assertThat(statistics.getMax(), is(523.12));
    }

    @Test
    public void resetStatistics()
    {
        restTemplate.delete("/s'0.0.1-SNAPSHOT'tatistics");
    }

    public void sleepFor(long howMuch)
    {
        try
        {
            Thread.sleep(howMuch);
        } catch (Exception ex) {

        }
    }
}
