package com.n26.challenge.api.controllers;

import com.n26.challenge.api.model.Event;
import com.n26.challenge.services.SlidingStatisticsInterface;
import com.n26.challenge.services.Statistics;
import com.n26.challenge.spring.TransactionStatisticsCollectorApp;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionStatisticsCollectorApp.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionsControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("slidingStatisticsInterface")
    private SlidingStatisticsInterface slidingStatisticsInterface;

    @Before
    public void setup() {
        slidingStatisticsInterface.resetStatistics();
    }

    @Test
    public void shouldAddTransaction201() {
        Event event = new Event();
        event.setAmount(123.45);
        event.setTimestamp(System.currentTimeMillis()/1000);

        ResponseEntity<Void> response = restTemplate.postForEntity("/transactions", event, Void.class);
        assertThat(response.getStatusCodeValue(),is(equalTo(201)));

        sleepFor(50);

        Statistics statistics = slidingStatisticsInterface.getStatistics();

        assertThat(statistics, Matchers.is(notNullValue()));
        assertThat(statistics.getCount(), Matchers.is(1));
        assertThat(statistics.getSum(), Matchers.is(123.45));
        assertThat(statistics.getAvg(), Matchers.is(123.45));
        assertThat(statistics.getMin(), Matchers.is(123.45));
        assertThat(statistics.getMax(), Matchers.is(123.45));
    }

    @Test
    public void shouldAddTransaction204() {
        Event event = new Event();
        event.setAmount(123.45);
        event.setTimestamp((System.currentTimeMillis()/1000)-80);

        ResponseEntity<Void> response = restTemplate.postForEntity("/transactions", event, Void.class);
        assertThat(response.getStatusCodeValue(),is(equalTo(204)));
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
