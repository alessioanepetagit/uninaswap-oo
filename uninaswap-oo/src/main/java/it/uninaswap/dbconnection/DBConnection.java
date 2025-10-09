package it.uninaswap.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
	private static final String URL  = "jdbc:postgresql://localhost:5432/uninaswap";
	private static final String USER = "AlessioAnepeta";
	private static final String PASS = "ciao123";


  private DBConnection() {}

  public static Connection getConnection() throws SQLException {
    // Se richiesto dagli esempi del prof:
    // try { Class.forName("org.postgresql.Driver"); } catch (ClassNotFoundException ignored) {}
    return DriverManager.getConnection(URL, USER, PASS);
  }
}
