package it.uninaswap.model;

import java.time.LocalDate;
import it.uninaswap.model.enums.StatoAnnuncio;
import it.uninaswap.model.enums.TipoAnnuncio;

public class Annuncio {
  private int id;
  private String modalitaConsegna;
  private StatoAnnuncio stato;
  private TipoAnnuncio tipo;
  private Double prezzoRichiesto;
  private LocalDate dataCreazione;
  private String descrizione;
  private int autoreId;
  private int categoriaId;
  private int oggettoId;

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getModalitaConsegna() { return modalitaConsegna; }
  public void setModalitaConsegna(String modalitaConsegna) { this.modalitaConsegna = modalitaConsegna; }

  public StatoAnnuncio getStato() { return stato; }
  public void setStato(StatoAnnuncio stato) { this.stato = stato; }

  public TipoAnnuncio getTipo() { return tipo; }
  public void setTipo(TipoAnnuncio tipo) { this.tipo = tipo; }

  public Double getPrezzoRichiesto() { return prezzoRichiesto; }
  public void setPrezzoRichiesto(Double prezzoRichiesto) { this.prezzoRichiesto = prezzoRichiesto; }

  public LocalDate getDataCreazione() { return dataCreazione; }
  public void setDataCreazione(LocalDate dataCreazione) { this.dataCreazione = dataCreazione; }

  public String getDescrizione() { return descrizione; }
  public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

  public int getAutoreId() { return autoreId; }
  public void setAutoreId(int autoreId) { this.autoreId = autoreId; }

  public int getCategoriaId() { return categoriaId; }
  public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

  public int getOggettoId() { return oggettoId; }
  public void setOggettoId(int oggettoId) { this.oggettoId = oggettoId; }

  @Override
  public String toString() {
    return "[" + id + "] " + tipo + " - " + descrizione;
  }
}
