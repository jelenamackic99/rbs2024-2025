package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.config.Entity;
import com.zuehlke.securesoftwaredevelopment.domain.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CommentRepository.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(CommentRepository.class);
    private DataSource dataSource;

    public CommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(Comment comment) {
        String query = "insert into comments(bookId, userId, comment) values (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setInt(1, comment.getBookId());
            statement.setInt(2, comment.getUserId());
            statement.setString(3, comment.getComment());
            statement.executeUpdate();
            auditLogger.audit(String.format("DODAN KOMENTAR '%s' za knjigu sa id: '%s'", comment.getComment(), comment.getBookId()));
            LOG.info("KOMENTAR KREIRAN: Korisnik ID: {} postavio komentar za knjigu ID: {}",
                    comment.getUserId(), comment.getBookId());
        } catch (SQLException e) {
            LOG.error("Greška pri kreiranju komentara za knjigu ID: {}. Korisnik ID: {}",
                    comment.getBookId(), comment.getUserId(), e);
        }
    }

    public List<Comment> getAll(String bookId) {
        List<Comment> commentList = new ArrayList<>();
        String query = "SELECT bookId, userId, comment FROM comments WHERE bookId = " + bookId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                commentList.add(new Comment(rs.getInt(1), rs.getInt(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            LOG.error("Greška pri dohvatanju komentara za knjigu ID: {}.", bookId, e);
        }
        return commentList;
    }
}
