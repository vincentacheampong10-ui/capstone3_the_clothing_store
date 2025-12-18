package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

import static java.sql.DriverManager.getConnection;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {
    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            prepareStatement.setInt(1, profile.getUserId());
            prepareStatement.setString(2, profile.getFirstName());
            prepareStatement.setString(3, profile.getLastName());
            prepareStatement.setString(4, profile.getPhone());
            prepareStatement.setString(5, profile.getEmail());
            prepareStatement.setString(6, profile.getAddress());
            prepareStatement.setString(7, profile.getCity());
            prepareStatement.setString(8, profile.getState());
            prepareStatement.setString(9, profile.getZip());

            prepareStatement.executeUpdate();

            return profile;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Profile getByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapRow(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void update(Profile profile)
    {
        String sql = "UPDATE profiles SET first_name = ?, last_name = ?, phone = ?, email = ?, " +
                "address = ?, city = ?, state = ?, zip = ? WHERE user_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, profile.getFirstName());
            statement.setString(2, profile.getLastName());
            statement.setString(3, profile.getPhone());
            statement.setString(4, profile.getEmail());
            statement.setString(5, profile.getAddress());
            statement.setString(6, profile.getCity());
            statement.setString(7, profile.getState());
            statement.setString(8, profile.getZip());
            statement.setInt(9, profile.getUserId());

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Profile mapRow(ResultSet row) throws SQLException
    {
        int userId = row.getInt("user_id");
        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");

        return new Profile(userId, firstName, lastName, phone, email, address, city, state, zip);
    }
}
