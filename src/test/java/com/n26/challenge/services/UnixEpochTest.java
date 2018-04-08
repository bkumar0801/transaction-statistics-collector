package com.n26.challenge.services;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnixEpochTest {
    @Test
    public void now()
    {
        UnixEpoch now = new UnixEpoch();
        long distance = System.currentTimeMillis() - now.getAsDate().getTime();
        assertThat("Distance is " + distance, distance < 1500, is(true));
    }

    @Test
    public void before()
    {
        UnixEpoch unixEpoch = new UnixEpoch(1496264307);
        assertThat(unixEpoch.before(unixEpoch), is(false));

        UnixEpoch other = new UnixEpoch(1496264307 + 2);
        assertThat(unixEpoch.before(other), is(true));

        assertThat(other.before(unixEpoch), is(false));

    }

    @Test
    public void after()
    {
        UnixEpoch unixEpoch = new UnixEpoch(1496264307);
        assertThat(unixEpoch.after(unixEpoch), is(false));

        UnixEpoch other = new UnixEpoch(1496264307 - 12);
        assertThat(unixEpoch.after(other), is(true));

        assertThat(other.after(unixEpoch), is(false));

    }

    @Test
    public void equals() {
        UnixEpoch unixEpoch = new UnixEpoch(1496312086);
        UnixEpoch unixEpoch1 = new UnixEpoch(1496312086);

        assertThat(unixEpoch.equals(unixEpoch),is(true));

        assertThat(unixEpoch.equals(unixEpoch1),is(true));
        assertThat(unixEpoch1.equals(unixEpoch),is(true));
    }

    @Test
    public void isBeforeThan() {
        UnixEpoch unixEpoch = new UnixEpoch(1496312086);
        UnixEpoch unixEpoch1 = new UnixEpoch(1496312082);

        assertThat(unixEpoch1.isBeforeThan(unixEpoch, 2), is(true));

        assertThat(unixEpoch1.isBeforeThan(unixEpoch, 3), is(true));

        assertThat(unixEpoch1.isBeforeThan(unixEpoch, 4), is(false));

    }

    @Test
    public void isBeforeThanNow() {
        UnixEpoch unixEpoch = UnixEpoch.now();
        unixEpoch = unixEpoch.add(-6);

        assertThat(unixEpoch.isBeforeThan(5), is(true));

        assertThat(unixEpoch.isBeforeThan(6), is(false));

    }
}
