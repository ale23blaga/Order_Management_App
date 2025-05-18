package org.example.PresentatoinLayer;

import org.example.BusinessLogic.ClientBLL;
import org.example.BusinessLogic.OrderBLL;
import org.example.BusinessLogic.ProductBLL;
import org.example.Model.Client;
import org.example.Model.Orders;
import org.example.Model.Product;
import org.example.Utilities.TableGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OrderPanel extends JPanel {
    private final ClientBLL clientBLL = new ClientBLL();
    private final ProductBLL productBLL = new ProductBLL();
    private final OrderBLL orderBLL = new OrderBLL();

    JTable clientTable;
    JTable productTable;
    JScrollPane clientScrollPane;
    JScrollPane productScrollPane;

    public OrderPanel(){
        setLayout(new BorderLayout());

        JTable clientTable = new TableGenerator<Client>().generateTable(clientBLL.getAllActiveClients());
        JTable productTable = new TableGenerator<Product>().generateTable(productBLL.getAllActiveProducts());

        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientScrollPane = new JScrollPane(clientTable);
        productScrollPane = new JScrollPane(productTable);

        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(clientScrollPane);
        center.add(productScrollPane);

        JPanel bottom = new JPanel();
        JTextField quantityField = new JTextField(5);
        JButton orderButton = new JButton("Place Order");
        JLabel statusLabel = new JLabel("");

        bottom.add(new JLabel("Quantity:"));
        bottom.add(quantityField);
        bottom.add(orderButton);
        bottom.add(statusLabel);

        orderButton.addActionListener((ActionEvent e) -> {
           int clientRow = clientTable.getSelectedRow();
           int productRow = productTable.getSelectedRow();

           if (clientRow == -1 || productRow == -1) {
               statusLabel.setText("Select client and product");
               return;
           }

           try{
               int clientId = (int) clientTable.getValueAt(clientRow, 0);
               int productId = (int) productTable.getValueAt(productRow, 0);
               int quantity = Integer.parseInt(quantityField.getText());

               Orders order = new Orders(0, clientId, productId, quantity);
               String result = orderBLL.placeOrder(order);
               statusLabel.setText(result);

               refreshTables();
           }catch(NumberFormatException ex){
               statusLabel.setText("Invalid Quantity");
           }
        });

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refreshTables(){
        clientTable = new TableGenerator<Client>().generateTable(clientBLL.getAllActiveClients());
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientScrollPane.setViewportView(clientTable);

        productTable = new TableGenerator<Product>().generateTable(productBLL.getAllActiveProducts());
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productScrollPane.setViewportView(productTable);
    }

}
