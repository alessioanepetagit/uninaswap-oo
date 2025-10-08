package it.uninaswap.dao;

import it.uninaswap.model.Oggetto;
import java.util.List;

public interface OggettoDAO {
  Oggetto findById(int id);

  // NUOVO: lista oggetti disponibili dell’utente
  List<Oggetto> findDisponibiliByUtente(int userId);
}
