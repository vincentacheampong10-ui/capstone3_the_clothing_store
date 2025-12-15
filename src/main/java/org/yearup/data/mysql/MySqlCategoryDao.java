package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.jdbc.core.JdbcOperationsExtensionsKt.query;
import static org.yearup.data.mysql.MySqlProductDao.mapRow;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        String sql = """
                SELECT category_id, name, description
                FROM categories;
                """;

        try (Connection conn = super.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {

                categories.add(mapRow(rs));
            }

            return categories;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all categories", e);
        }
    }

    @Override
    public Category getById(int categoryId) {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection conn = super.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, categoryId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting category by id", e);
        }

        return null; // no category found
    }

    @Override
    public Category create(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setCategoryId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating category", e);
        }

        return category;
    }


    @Override
    public void update(int categoryId, Category category) {
        String sql = """
                UPDATE categories
                SET name = ?, description = ?
                WHERE category_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {
            {
                setCategoryId(categoryId);
                setName(name);
                setDescription(description);
            }
        };

        return category;
    }
}

