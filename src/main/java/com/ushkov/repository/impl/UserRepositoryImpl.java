package com.ushkov.repository.impl;

import com.ushkov.domain.User;
import com.ushkov.exception.NoSuchEntityException;
import com.ushkov.repository.UserRepository;
import com.ushkov.util.DatabasePropertiesReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.ushkov.util.DatabasePropertiesReader.*;

public class UserRepositoryImpl implements UserRepository {

    private DatabasePropertiesReader reader = DatabasePropertiesReader.getInstance();

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String BIRTH_DATE = "birth_date";
    public static final String LOGIN = "login";
    public static final String WEIGHT = "weight";

    @Override
    public List<User> findAll() {
        final String findAllQuery = "select * from users order by id desc";
        List<User> result = new ArrayList<>();
        Connection connection;
        Statement statement;
        ResultSet rs;
        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);
        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            statement = connection.createStatement();
            rs = statement.executeQuery(findAllQuery);
            //Row mapping
            while (rs.next()) {
                result.add(CreateUserFromDBResult(rs));
            }

            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public User findOne(Long id) {
        final String findById = "select * from users where id = ?";
        Connection connection;
        PreparedStatement statement;
        ResultSet rs;
        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);
        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            statement = connection.prepareStatement(findById);
            statement.setLong(1, id);
            rs = statement.executeQuery();
            //Row mapping
            if (rs.next()) {
                return CreateUserFromDBResult(rs);
            }
            throw new NoSuchEntityException("No such user with id:" + id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public User save(User user) {
        final String insertQuery = "insert into users (name, surname, birth_date, login, weight) " +
                "values (?,?,?,?,?)";
        Connection connection;
        PreparedStatement statement;
        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);
        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            statement = connection.prepareStatement(insertQuery);
            PreparedStatement lastInsertId = connection.prepareStatement("SELECT currval('users_id_seq') as last_insert_id;");
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setDate(3, new Date(user.getBirthDate().getTime()));
            statement.setString(4, user.getLogin());
            statement.setFloat(5, user.getWeight());

            statement.executeUpdate();

            Long insertedId;
            ResultSet lastIdResultSet = lastInsertId.executeQuery();
            if (lastIdResultSet.next()) {
                insertedId = lastIdResultSet.getLong("last_insert_id");
            } else {
                throw new RuntimeException("We cannot read sequence last value during User creation!");
            }

            return findOne(insertedId);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public User update(User user) {
        final String updateQuery =
                "UPDATE users SET name = ?, surname = ?, birth_date = ?, login = ?, weight = ? WHERE id = ?";
        Connection connection;
        PreparedStatement statement;

        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);

        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            statement = connection.prepareStatement(updateQuery);

            PreparedStatement lastInsertId = connection.prepareStatement("SELECT currval('users_id_seq') as last_insert_id;");

            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setDate(3, new Date(user.getBirthDate().getTime()));
            statement.setString(4, user.getLogin());
            statement.setFloat(5, user.getWeight());
            statement.setLong(6, user.getId());

            statement.executeUpdate();

            User updatedUser = findOne(user.getId());
            if (!user.equals(updatedUser)) {
                throw new RuntimeException("Can`t update user!");
            }
            return updatedUser;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public void delete(Long id) {
        final String deleteQuery =
                "DELETE FROM users WHERE id = ?";
        Connection connection;
        PreparedStatement statement;

        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);

        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            final String controlCarOwners = "SELECT * FROM cars WHERE owner = ?";
            PreparedStatement controlCarsOwnersStatment = connection.prepareStatement(controlCarOwners);
            controlCarsOwnersStatment.setLong(1, id);
            if (controlCarsOwnersStatment.executeQuery().next())
                throw new RuntimeException("This User is owner of car/cars. It necessary delete information about cars, that belong to that user, before deleting.");
            statement = connection.prepareStatement(deleteQuery);

            PreparedStatement lastInsertId = connection.prepareStatement("SELECT currval('users_id_seq') as last_insert_id;");
            statement.setLong(1, id);
            int resultOfExecuteQuery = statement.executeUpdate();
            if (resultOfExecuteQuery == 0) {
                throw new RuntimeException("Can`t delete user!");
            }
            if (resultOfExecuteQuery > 1)
                throw new RuntimeException("Something wrong is happen. There are more than 1 row deleted from DB. Deleted " + resultOfExecuteQuery + " rows.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public List<User> findUsersByQuery(String query) {
        //final String findAllQuery = "select * from users order by id desc";

        List<User> result = new ArrayList<>();

        Connection connection;
        Statement statement;
        ResultSet rs;

        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }

        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);

        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            //Row mapping
            while (rs.next()) {
                result.add(CreateUserFromDBResult(rs));
            }

            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    @Override
    public Double getUserExpensiveCarPrice(Integer userId) {
        final String findPriceFunction = "select get_user_expensive_car(?)";

        Connection connection;
        PreparedStatement statement;
        ResultSet rs;

        try {
            Class.forName(reader.getProperty(DATABASE_DRIVER_NAME));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }

        String jdbcURL = reader.getProperty(DATABASE_URL);
        String login = reader.getProperty(DATABASE_LOGIN);
        String password = reader.getProperty(DATABASE_PASSWORD);

        try {
            connection = DriverManager.getConnection(jdbcURL, login, password);
            statement = connection.prepareStatement(findPriceFunction);
            statement.setInt(1, userId);
            rs = statement.executeQuery();

            //Row mapping
            rs.next();
            return rs.getDouble("get_user_expensive_car");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("SQL Issues!");
        }
    }

    private static User CreateUserFromDBResult(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong(ID));
        user.setName(rs.getString(NAME));
        user.setSurname(rs.getString(SURNAME));
        user.setLogin(rs.getString(LOGIN));
        user.setBirthDate(rs.getDate(BIRTH_DATE));
        user.setWeight(rs.getFloat(WEIGHT));
        return user;
    }
}