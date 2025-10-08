package it.uninaswap.dao.postgres;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import it.uninaswap.dao.DBConnection;
import it.uninaswap.dao.AnnuncioDAO;
import it.uninaswap.model.Annuncio;
import it.uninaswap.model.enums.StatoAnnuncio;
import it.uninaswap.model.enums.TipoAnnuncio;

public class AnnuncioDAOPg implements AnnuncioDAO {

  private static final String SQL_FIND_ATTIVI =
      "SELECT a.IDAnnuncio, a.ModalitaConsegna, a.Stato, a.Tipo, a.PrezzoRichiesto, " +
      "       a.DataCreazione, a.Descrizione, a.IDUtente, a.IDCategoria, a.IDOggetto " +
      "  FROM ANNUNCIO a " +
      "  JOIN CATEGORIA c ON c.IDCategoria = a.IDCategoria " +
      " WHERE a.Stato = 'Attivo' " +
      "   AND (? IS NULL OR c.NomeCategoria ILIKE ?) " +
      "   AND (? IS NULL OR a.Tipo = ?) " +
      " ORDER BY a.DataCreazione DESC";

  private static final String SQL_FIND_BY_ID =
      "SELECT IDAnnuncio, ModalitaConsegna, Stato, Tipo, PrezzoRichiesto, " +
      "       DataCreazione, Descrizione, IDUtente, IDCategoria, IDOggetto " +
      "  FROM ANNUNCIO " +
      " WHERE IDAnnuncio = ?";

  private static final String SQL_INSERT =
      "INSERT INTO ANNUNCIO (ModalitaConsegna, Stato, Tipo, PrezzoRichiesto, DataCreazione, Descrizione, IDUtente, IDCategoria, IDOggetto) " +
      "VALUES (?,?,?,?,?,?,?,?,?) RETURNING IDAnnuncio";

  @Override
  public List<Annuncio> findAttivi(String categoriaLike, TipoAnnuncio tipo) {
    List<Annuncio> out = new ArrayList<>();
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_FIND_ATTIVI)) {
      ps.setString(1, categoriaLike);
      ps.setString(2, categoriaLike == null ? null : "%" + categoriaLike + "%");
      ps.setString(3, tipo == null ? null : tipo.name());
      ps.setString(4, tipo == null ? null : tipo.name());
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) out.add(mapRow(rs));
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return out;
  }

  @Override
  public Annuncio findById(int id) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapRow(rs);
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
  }

  @Override
  public Annuncio create(Annuncio a) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
      ps.setString(1, a.getModalitaConsegna());
      ps.setString(2, a.getStato() == null ? StatoAnnuncio.Attivo.name() : a.getStato().name());
      ps.setString(3, a.getTipo().name());
      if (a.getPrezzoRichiesto() == null) ps.setNull(4, Types.NUMERIC); else ps.setDouble(4, a.getPrezzoRichiesto());
      ps.setDate(5, a.getDataCreazione() == null ? new Date(System.currentTimeMillis()) : Date.valueOf(a.getDataCreazione()));
      ps.setString(6, a.getDescrizione());
      ps.setInt(7, a.getAutoreId());
      ps.setInt(8, a.getCategoriaId());
      ps.setInt(9, a.getOggettoId());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          a.setId(rs.getInt(1));
          return a;
        }
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
  }

  private Annuncio mapRow(ResultSet rs) throws SQLException {
    Annuncio a = new Annuncio();
    a.setId(rs.getInt("IDAnnuncio"));
    a.setModalitaConsegna(rs.getString("ModalitaConsegna"));
    a.setStato(StatoAnnuncio.valueOf(rs.getString("Stato")));
    a.setTipo(TipoAnnuncio.valueOf(rs.getString("Tipo")));
    double pr = rs.getDouble("PrezzoRichiesto");
    a.setPrezzoRichiesto(rs.wasNull() ? null : pr);
    Date d = rs.getDate("DataCreazione");
    a.setDataCreazione(d == null ? null : d.toLocalDate());
    a.setDescrizione(rs.getString("Descrizione"));
    a.setAutoreId(rs.getInt("IDUtente"));
    a.setCategoriaId(rs.getInt("IDCategoria"));
    a.setOggettoId(rs.getInt("IDOggetto"));
    return a;
  }
}
