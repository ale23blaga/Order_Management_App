package org.example.BusinessLogic;

import org.example.DataAccess.ProductDAO;
import org.example.Model.Product;
import org.example.Model.Status;

import java.util.List;
import java.util.stream.Collectors;

public class ProductBLL {
    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        List<Product> allProducts = productDAO.findAll();
        return allProducts;
    }

    public List<Product> getAllActiveProducts() {
        List<Product> allProducts = getAllProducts();
        return allProducts.stream()
                .filter(p -> p.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    public void addProduct(Product product) {
        productDAO.insert(product);
    }

    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    public void deleteProduct(Product product) {
        productDAO.delete(product); // soft delete
    }

    public Product findById(int id) {
        return productDAO.findById(id);
    }
}
