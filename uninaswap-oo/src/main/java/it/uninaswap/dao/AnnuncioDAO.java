package it.uninaswap.dao;

import java.util.List;
import it.uninaswap.model.Annuncio;
import it.uninaswap.model.enums.TipoAnnuncio;

public interface AnnuncioDAO {
  List<Annuncio> findAttivi(String categoriaLike, TipoAnnuncio tipo);
  Annuncio findById(int id);


  Annuncio create(Annuncio a);
}
