package org.example.BusinessLogic;

import org.example.DataAccess.ProductDAO;
import org.example.Model.Product;
import org.example.Model.Status;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Logic Layer for Product operations.
 *     Delegates data acess to {@link ProductDAO} and performs validation or filtering as needed.
 */
public class ProductBLL {
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Retrieves all products, including soft-deleted ones.
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        List<Product> allProducts = productDAO.findAll();
        return allProducts;
    }

    /**
     * Retrieves only products marked as active.
     * @return list of active products
     */
    public List<Product> getAllActiveProducts() {
        List<Product> allProducts = getAllProducts();
        return allProducts.stream()
                .filter(p -> p.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Inserts a new client into the products.
     * @param product the product to add
     */
    public void addProduct(Product product) {
        productDAO.insert(product);
    }

    /**
     * Updates an existing product's information.
     * @param product the product with updated fields
     */
    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    /**
     * Soft-deletes a product by marking them as {@code Status.DELETED}.
     * @param product the product to mark as deleted
     */
    public void softDeleteProduct(Product product) {
        productDAO.softDelete(product); // soft delete
    }

    /**
     * Retries a product based on its id.
     * @param id the id of the wanted product.
     * @return product returned
     */
    public Product findById(int id) {
        return productDAO.findById(id);
    }

    /**
     * Completely removes a product and all their orders from the database.
     * @param id the ID of the product to delete
     */
    public void hardDeleteById(int id){
        productDAO.hardDeleteById(id);
    }
}
