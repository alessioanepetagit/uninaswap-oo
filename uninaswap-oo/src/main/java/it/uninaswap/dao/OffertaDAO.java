package it.uninaswap.dao;

import it.uninaswap.model.Offerta;

import java.util.List;

/**
 * Data Access Object per le Offerte.
 *
 * Nota sui contratti:
 * - I metodi qui NON lanciano SQLException: gli implementativi dovrebbero
 *   incapsulare problemi tecnici in un'InfrastructureException (runtime).
 * - Le regole di dominio (es. "solo InAttesa", "solo proprietario/offerente")
 *   sono applicate nell'implementazione tramite WHERE protettive.
 */
public interface OffertaDAO {

  /**
   * Inserisce una nuova offerta e restituisce l'ID generato.
   * @param offerta offerta da inserire (richiede: tipo, stato, idAnnuncio, idUtente; prezzo/messaggio opzionali)
   * @return ID generato oppure null se non inserita
   */
  Integer create(Offerta offerta);

  /**
   * Aggiorna i campi modificabili di un'offerta (solo se InAttesa e di proprietà dell'offerente).
   * Campi tipici: messaggio motivazionale e/o prezzo proposto.
   * @param offerta offerta con id, offerenteId e nuovi valori
   * @return true se esattamente una riga è stata aggiornata
   */
  boolean update(Offerta offerta);

  /**
   * Ritira (cancella) un'offerta dell'utente corrente, solo se InAttesa.
   * @param offertaId id offerta
   * @param offerenteId id utente che ha inviato l'offerta
   * @return true se l'offerta è stata rimossa
   */
  boolean withdraw(int offertaId, int offerenteId);

  /**
   * Storico delle offerte inviate da un utente.
   * @param userId id utente
   * @return lista ordinata (es. per data invio desc)
   */
  List<Offerta> findByUser(int userId);

  /**
   * Offerte ricevute dal venditore: tutte le offerte su annunci di sua proprietà.
   * @param venditoreId id del proprietario degli annunci
   * @return lista di offerte ordinate (es. per data invio desc)
   */
  List<Offerta> findRicevuteByVenditore(int venditoreId);

  /**
   * Accetta un'offerta ricevuta su un proprio annuncio.
   * Valida a livello SQL: agisce solo se l'offerta è InAttesa e l'annuncio appartiene al venditore.
   * @param offertaId id offerta
   * @param venditoreId id proprietario dell'annuncio
   * @return true se lo stato è passato ad 'Accettata'
   */
  boolean sellerAccept(int offertaId, int venditoreId);

  /**
   * Rifiuta un'offerta ricevuta su un proprio annuncio.
   * Valida a livello SQL: agisce solo se l'offerta è InAttesa e l'annuncio appartiene al venditore.
   * @param offertaId id offerta
   * @param venditoreId id proprietario dell'annuncio
   * @return true se lo stato è passato a 'Rifiutata'
   */
  boolean sellerReject(int offertaId, int venditoreId);

  /**
   * Recupera un'offerta per id (utility).
   * @param id id offerta
   * @return offerta o null se non trovata
   */
  Offerta findById(int id);
}
