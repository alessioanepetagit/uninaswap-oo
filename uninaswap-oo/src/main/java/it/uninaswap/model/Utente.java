package it.uninaswap.model;

public class Utente {
  private int id;
  private String email;
  private String username;
  private String nome;
  private String cognome;

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getCognome() { return cognome; }
  public void setCognome(String cognome) { this.cognome = cognome; }
}
