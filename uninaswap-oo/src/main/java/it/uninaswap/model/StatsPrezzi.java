package it.uninaswap.dao;

public class StatsPrezzi {
  private int totale;
  private Double media;
  private Double minimo;
  private Double massimo;

  public int getTotale() { return totale; }
  public void setTotale(int totale) { this.totale = totale; }
  public Double getMedia() { return media; }
  public void setMedia(Double media) { this.media = media; }
  public Double getMinimo() { return minimo; }
  public void setMinimo(Double minimo) { this.minimo = minimo; }
  public Double getMassimo() { return massimo; }
  public void setMassimo(Double massimo) { this.massimo = massimo; }
}
