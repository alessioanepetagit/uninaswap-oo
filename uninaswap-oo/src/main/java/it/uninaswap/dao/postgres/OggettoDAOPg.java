package it.uninaswap.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.uninaswap.dao.OggettoDAO;
import it.uninaswap.dbconnection.DBConnection;
import it.uninaswap.model.Oggetto;

public class OggettoDAOPg implements OggettoDAO {

  private static final String SQL_BY_ID =
      "SELECT IDOggetto, Nome, DescrizioneDettagliata, StatoConservazione, Disponibilita, IDUtente " +
      "  FROM OGGETTO " +
      " WHERE IDOggetto = ?";

  private static final String SQL_DISP_BY_USER =
      "SELECT IDOggetto, Nome, DescrizioneDettagliata, StatoConservazione, Disponibilita, IDUtente " +
      "  FROM OGGETTO " +
      " WHERE IDUtente = ? AND Disponibilita = TRUE " +
      " ORDER BY Nome";

  @Override
  public Oggetto findById(int id) {
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
  public List<Oggetto> findDisponibiliByUtente(int idUtente) {
    List<Oggetto> out = new ArrayList<>();
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_DISP_BY_USER)) {
      ps.setInt(1, idUtente);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) out.add(mapRow(rs));
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return out;
  }

  private Oggetto mapRow(ResultSet rs) throws SQLException {
    Oggetto o = new Oggetto();
    o.setId(rs.getInt("IDOggetto"));
    o.setNome(rs.getString("Nome"));
    o.setDescrizione(rs.getString("DescrizioneDettagliata"));
    o.setStatoConservazione(rs.getString("StatoConservazione"));
    o.setDisponibile(rs.getBoolean("Disponibilita"));
    o.setProprietarioId(rs.getInt("IDUtente"));
    return o;
  }
}
