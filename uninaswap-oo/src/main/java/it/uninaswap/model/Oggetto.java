package it.uninaswap.model;

public class Oggetto {
  private int id;
  private String nome;
  private String descrizione;
  private String statoConservazione;
  private boolean disponibile;
  private int proprietarioId;

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getDescrizione() { return descrizione; }
  public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
  public String getStatoConservazione() { return statoConservazione; }
  public void setStatoConservazione(String statoConservazione) { this.statoConservazione = statoConservazione; }
  public boolean isDisponibile() { return disponibile; }
  public void setDisponibile(boolean disponibile) { this.disponibile = disponibile; }
  public int getProprietarioId() { return proprietarioId; }
  public void setProprietarioId(int proprietarioId) { this.proprietarioId = proprietarioId; }

  @Override
  public String toString() {
    return (nome != null && !nome.isEmpty()) ? nome : ("Oggetto #" + id);
  }
}
