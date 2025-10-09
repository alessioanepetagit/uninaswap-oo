package it.uninaswap.control;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import it.uninaswap.dao.AnnuncioDAO;
import it.uninaswap.dao.CategoriaDAO;
import it.uninaswap.dao.OffertaDAO;
import it.uninaswap.dao.OggettoDAO;
import it.uninaswap.dao.UtenteDAO;

import it.uninaswap.dao.postgres.AnnuncioDAOPg;
import it.uninaswap.dao.postgres.CategoriaDAOPg;
import it.uninaswap.dao.postgres.OffertaDAOPg;
import it.uninaswap.dao.postgres.OggettoDAOPg;
import it.uninaswap.dao.postgres.ReportDAOPg;
import it.uninaswap.dao.postgres.UtenteDAOPg;

import it.uninaswap.model.Annuncio;
import it.uninaswap.model.Categoria;
import it.uninaswap.model.Offerta;
import it.uninaswap.model.Oggetto;
import it.uninaswap.model.Utente;
import it.uninaswap.model.enums.StatoOfferta;
import it.uninaswap.model.enums.TipoAnnuncio;
import it.uninaswap.model.enums.TipoOfferta;

import it.uninaswap.exceptions.AuthorizationException;
import it.uninaswap.exceptions.BusinessException;
import it.uninaswap.exceptions.StateConflictException;
import it.uninaswap.exceptions.ValidationException;

public class Controller {

  // =========================
  // DAO / Dipendenze
  // =========================
  private final UtenteDAO   utenteDAO    = new UtenteDAOPg();
  private final AnnuncioDAO annuncioDAO  = new AnnuncioDAOPg();
  private final OffertaDAO  offertaDAO   = new OffertaDAOPg();
  private final CategoriaDAO categoriaDAO= new CategoriaDAOPg();
  private final OggettoDAO  oggettoDAO   = new OggettoDAOPg();
  private final ReportDAOPg reportDao    = new ReportDAOPg();

  // =========================
  // Sessione
  // =========================
  private Utente currentUser;

  // =========================
  // Login / Sessione
  // =========================
  public Utente doLogin(String username, String password) {
    if (username == null || password == null) return null;
    String u = username.trim();
    if (u.isEmpty() || password.isEmpty()) return null;
    Utente found = utenteDAO.findByUsernameAndPassword(u, password);
    if (found != null) currentUser = found;
    return found;
  }

  public Utente getCurrentUser() { return currentUser; }

  // =========================
  // Letture base
  // =========================
  public List<Categoria> getCategorie() { return categoriaDAO.findAll(); }

  public List<Annuncio> cercaAnnunci(String categoriaLike, TipoAnnuncio tipo) {
    String cat = (categoriaLike == null || categoriaLike.trim().isEmpty()) ? null : categoriaLike.trim();
    return annuncioDAO.findAttivi(cat, tipo);
  }

  public Annuncio getAnnuncio(int id) { return annuncioDAO.findById(id); }

  public String getNomeOggetto(int oggettoId) {
    Oggetto o = oggettoDAO.findById(oggettoId);
    return (o != null) ? o.getNome() : null;
  }

  public String getNomeUtenteById(int utenteId) {
    Utente u = utenteDAO.findById(utenteId);
    return (u != null) ? u.getUsername() : null;
  }

  // =========================
  // Creazione Annuncio
  // =========================
  /** Crea un annuncio dell’utente loggato. */
  public boolean createAnnuncio(TipoAnnuncio tipo, int categoriaId, int oggettoId,
                                String consegna, String descrizione, Double prezzoVendita)
      throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    if (tipo == null) throw ValidationException.notFound("Tipo annuncio");

    Oggetto obj = oggettoDAO.findById(oggettoId);
    if (obj == null) throw ValidationException.notFound("Oggetto");

    if (obj.getProprietarioId() != currentUser.getId())
      throw new AuthorizationException("Puoi creare annunci solo con oggetti che possiedi.");
    if (!obj.isDisponibile())
      throw new ValidationException("L'oggetto selezionato non è disponibile.");

    if (tipo == TipoAnnuncio.AnnuncioVendita) {
      if (prezzoVendita == null || prezzoVendita <= 0)
        throw ValidationException.positivePrice();
    } else {
      prezzoVendita = null; // obbligatorio null per scambio/regalo
    }

