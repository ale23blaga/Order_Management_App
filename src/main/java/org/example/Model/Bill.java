package org.example.Model;

/**
 * An immutable record respresenting a finalized bill for a placed order.
 * <p>
 *     Contains client and product details, quantity, and total price.
 *     Bills are written to a bill table and can only be insreted/read.
 * </p>
 * @param id unique bill identifier
 * @param orderId ID of the related order
 * @param clientName name of the client
 * @param productName name of the product
 * @param quantity number of products ordered
 * @param totalPrice calculated price of the entire order
 */
public record Bill(int id,int orderId, String clientName, String productName, int quantity, double totalPrice) { }
