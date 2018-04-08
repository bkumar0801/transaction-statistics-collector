package com.n26.challenge.services;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StatisticsTest {
    @Test
    public void shouldTestAllProperties() {
        Statistics statistics= new Statistics(12.0, 4.0, 4.0, 4.0, 3);
        assertThat(statistics.getSum(), is(12.0));
        assertThat(statistics.getAvg(), is(4.0));
        assertThat(statistics.getMax(), is(4.0));
        assertThat(statistics.getMin(), is(4.0));
        assertThat(statistics.getCount(), is(3));
        assertThat(statistics.toString(), is("Statistics{sum=12.0, avg=4.0, max=4.0, min=4.0, count=3}"));
    }
}
