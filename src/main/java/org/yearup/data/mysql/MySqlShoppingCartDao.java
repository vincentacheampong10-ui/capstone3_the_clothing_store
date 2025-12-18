package org.yearup.data.mysql;


import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        String sql = "SELECT sc.product_id, sc.quantity, " +
                "p.name, p.price, p.category_id, p.description, p.stock, p.featured, p.image_url " +
                "FROM shopping_cart sc "  +
                "LEFT JOIN products p ON sc.product_id = p.product_id " +
                "WHERE sc.user_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

            while (row.next()) {
                ShoppingCartItem item = new ShoppingCartItem();

                // Create product
                Product product = new Product();
                product.setProductId(row.getInt("product_id"));
                product.setName(row.getString("name"));
                product.setPrice(row.getBigDecimal("price"));
                product.setCategoryId(row.getInt("category_id"));
                product.setDescription(row.getString("description"));
                product.setStock(row.getInt("stock"));
                product.setFeatured(row.getBoolean("featured"));
                product.setImageUrl(row.getString("image_url"));
              //  product.setSubCategory(row.getString("sub_category"));

                // Set item properties
                item.setProduct(product);
                item.setQuantity(row.getInt("quantity"));

                // Add to cart
                cart.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cart;
    }

    @Override
    public ShoppingCart addProduct(int userId, int productId) {

        // Check if product already exists in cart
        String query = """
                INSERT INTO shopping_cart(
                    user_id,
                    product_id,
                    quantity)
                VALUES(?, ?, 1)
                ON DUPLICATE KEY
                    UPDATE quantity=quantity+1;
                """;


        try (Connection connection = getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(query)) {

            checkStatement.setInt(1, userId);
            checkStatement.setInt(2, productId);
            checkStatement.executeUpdate();

            return getByUserId(userId);

        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateProduct(int userId, int productId, int quantity) {

    }

    @Override
    public void clearCart(int userId) {

    }
}
