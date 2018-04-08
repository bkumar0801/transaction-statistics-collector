package com.n26.challenge.api.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StatisticsTest {

    @Test
    public void shouldTestAllProperties() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"sum\":1.1,\"avg\":1.2,\"max\":2.0,\"min\":0.0,\"count\":2}";
        Statistics statistics = null;
        try {
            statistics = mapper.readValue(jsonString, Statistics.class);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assertThat(statistics.getCount(), is(2));
        assertThat(statistics.getSum(), is(1.1));
        assertThat(statistics.getAvg(), is(1.2));
        assertThat(statistics.getMin(), is(0.0));
        assertThat(statistics.getMax(), is(2.0));
        String expectedString = "Statistics{" +
                "sum=1.1"+
                ", avg=1.2" +
                ", max=2.0"+
                ", min=0.0"+
                ", count=2"+
                "}" ;
        assertThat(statistics.toString(), is(expectedString));
    }
}
