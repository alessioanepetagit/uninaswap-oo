package it.uninaswap.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.uninaswap.dao.DBConnection;
import it.uninaswap.dao.StatsPrezzi;

/**
 * Versione senza Map/long: espone metodi int per ogni conteggio.
 */
public class ReportDAOPg {

  // ---- COUNT totali (inviate dall'utente) per tipologia ----
  private static final String SQL_TOT_VENDITA =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND Tipo = 'OffertaVendita'";
  private static final String SQL_TOT_SCAMBIO =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND Tipo = 'OffertaScambio'";
  private static final String SQL_TOT_REGALO  =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND Tipo = 'OffertaRegalo'";

  // ---- COUNT accettate (tra quelle inviate dall'utente) per tipologia ----
  private static final String SQL_ACC_VENDITA =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND StatoOfferta = 'Accettata' AND Tipo = 'OffertaVendita'";
  private static final String SQL_ACC_SCAMBIO =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND StatoOfferta = 'Accettata' AND Tipo = 'OffertaScambio'";
  private static final String SQL_ACC_REGALO  =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND StatoOfferta = 'Accettata' AND Tipo = 'OffertaRegalo'";

  // ---- Statistiche € sulle offerte di VENDITA accettate (inviate dall'utente) ----
  private static final String SQL_STATS_VENDITE =
      "SELECT COUNT(*) AS n, " +
      "       AVG(o.PrezzoProposto) AS avgp, " +
      "       MIN(o.PrezzoProposto) AS minp, " +
      "       MAX(o.PrezzoProposto) AS maxp " +
      "  FROM OFFERTA o " +
      "  JOIN ANNUNCIO a ON a.IDAnnuncio = o.IDAnnuncio " +
      " WHERE o.IDUtente = ? " +
      "   AND o.StatoOfferta = 'Accettata' " +
      "   AND a.Tipo = 'AnnuncioVendita'";

  // ------------------ API public (solo int / StatsPrezzi) ------------------

  public int countTotVendita(int userId) { return scalarCount(SQL_TOT_VENDITA, userId); }
  public int countTotScambio(int userId) { return scalarCount(SQL_TOT_SCAMBIO, userId); }
  public int countTotRegalo (int userId) { return scalarCount(SQL_TOT_REGALO , userId); }

  public int countAccVendita(int userId) { return scalarCount(SQL_ACC_VENDITA, userId); }
  public int countAccScambio(int userId) { return scalarCount(SQL_ACC_SCAMBIO, userId); }
  public int countAccRegalo (int userId) { return scalarCount(SQL_ACC_REGALO , userId); }

  public StatsPrezzi statsVenditeAccettate(int userId) {
    StatsPrezzi s = new StatsPrezzi();
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_STATS_VENDITE)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          s.setTotale(rs.getInt("n"));

          double avg = rs.getDouble("avgp");
          if (!rs.wasNull()) s.setMedia(avg); else s.setMedia(null);

          double min = rs.getDouble("minp");
          if (!rs.wasNull()) s.setMinimo(min); else s.setMinimo(null);

          double max = rs.getDouble("maxp");
          if (!rs.wasNull()) s.setMassimo(max); else s.setMassimo(null);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return s;
  }

  // ------------------ helper ------------------

  private int scalarCount(String sql, int userId) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }
}
