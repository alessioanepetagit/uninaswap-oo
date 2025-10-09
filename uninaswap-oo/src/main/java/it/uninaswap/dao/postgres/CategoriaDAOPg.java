package it.uninaswap.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.uninaswap.dao.CategoriaDAO;
import it.uninaswap.dbconnection.DBConnection;
import it.uninaswap.model.Categoria;

public class CategoriaDAOPg implements CategoriaDAO {

  private static final String SQL_ALL =
      "SELECT IDCategoria, NomeCategoria " +
      "  FROM CATEGORIA " +
      " ORDER BY NomeCategoria";

  @Override
  public List<Categoria> findAll() {
    List<Categoria> out = new ArrayList<>();
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL_ALL);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        out.add(new Categoria(rs.getInt("IDCategoria"), rs.getString("NomeCategoria")));
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return out;
  }
}
