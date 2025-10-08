package it.uninaswap.dao.postgres;

import it.uninaswap.dao.DBConnection;
import it.uninaswap.dao.OffertaDAO;
import it.uninaswap.model.Offerta;
import it.uninaswap.model.enums.StatoOfferta;
import it.uninaswap.model.enums.TipoOfferta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffertaDAOPg implements OffertaDAO {

  private static final String BASE_SELECT =
      "SELECT IDOfferta, StatoOfferta, DataOraInvio, Tipo, MessaggioMotivazionale, PrezzoProposto, IDUtente, IDAnnuncio " +
      "FROM OFFERTA ";

  private static final String SQL_INSERT =
      "INSERT INTO OFFERTA (StatoOfferta, DataOraInvio, Tipo, MessaggioMotivazionale, PrezzoProposto, IDUtente, IDAnnuncio) " +
      "VALUES (?,?,?,?,?,?,?) RETURNING IDOfferta";

  private static final String SQL_UPDATE =
      "UPDATE OFFERTA SET StatoOfferta=?, MessaggioMotivazionale=?, PrezzoProposto=? " +
      "WHERE IDOfferta=? AND StatoOfferta='InAttesa'";

  private static final String SQL_WITHDRAW =
      "UPDATE OFFERTA SET StatoOfferta='Rifiutata' " +
      "WHERE IDOfferta=? AND IDUtente=? AND StatoOfferta='InAttesa'";

  private static final String SQL_BY_USER =
      BASE_SELECT + "WHERE IDUtente=? ORDER BY DataOraInvio DESC";



  private static final String SQL_SELLER_ACCEPT =
      "UPDATE OFFERTA o SET StatoOfferta='Accettata' " +
      "FROM ANNUNCIO a " +
      "WHERE o.IDOfferta=? AND o.IDAnnuncio=a.IDAnnuncio AND a.IDUtente=? AND o.StatoOfferta='InAttesa'";

  private static final String SQL_SELLER_REJECT =
      "UPDATE OFFERTA o SET StatoOfferta='Rifiutata' " +
      "FROM ANNUNCIO a " +
      "WHERE o.IDOfferta=? AND o.IDAnnuncio=a.IDAnnuncio AND a.IDUtente=? AND o.StatoOfferta='InAttesa'";

  private static final String SQL_BY_ID =
      BASE_SELECT + "WHERE IDOfferta=?";

  @Override
  public Offerta findById(int id) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_BY_ID)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapRow(rs);
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
  }

  @Override
  public Integer create(Offerta o) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
      ps.setString(1, o.getStato().name());
      ps.setTimestamp(2, Timestamp.valueOf(o.getDataOraInvio()));
      ps.setString(3, o.getTipo().name());
      ps.setString(4, o.getMessaggioMotivazionale());
      if (o.getPrezzoProposto() == null) ps.setNull(5, Types.NUMERIC);
      else ps.setBigDecimal(5, java.math.BigDecimal.valueOf(o.getPrezzoProposto()));
      ps.setInt(6, o.getOfferenteId());
      ps.setInt(7, o.getAnnuncioId());

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int id = rs.getInt(1);
          o.setId(id); // opzionale
          return id;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean update(Offerta o) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
      ps.setString(1, o.getStato().name());
      ps.setString(2, o.getMessaggioMotivazionale());
      if (o.getPrezzoProposto() == null) ps.setNull(3, Types.NUMERIC);
      else ps.setBigDecimal(3, java.math.BigDecimal.valueOf(o.getPrezzoProposto()));
      ps.setInt(4, o.getId());
      return ps.executeUpdate() == 1;
    } catch (SQLException e) { e.printStackTrace(); }
    return false;
  }

  @Override
  public boolean withdraw(int offertaId, int userId) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_WITHDRAW)) {
      ps.setInt(1, offertaId);
      ps.setInt(2, userId);
      return ps.executeUpdate() == 1;
    } catch (SQLException e) { e.printStackTrace(); }
    return false;
  }

  @Override
  public List<Offerta> findByUser(int userId) {
    List<Offerta> out = new ArrayList<>();
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_BY_USER)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) out.add(mapRow(rs));
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return out;
  }

  @Override
  public List<Offerta> findRicevuteByVenditore(int venditoreId) {
    List<Offerta> out = new ArrayList<>();
    String sql =
        "SELECT o.IDOfferta, o.StatoOfferta, o.DataOraInvio, o.Tipo, o.MessaggioMotivazionale, " +
        "       o.PrezzoProposto, o.IDUtente, o.IDAnnuncio " +
        "FROM OFFERTA o JOIN ANNUNCIO a ON a.IDAnnuncio=o.IDAnnuncio " +
        "WHERE a.IDUtente=? ORDER BY o.DataOraInvio DESC";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, venditoreId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) out.add(mapRow(rs));
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return out;
  }

  @Override
  public boolean sellerAccept(int offertaId, int venditoreId) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_SELLER_ACCEPT)) {
      ps.setInt(1, offertaId);
      ps.setInt(2, venditoreId);
      return ps.executeUpdate() == 1;
    } catch (SQLException e) { e.printStackTrace(); }
    return false;
  }

  @Override
  public boolean sellerReject(int offertaId, int venditoreId) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_SELLER_REJECT)) {
      ps.setInt(1, offertaId);
      ps.setInt(2, venditoreId);
      return ps.executeUpdate() == 1;
    } catch (SQLException e) { e.printStackTrace(); }
    return false;
  }

  private Offerta mapRow(ResultSet rs) throws SQLException {
    Offerta o = new Offerta();
    o.setId(rs.getInt("IDOfferta"));
    o.setStato(StatoOfferta.valueOf(rs.getString("StatoOfferta")));
    Timestamp ts = rs.getTimestamp("DataOraInvio");
    o.setDataOraInvio(ts == null ? null : ts.toLocalDateTime());
    o.setTipo(TipoOfferta.valueOf(rs.getString("Tipo")));
    o.setMessaggioMotivazionale(rs.getString("MessaggioMotivazionale"));
    double p = rs.getDouble("PrezzoProposto");
    o.setPrezzoProposto(rs.wasNull() ? null : p);
    o.setOfferenteId(rs.getInt("IDUtente"));
    o.setAnnuncioId(rs.getInt("IDAnnuncio"));
    return o;
  }
}
