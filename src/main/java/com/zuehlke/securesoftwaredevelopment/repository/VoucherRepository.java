package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.domain.Tag;
import com.zuehlke.securesoftwaredevelopment.domain.Voucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoucherRepository {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherRepository.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(VoucherRepository.class);

    private DataSource dataSource;

    public VoucherRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(int userId, String code, int value) {
        String query = "INSERT INTO voucher(code, value) VALUES(?, ?)";
        long id = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, code);
            statement.setString(2, String.valueOf(value));
            statement.executeUpdate();
            LOG.info("VOUCHER KREIRAN: Korisnik ID: {} kreirao novi vaučer: {}", userId, code);
        }  catch (SQLException e) {
            LOG.error("Greska pri kreiranju vaucera za korisnika ID: {}", userId, e);
        }
    }

    public boolean checkIfVoucherExist(String voucher) {
        String query = "SELECT id FROM voucher WHERE code=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, voucher);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            LOG.error("Greska pri dohvatanju vaucera {}", voucher, e);
        }
        return false;
    }

    public boolean checkIfVoucherIsAssignedToUser(String voucher, int id) {
        String query1 = "SELECT username FROM users WHERE id=?";
        String username = "";

        try (Connection connection = dataSource.getConnection();

             PreparedStatement statement1 = connection.prepareStatement(query1)) {

            statement1.setInt(1, id);

            try (ResultSet rs = statement1.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString(1);
                } else {
                    return false;
                }
            }

            String query2 = "SELECT id FROM voucher WHERE code=? AND code LIKE ?";

            try (PreparedStatement statement2 = connection.prepareStatement(query2)) {

                statement2.setString(1, voucher);
                statement2.setString(2, "%" + username + "%");

                try (ResultSet set = statement2.executeQuery()) {
                    if (set.next()) {
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            LOG.error("Greska pri provjeri vaucera {}", voucher,  e);
        }
        return false;
    }

    public void deleteVoucher(String voucher) {
        String query = "DELETE FROM voucher WHERE code=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, voucher);
            statement.executeUpdate();
            LOG.info("VOUCHER OBRISAN: Uspješno obrisan vaučer: {}", voucher);
        } catch (SQLException e) {
            LOG.error("Greška pri brisanju vaučera: {}", voucher, e);
        }
    }

    public List<Voucher> getAll() {
        List<Voucher> vouchers = new ArrayList<>();
        String query = "SELECT id, code, value FROM voucher";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                vouchers.add(new Voucher(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException e) {
            LOG.error("Greška pri dohvatanju svih vaučera", e);
        }
        return vouchers;
    }

}
