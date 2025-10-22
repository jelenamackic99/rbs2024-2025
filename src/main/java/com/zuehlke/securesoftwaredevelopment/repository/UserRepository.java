package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    private DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User findUser(String username) {
        String query = "SELECT id, username, password FROM users WHERE username='" + username + "'";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            if (rs.next()) {
                int id = rs.getInt(1);
                String username1 = rs.getString(2);
                String password = rs.getString(3);
                return new User(id, username1, password);
            }
        } catch (SQLException e) {
            LOG.error("Greška pri dohvatanju korisnika {}",username , e);
        }
        return null;
    }

    public String findUsername(int id) {
        String query = "SELECT username FROM users WHERE id=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            if (rs.next()) {
                String username = rs.getString(1);
                return username;
            }
        } catch (SQLException e) {
            LOG.error("Greška pri dohvatanju korisničkog imena ID: {}", id, e);
        }
        return null;
    }

    public void updateUsername(int id, String username) {
        String query = "UPDATE users SET username = ? WHERE id = " + id;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, username);
            statement.executeUpdate();
            LOG.info("USPJEŠAN UPDATE: Promijenjeno korisničko ime za ID: {} na {}", id, username);
        } catch (SQLException e) {
            LOG.error("Greška pri ažuriranju korisničkog imena ID: {}", id, e);
        }
    }

    public boolean validCredentials(String username, String password) {
        String query = "SELECT username FROM users WHERE username = ? AND password = ?";

        try (Connection connection = dataSource.getConnection();
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                boolean isValid = rs.next();
                if (!isValid) {
                    LOG.warn("NEUSPJELA PRIJAVA: Pokušaj sa username: {}", username);
                }
                return isValid;
            }
        } catch (SQLException e) {
            LOG.error("Greška pri validaciji kredencijala za korisnika: {}", username, e);
        }
        return false;
    }

    public void delete(int userId) {
        String query = "DELETE FROM users WHERE id = " + userId;
        LOG.info("Brisanje korisnika {}", userId);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        )
        {
            statement.executeUpdate(query);
            LOG.info("KORISNIK OBRISAN: Uspješno obrisan korisnik ID: {}", userId);
        } catch (SQLException e) {
            LOG.error("Greška pri brisanju korisnika id {}",userId , e);
        }
    }
}
