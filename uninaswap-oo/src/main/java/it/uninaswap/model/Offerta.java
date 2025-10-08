package it.uninaswap.model;

import java.time.LocalDateTime;
import it.uninaswap.model.enums.StatoOfferta;
import it.uninaswap.model.enums.TipoOfferta;

public class Offerta {
  private int id;
  private StatoOfferta stato;
  private LocalDateTime dataOraInvio;
  private TipoOfferta tipo;
  private String messaggioMotivazionale;
  private Double prezzoProposto;
  private int offerenteId;
  private int annuncioId;

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public StatoOfferta getStato() { return stato; }
  public void setStato(StatoOfferta stato) { this.stato = stato; }
  public LocalDateTime getDataOraInvio() { return dataOraInvio; }
  public void setDataOraInvio(LocalDateTime dataOraInvio) { this.dataOraInvio = dataOraInvio; }
  public TipoOfferta getTipo() { return tipo; }
  public void setTipo(TipoOfferta tipo) { this.tipo = tipo; }
  public String getMessaggioMotivazionale() { return messaggioMotivazionale; }
  public void setMessaggioMotivazionale(String messaggioMotivazionale) { this.messaggioMotivazionale = messaggioMotivazionale; }
  public Double getPrezzoProposto() { return prezzoProposto; }
  public void setPrezzoProposto(Double prezzoProposto) { this.prezzoProposto = prezzoProposto; }
  public int getOfferenteId() { return offerenteId; }
  public void setOfferenteId(int offerenteId) { this.offerenteId = offerenteId; }
  public int getAnnuncioId() { return annuncioId; }
  public void setAnnuncioId(int annuncioId) { this.annuncioId = annuncioId; }
}