    if (consegna == null || consegna.trim().isEmpty())
      consegna = "Consegna da concordare";

    // se non viene passata/è vuota uso una descrizione di default
    String finalDescr = (descrizione != null && !descrizione.trim().isEmpty())
        ? descrizione.trim()
        : defaultDescrizioneFor(tipo, obj);

    Annuncio toSave = new Annuncio();
    toSave.setTipo(tipo);
    toSave.setCategoriaId(categoriaId);
    toSave.setOggettoId(oggettoId);
    toSave.setAutoreId(currentUser.getId());
    toSave.setModalitaConsegna(consegna.trim());
    toSave.setPrezzoRichiesto(prezzoVendita);
    toSave.setDescrizione(finalDescr);

    return annuncioDAO.create(toSave) != null;
  }

  private String defaultDescrizioneFor(TipoAnnuncio tipo, Oggetto obj) {
    String nome = (obj != null && obj.getNome()!=null) ? obj.getNome() : "oggetto";
    switch (tipo) {
      case AnnuncioVendita: return "Vendo " + nome;
      case AnnuncioScambio: return "Scambio " + nome;
      case AnnuncioRegalo:  return "Regalo " + nome;
      default: return nome;
    }
  }

  /** Oggetti dell’utente corrente che risultano disponibili. */
  public List<Oggetto> getMieiOggettiDisponibili() throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    return oggettoDAO.findDisponibiliByUtente(currentUser.getId());
  }

  // =========================
  // Invio Offerte
  // =========================

  /** Offerta al ribasso: richiede prezzo < richiesto. */
  public boolean inviaOffertaVendita(int annuncioId, double prezzo) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    if (prezzo <= 0) throw ValidationException.positivePrice();

    Annuncio a = annuncioDAO.findById(annuncioId);
    if (a == null) throw ValidationException.notFound("Annuncio");
    if (a.getAutoreId() == currentUser.getId()) throw AuthorizationException.ownListing();
    if (a.getTipo() != TipoAnnuncio.AnnuncioVendita) throw ValidationException.wrongType("vendita");
    if (a.getPrezzoRichiesto() == null) throw ValidationException.notFound("Prezzo richiesto");

    if (!(prezzo < a.getPrezzoRichiesto()))
      throw ValidationException.priceNotLower(prezzo, a.getPrezzoRichiesto());

    Offerta o = new Offerta();
    o.setAnnuncioId(annuncioId);
    o.setOfferenteId(currentUser.getId());
    o.setTipo(TipoOfferta.OffertaVendita);
    o.setPrezzoProposto(prezzo);
    o.setStato(StatoOfferta.InAttesa);
    o.setDataOraInvio(LocalDateTime.now());
    return offertaDAO.create(o) != null;
  }

  /** Accetta il prezzo richiesto (nessun check “<”). */
  public boolean accettaPrezzoRichiesto(int annuncioId) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();

    Annuncio a = annuncioDAO.findById(annuncioId);
    if (a == null) throw ValidationException.notFound("Annuncio");
    if (a.getAutoreId() == currentUser.getId()) throw AuthorizationException.ownListing();
    if (a.getTipo() != TipoAnnuncio.AnnuncioVendita) throw ValidationException.wrongType("vendita");
    if (a.getPrezzoRichiesto() == null) throw ValidationException.notFound("Prezzo richiesto");

    Offerta o = new Offerta();
    o.setAnnuncioId(annuncioId);
    o.setOfferenteId(currentUser.getId());
    o.setTipo(TipoOfferta.OffertaVendita);
    o.setPrezzoProposto(a.getPrezzoRichiesto());
    o.setStato(StatoOfferta.InAttesa);
    o.setDataOraInvio(LocalDateTime.now());
    return offertaDAO.create(o) != null;
  }

  public boolean inviaOffertaScambio(int annuncioId, String proposta) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    if (proposta == null || proposta.trim().isEmpty()) throw ValidationException.emptyMessage();

    Annuncio a = annuncioDAO.findById(annuncioId);
    if (a == null) throw ValidationException.notFound("Annuncio");
    if (a.getAutoreId() == currentUser.getId()) throw AuthorizationException.ownListing();
    if (a.getTipo() != TipoAnnuncio.AnnuncioScambio) throw ValidationException.wrongType("scambio");

    Offerta o = new Offerta();
    o.setAnnuncioId(annuncioId);
    o.setOfferenteId(currentUser.getId());
    o.setTipo(TipoOfferta.OffertaScambio);
    o.setMessaggioMotivazionale(proposta.trim());
    o.setStato(StatoOfferta.InAttesa);
    o.setDataOraInvio(LocalDateTime.now());
    return offertaDAO.create(o) != null;
  }

  public boolean inviaOffertaRegalo(int annuncioId, String messaggio) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    if (messaggio == null || messaggio.trim().isEmpty()) throw ValidationException.emptyMessage();

    Annuncio a = annuncioDAO.findById(annuncioId);
    if (a == null) throw ValidationException.notFound("Annuncio");
    if (a.getAutoreId() == currentUser.getId()) throw AuthorizationException.ownListing();
    if (a.getTipo() != TipoAnnuncio.AnnuncioRegalo) throw ValidationException.wrongType("regalo");

    Offerta o = new Offerta();
    o.setAnnuncioId(annuncioId);
    o.setOfferenteId(currentUser.getId());
    o.setTipo(TipoOfferta.OffertaRegalo);
    o.setMessaggioMotivazionale(messaggio.trim());
    o.setStato(StatoOfferta.InAttesa);
    o.setDataOraInvio(LocalDateTime.now());
    return offertaDAO.create(o) != null;
  }

  // =========================
  // Modifica / Ritiro / Decisione
  // =========================
  public boolean modificaOfferta(Offerta o) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    if (o == null) throw ValidationException.notFound("Offerta");
    boolean ok = offertaDAO.update(o);
    if (!ok) throw StateConflictException.alreadyEvaluated();
    return true;
  }

  public boolean ritiraOfferta(int offertaId) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    boolean ok = offertaDAO.withdraw(offertaId, currentUser.getId());
    if (!ok) throw StateConflictException.alreadyEvaluated();
    return true;
  }

  public List<Offerta> getStoricoOfferte() {
    if (currentUser == null) return Collections.emptyList();
    return offertaDAO.findByUser(currentUser.getId());
  }

  public List<Offerta> getOfferteRicevute() {
    if (currentUser == null) return Collections.emptyList();
    return offertaDAO.findRicevuteByVenditore(currentUser.getId());
  }

  public boolean accettaOffertaRicevuta(int offertaId) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    boolean ok = offertaDAO.sellerAccept(offertaId, currentUser.getId());
    if (!ok) throw StateConflictException.alreadyEvaluated();
    return true;
  }

  public boolean rifiutaOffertaRicevuta(int offertaId) throws BusinessException {
    if (currentUser == null) throw AuthorizationException.notAuthenticated();
    boolean ok = offertaDAO.sellerReject(offertaId, currentUser.getId());
    if (!ok) throw StateConflictException.alreadyEvaluated();
    return true;
  }

  // =========================
  // Report (solo int + StatsPrezzi)
  // =========================
  public int reportTotVendita()   { return (currentUser == null) ? 0 : reportDao.countTotVendita(currentUser.getId()); }
  public int reportTotScambio()   { return (currentUser == null) ? 0 : reportDao.countTotScambio(currentUser.getId()); }
  public int reportTotRegalo()    { return (currentUser == null) ? 0 : reportDao.countTotRegalo(currentUser.getId()); }
  public int reportAccVendita()   { return (currentUser == null) ? 0 : reportDao.countAccVendita(currentUser.getId()); }
  public int reportAccScambio()   { return (currentUser == null) ? 0 : reportDao.countAccScambio(currentUser.getId()); }
  public int reportAccRegalo()    { return (currentUser == null) ? 0 : reportDao.countAccRegalo(currentUser.getId()); }
  public int reportTotaleOfferte(){ return reportTotVendita() + reportTotScambio() + reportTotRegalo(); }
//--- Vendite accettate: statistiche senza DTO ---

public int reportVenditeAccettateTot() {
 return (currentUser == null) ? 0 : reportDao.countVenditeAccettate(currentUser.getId());
}

public Double reportVenditeAccettateMedia() {
 return (currentUser == null) ? null : reportDao.avgVenditeAccettate(currentUser.getId());
}

public Double reportVenditeAccettateMin() {
 return (currentUser == null) ? null : reportDao.minVenditeAccettate(currentUser.getId());
}

public Double reportVenditeAccettateMax() {
 return (currentUser == null) ? null : reportDao.maxVenditeAccettate(currentUser.getId());
}

}
