package bank.service;

import bank.domain.TransactionType;

import java.time.Instant;

public record TransactionQuery(TransactionType type, String text, Instant from, Instant to, int page, int size) {
    public TransactionQuery {
        text = text == null ? "" : text.trim();
        if (text.length() > 100 || page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Invalid transaction query");
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }
    }
}