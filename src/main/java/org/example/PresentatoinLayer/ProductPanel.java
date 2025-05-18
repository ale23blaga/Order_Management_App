package org.example.PresentatoinLayer;

import org.example.BusinessLogic.ProductBLL;
import org.example.Model.Product;
import org.example.Utilities.TableGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private final ProductBLL productBLL = new ProductBLL();
    private JTable productTable;
    private final JCheckBox showAllCheckBox = new JCheckBox("Show Deleted");
    private JScrollPane productTableScrollPane;

    public ProductPanel() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();

        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");

        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(showAllCheckBox);

        productTable = new TableGenerator<Product>().generateTable(productBLL.getAllActiveProducts());
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTableScrollPane = new JScrollPane(productTable);

        showAllCheckBox.addActionListener(e -> refreshTable());
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteButton());

        add(topPanel, BorderLayout.NORTH);
        add(productTableScrollPane, BorderLayout.CENTER);
    }
    private void refreshTable() {
        List<Product> data = showAllCheckBox.isSelected() ? productBLL.getAllProducts() : productBLL.getAllActiveProducts();
        productTable.setModel(new TableGenerator<Product>().generateTable(data).getModel());
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTableScrollPane.setViewportView(productTable);
    }

    private void addProduct() {
        JTextField name = new JTextField(15);
        JTextField quantity = new JTextField(15);
        JTextField price = new JTextField(15);

        Object[] inputs = {"Name:", name, "Quantity:", quantity, "Price:", price};
        int result = JOptionPane.showConfirmDialog(null, inputs, "Add Product", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            try{
                int qty = Integer.parseInt(quantity.getText());
                double prc = Double.parseDouble(price.getText());
                productBLL.addProduct(new Product(0, name.getText(), qty, prc));
                refreshTable();
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Invalid quantity or price");
            }
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "No product selected.");
            return;
        }
        int id = (int)productTable.getValueAt(row, 0);
        Product existingProduct = productBLL.findById(id);

        JTextField name = new JTextField(String.valueOf(productTable.getValueAt(row, 1)));
        JTextField quantity = new JTextField(String.valueOf(productTable.getValueAt(row, 2)));
        JTextField price = new JTextField(String.valueOf(productTable.getValueAt(row, 3)));

        Object[] inputs = {"Name:", name, "Quantity:", quantity, "Price:", price};
        int result = JOptionPane.showConfirmDialog(null, inputs, "Update Product", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            try{
                int qty = Integer.parseInt(quantity.getText());
                double prc = Double.parseDouble(price.getText());
                Product updated = new Product(id, name.getText(), qty, prc);
                updated.setStatus(existingProduct.getStatus());
                productBLL.updateProduct(updated);
                refreshTable();
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Invalid quantity or price");
            }
        }
    }

    private void deleteButton() {
        int row = productTable.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(null, "No product selected.");
            return;
        }
        int id = (int)productTable.getValueAt(row, 0);
        Product p = productBLL.findById(id);
        productBLL.deleteProduct(p);
        refreshTable();
    }
}
