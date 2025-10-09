package it.uninaswap.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.uninaswap.dbconnection.DBConnection;


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



  
  // ------------------ API public (solo int / StatsPrezzi) ------------------

  public int countTotVendita(int userId) { return scalarCount(SQL_TOT_VENDITA, userId); }
  public int countTotScambio(int userId) { return scalarCount(SQL_TOT_SCAMBIO, userId); }
  public int countTotRegalo (int userId) { return scalarCount(SQL_TOT_REGALO , userId); }

  public int countAccVendita(int userId) { return scalarCount(SQL_ACC_VENDITA, userId); }
  public int countAccScambio(int userId) { return scalarCount(SQL_ACC_SCAMBIO, userId); }
  public int countAccRegalo (int userId) { return scalarCount(SQL_ACC_REGALO , userId); }

  private static final String SQL_STATS_VENDITE =
		    "SELECT COUNT(*) AS tot, AVG(o.PrezzoProposto) AS media, " +
		    "       MIN(o.PrezzoProposto) AS minimo, MAX(o.PrezzoProposto) AS massimo " +
		    "FROM OFFERTA o " +
		    "JOIN ANNUNCIO a ON a.IDAnnuncio = o.IDAnnuncio " +
		    "WHERE o.StatoOfferta = 'Accettata' " +
		    "  AND o.Tipo = 'OffertaVendita' " +
		    "  AND o.IDUtente = ?";  // offerte fatte dall'utente corrente

		// helper: una sola query, poi accessor per ogni metrica
		private double[] fetchVenditeAccettateStats(int userId) {
		  try (Connection con = DBConnection.getConnection();
		       PreparedStatement ps = con.prepareStatement(SQL_STATS_VENDITE)) {
		    ps.setInt(1, userId);
		    try (ResultSet rs = ps.executeQuery()) {
		      if (rs.next()) {
		        double tot   = rs.getDouble("tot");    // COUNT(*)
		        double media = rs.getDouble("media");  // può essere NULL -> 0.0 con wasNull check
		        double min   = rs.getDouble("minimo");
		        double max   = rs.getDouble("massimo");
		        // gestisci NULL di media/min/max
		        if (rs.wasNull()) media = Double.NaN;
		        // rs.wasNull() si riferisce all'ultima get*: per sicurezza puoi testare ogni campo separatamente
		        return new double[]{ tot, media, min, max };
		      }
		    }
		  } catch (SQLException e) { e.printStackTrace(); }
		  return new double[]{ 0d, Double.NaN, Double.NaN, Double.NaN };
		}

		public int countVenditeAccettate(int userId) {
		  return (int) fetchVenditeAccettateStats(userId)[0];
		}

		public Double avgVenditeAccettate(int userId) {
		  double v = fetchVenditeAccettateStats(userId)[1];
		  return Double.isNaN(v) ? null : v;
		}

		public Double minVenditeAccettate(int userId) {
		  double v = fetchVenditeAccettateStats(userId)[2];
		  return Double.isNaN(v) ? null : v;
		}

		public Double maxVenditeAccettate(int userId) {
		  double v = fetchVenditeAccettateStats(userId)[3];
		  return Double.isNaN(v) ? null : v;
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
