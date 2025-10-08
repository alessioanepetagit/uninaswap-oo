package it.uninaswap.dao;

import it.uninaswap.model.Utente;

public interface UtenteDAO {
  Utente findByUsernameAndPassword(String username, String password);

  // ⬇️ AGGIUNGI QUESTO
  Utente findById(int id);
}
