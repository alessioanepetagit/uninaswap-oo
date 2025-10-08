package it.uninaswap.dao.postgres;

import it.uninaswap.dao.DBConnection;
import it.uninaswap.dao.UtenteDAO;
import it.uninaswap.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAOPg implements UtenteDAO {

  private static final String SQL_BY_USER_PASS =
      "SELECT IDUtente, Email, Username, Nome, Cognome " +
      "FROM UTENTE WHERE Username = ? AND Password = ?";

  private static final String SQL_BY_ID =
      "SELECT IDUtente, Email, Username, Nome, Cognome " +
      "FROM UTENTE WHERE IDUtente = ?";

  @Override
  public Utente findByUsernameAndPassword(String username, String password) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_BY_USER_PASS)) {
      ps.setString(1, username);
      ps.setString(2, password);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return map(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Utente findById(int id) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_BY_ID)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return map(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Utente map(ResultSet rs) throws SQLException {
    Utente u = new Utente();
    u.setId(rs.getInt("IDUtente"));
    u.setEmail(rs.getString("Email"));
    u.setUsername(rs.getString("Username"));
    u.setNome(rs.getString("Nome"));
    u.setCognome(rs.getString("Cognome"));
    // NOTA: non impostiamo la password perché il model non espone setPassword
    return u;
  }
}
