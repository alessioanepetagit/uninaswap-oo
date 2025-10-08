package it.uninaswap.dao;

import java.util.List;
import it.uninaswap.model.Categoria;

public interface CategoriaDAO {
  List<Categoria> findAll();
}
