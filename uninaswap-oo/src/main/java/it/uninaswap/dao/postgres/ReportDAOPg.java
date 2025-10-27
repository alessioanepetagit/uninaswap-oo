package it.uninaswap.dao.postgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.uninaswap.dao.ReportDAO;
import it.uninaswap.dbconnection.DBConnection;

public class ReportDAOPg implements ReportDAO {

  // ---- COUNT totali (inviate) per tipologia ----
  private static final String SQL_TOT_VENDITA =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND Tipo = 'OffertaVendita'";
  private static final String SQL_TOT_SCAMBIO =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND Tipo = 'OffertaScambio'";
  private static final String SQL_TOT_REGALO  =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND Tipo = 'OffertaRegalo'";

  // ---- COUNT accettate (inviate) per tipologia ----
  private static final String SQL_ACC_VENDITA =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND StatoOfferta = 'Accettata' AND Tipo = 'OffertaVendita'";
  private static final String SQL_ACC_SCAMBIO =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND StatoOfferta = 'Accettata' AND Tipo = 'OffertaScambio'";
  private static final String SQL_ACC_REGALO  =
      "SELECT COUNT(*) FROM OFFERTA WHERE IDUtente = ? AND StatoOfferta = 'Accettata' AND Tipo = 'OffertaRegalo'";

  // ---- Statistiche vendite accettate (tra le offerte INVIATE dall’utente) ----
  private static final String SQL_STATS_VENDITE =
      "SELECT COUNT(*) AS tot, AVG(o.PrezzoProposto) AS media, " +
      "       MIN(o.PrezzoProposto) AS minimo, MAX(o.PrezzoProposto) AS massimo " +
      "FROM OFFERTA o " +
      "JOIN ANNUNCIO a ON a.IDAnnuncio = o.IDAnnuncio " +
      "WHERE o.StatoOfferta = 'Accettata' " +
      "  AND o.Tipo = 'OffertaVendita' " +
      "  AND o.IDUtente = ?";

  @Override public int countTotVendita(int userId) { return scalarCount(SQL_TOT_VENDITA, userId); }
  @Override public int countTotScambio(int userId) { return scalarCount(SQL_TOT_SCAMBIO, userId); }
  @Override public int countTotRegalo (int userId) { return scalarCount(SQL_TOT_REGALO , userId); }

  @Override public int countAccVendita(int userId) { return scalarCount(SQL_ACC_VENDITA, userId); }
  @Override public int countAccScambio(int userId) { return scalarCount(SQL_ACC_SCAMBIO, userId); }
  @Override public int countAccRegalo (int userId) { return scalarCount(SQL_ACC_REGALO , userId); }

  @Override public int countVenditeAccettate(int userId) {
    return (int) fetchVenditeAccettateStats(userId)[0];
  }

  @Override public Double avgVenditeAccettate(int userId) {
    double v = fetchVenditeAccettateStats(userId)[1];
    return Double.isNaN(v) ? null : v;
  }

  @Override public Double minVenditeAccettate(int userId) {
    double v = fetchVenditeAccettateStats(userId)[2];
    return Double.isNaN(v) ? null : v;
  }

  @Override public Double maxVenditeAccettate(int userId) {
    double v = fetchVenditeAccettateStats(userId)[3];
    return Double.isNaN(v) ? null : v;
  }

  // helper 

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

  /* Ritorna { tot, media, min, max } come double; per i NULL usa NaN. */
  private double[] fetchVenditeAccettateStats(int userId) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_STATS_VENDITE)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          // COUNT(*) non è mai NULL
          double tot = rs.getInt("tot");

          // NUMERIC/DECIMAL -> BigDecimal in JDBC
          BigDecimal bdMedia = rs.getBigDecimal("media");
          BigDecimal bdMin   = rs.getBigDecimal("minimo");
          BigDecimal bdMax   = rs.getBigDecimal("massimo");

          double media = (bdMedia == null) ? Double.NaN : bdMedia.doubleValue();
          double min   = (bdMin   == null) ? Double.NaN : bdMin.doubleValue();
          double max   = (bdMax   == null) ? Double.NaN : bdMax.doubleValue();

          return new double[]{ tot, media, min, max };
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new double[]{ 0d, Double.NaN, Double.NaN, Double.NaN };
  }
}
