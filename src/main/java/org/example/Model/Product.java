package org.example.Model;

/**
 * Represents a product that can be ordered.
 * Includes name, quantity in stock, price, and status.
 */
public class Product {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private Status status;

    /**
     * Constructs a new Product with the default status (ACTIVE).
     */
    public Product() {
        this.status = Status.ACTIVE;
    }

    /**
     * Constructs a new Product with the specified details. The status is set to default (ACTIVE)
     * @param id the product's id
     * @param name the product's name
     * @param quantity the product's total quantity (stock)
     * @param price the product's price per unit
     */
    public Product(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.status = Status.ACTIVE;
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
