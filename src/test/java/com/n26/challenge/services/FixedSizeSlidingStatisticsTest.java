package com.n26.challenge.services;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FixedSizeSlidingStatisticsTest {
    FixedSizeSlidingStatistics fixedSizeSlidingStatistics;

    @Before
    public void setup()
    {
        fixedSizeSlidingStatistics = new FixedSizeSlidingStatistics(5);
    }

    @Test
    public void onlySomeEventsAtTheBeginning()
    {
        fixedSizeSlidingStatistics.addTransaction(Transaction.forAmount(3.5));
        fixedSizeSlidingStatistics.addTransaction(Transaction.forAmount(8.22));
        fixedSizeSlidingStatistics.addTransactions(
                asList(
                        Transaction.forAmount(18.22),
                        Transaction.forAmount(86.72),
                        Transaction.forAmount(180.12),
                        Transaction.forAmount(48),
                        Transaction.forAmount(57)
                )
        );

        for (int i = 0; i < 5; i++)
        {
            Statistics statistics = fixedSizeSlidingStatistics.getStatistics();
            assertStatistics(statistics, 401.78, 57.39714285714285, 180.12, 3.5, 7);
            fixedSizeSlidingStatistics.slide();
        }

        Statistics statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 0.0, 0.0, 0.0, 0.0, 0);
    }



    @Test
    public void eventsArrivingInDifferentMoments()
    {
        fixedSizeSlidingStatistics.addTransaction(Transaction.forAmount(3.5));
        fixedSizeSlidingStatistics.addTransaction(Transaction.forAmount(8.22));

        Statistics statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 11.72, 5.86, 8.22, 3.5, 2);


        fixedSizeSlidingStatistics.slide();

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 11.72, 5.86, 8.22, 3.5, 2);

        fixedSizeSlidingStatistics.addTransactions(
                asList(
                        Transaction.forAmount(47.562),
                        Transaction.forAmount(86.72)
                )
        );

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 146.002, 36.5005, 86.72, 3.5, 4);

        fixedSizeSlidingStatistics.slide();
        fixedSizeSlidingStatistics.slide();

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 146.002, 36.5005, 86.72, 3.5, 4);

        fixedSizeSlidingStatistics.slide();
        fixedSizeSlidingStatistics.slide();

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 134.28199999999998, 67.14099999999999, 86.72, 47.562, 2);

        fixedSizeSlidingStatistics.slide();

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 0.0, 0.0, 0.0, 0.0, 0);
    }

    @Test
    public void transactionsNotAppliableToAllTheSamples()
    {
        UnixEpoch now = UnixEpoch.now();

        fixedSizeSlidingStatistics.addTransaction(
                new Transaction(
                        3.5,
                        now.add(-40)
                )
        );
        fixedSizeSlidingStatistics.addTransaction(
                new Transaction(
                        123.15,
                        now.add(-58)
                )
        );
        fixedSizeSlidingStatistics.addTransaction(
                new Transaction(
                        88.5,
                        now.add(-57)
                )
        );
        fixedSizeSlidingStatistics.addTransaction(
                new Transaction(
                        23.5,
                        now.add(-60)
                )
        );

        Statistics statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 215.15, 71.716666666666667, 123.15, 3.5, 3);

        fixedSizeSlidingStatistics.slide();

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 215.15, 71.716666666666667, 123.15, 3.5, 3);

        fixedSizeSlidingStatistics.slide();

        statistics = fixedSizeSlidingStatistics.getStatistics();
        assertStatistics(statistics, 92, 46, 88.5, 3.5, 2);
    }

    private void assertStatistics(Statistics statistics, double sum, double avg, double max, double min, int count) {
        assertThat(statistics.getSum(), is(sum));
        assertThat(statistics.getAvg(), is(avg));
        assertThat(statistics.getMax(), is(max));
        assertThat(statistics.getMin(), is(min));
        assertThat(statistics.getCount(), is(count));
    }
}
