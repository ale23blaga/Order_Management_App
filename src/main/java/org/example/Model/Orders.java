package org.example.Model;

/**
 * Represents an order placed by a client for a specific product.
 * Links client and product by ID and stores the requested quantity.
 */
public class Orders {
    private int id;
    private int clientId;
    private int productId;
    private int quantity;

    /**
     * Construct a new Order with the specified details.
     * @param id the order's id
     * @param clientId the id of the client who placed he order.
     * @param productId the id of the product for which the order is placed.
     * @param quantity the quantity of the product that is ordered
     */
    public Orders(int id, int clientId, int productId, int quantity) {
        this.id = id;
        this.clientId = clientId;

        this.productId = productId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


