package org.example.Model;

/**
 * Immutable bill model stored in Log table.
 */
public record Bill(int id,int orderId,  String clientName, String productName, int quantity, double totalPrice) { }
