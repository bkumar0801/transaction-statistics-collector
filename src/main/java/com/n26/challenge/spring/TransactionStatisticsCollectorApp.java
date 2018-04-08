package com.n26.challenge.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.n26.challenge.spring","com.n26.challenge.api.controllers"})
public class TransactionStatisticsCollectorApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(TransactionStatisticsCollectorApp.class, args);
    }
}
