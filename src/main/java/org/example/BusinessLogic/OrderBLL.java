package org.example.BusinessLogic;

import org.example.DataAccess.BillDAO;
import org.example.DataAccess.ClientDAO;
import org.example.DataAccess.OrderDAO;
import org.example.DataAccess.ProductDAO;
import org.example.Model.*;

import java.util.List;

/**
 * Business Logic Layer for Orders.
 */
public class OrderBLL {
    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final BillDAO billDAO = new BillDAO();

    public List<Orders> getAllOrders() {
        return orderDAO.findAll();
    }

    public String placeOrder(Orders order) {
        Product product = productDAO.findById(order.getProductId());
        Client client = clientDAO.findById(order.getClientId());

        if (product == null || product.getStatus() == Status.DELETED){
            return "Product not found or deleted.";
        }
        if (client == null || client.getStatus() == Status.DELETED){
            return "Client not found or deleted.";
        }
        if(product.getQuantity() < order.getQuantity()){
            return "Not enough stock!";
        }

        //update product stock
        product.setQuantity(product.getQuantity() - order.getQuantity());
        productDAO.update(product);

        //Insert Order
        orderDAO.insert(order);

        //Create and insert Bill
        double totalPrice = order.getQuantity() * product.getPrice();
        Bill bill = new Bill(0, order.getId(), client.getName(), product.getName(), order.getQuantity(), totalPrice);
        new BillDAO().insert(bill);
        return "Order placed successfully!";
    }
}
