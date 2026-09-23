package bank.service;

import bank.domain.Transaction;

import java.util.List;

public record TransactionPage(List<Transaction> content, int page, int size, long totalElements) {
    public TransactionPage {
        content = List.copyOf(content);
        if (page < 0 || size <= 0 || totalElements < 0) {
            throw new IllegalArgumentException("Invalid transaction page");
        }
    }
}