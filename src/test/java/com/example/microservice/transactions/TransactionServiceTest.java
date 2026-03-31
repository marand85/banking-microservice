package com.example.microservice.transactions;

import com.example.microservice.transactions.model.Account;
import com.example.microservice.transactions.model.Transaction;
import com.example.microservice.transactions.service.TransactionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionServiceTest {

    private final TransactionService service = new TransactionService();

    @Test
    void shouldAggregateAndSortAccounts() {
        List<Transaction> input = List.of(
            new Transaction("32309111922661937852684864", "06105023389842834748547303", 10.90),
            new Transaction("31074318698137062235845814", "66105036543749403346524547", 200.90),
            new Transaction("66105036543749403346524547", "32309111922661937852684864", 50.10)
        );

        List<Account> result = service.generateReport(input);

        assertThat(result).hasSize(4);

        assertThat(result.get(0).account()).isEqualTo("06105023389842834748547303");
        assertThat(result.get(0).debitCount()).isEqualTo(0);
        assertThat(result.get(0).creditCount()).isEqualTo(1);
        assertThat(result.get(0).balance()).isEqualTo(10.90);

        assertThat(result.get(1).account()).isEqualTo("31074318698137062235845814");
        assertThat(result.get(1).debitCount()).isEqualTo(1);
        assertThat(result.get(1).creditCount()).isEqualTo(0);
        assertThat(result.get(1).balance()).isEqualTo(-200.90);

        assertThat(result.get(2).account()).isEqualTo("32309111922661937852684864");
        assertThat(result.get(2).debitCount()).isEqualTo(1);
        assertThat(result.get(2).creditCount()).isEqualTo(1);
        assertThat(result.get(2).balance()).isEqualTo(39.20);

        assertThat(result.get(3).account()).isEqualTo("66105036543749403346524547");
        assertThat(result.get(3).debitCount()).isEqualTo(1);
        assertThat(result.get(3).creditCount()).isEqualTo(1);
        assertThat(result.get(3).balance()).isEqualTo(150.80);
    }

    @Test
    void shouldReturnAccountsSortedAscending() {
        List<Transaction> input = List.of(
            new Transaction("BBBBBBBBBBBBBBBBBBBBBBBBBB", "AAAAAAAAAAAAAAAAAAAAAAAAAA", 1.0),
            new Transaction("CCCCCCCCCCCCCCCCCCCCCCCCCC", "BBBBBBBBBBBBBBBBBBBBBBBBBB", 1.0)
        );

        List<Account> result = service.generateReport(input);

        assertThat(result).extracting(Account::account)
            .containsExactly(
                "AAAAAAAAAAAAAAAAAAAAAAAAAA",
                "BBBBBBBBBBBBBBBBBBBBBBBBBB",
                "CCCCCCCCCCCCCCCCCCCCCCCCCC"
            );
    }

    @Test
    void shouldHandleAccountAppearingOnBothSides() {
        // Account A sends to B, then B sends back to A
        List<Transaction> input = List.of(
            new Transaction("AAAAAAAAAAAAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBB", 100.0),
            new Transaction("BBBBBBBBBBBBBBBBBBBBBBBBBB", "AAAAAAAAAAAAAAAAAAAAAAAAAA", 40.0)
        );

        List<Account> result = service.generateReport(input);

        Account a = result.get(0); // A
        assertThat(a.debitCount()).isEqualTo(1);
        assertThat(a.creditCount()).isEqualTo(1);
        assertThat(a.balance()).isEqualTo(-60.0);

        Account b = result.get(1); // B
        assertThat(b.debitCount()).isEqualTo(1);
        assertThat(b.creditCount()).isEqualTo(1);
        assertThat(b.balance()).isEqualTo(60.0);
    }
}
