package it.uninaswap.dao;

import java.util.Map;
import it.uninaswap.model.enums.TipoOfferta;

public interface ReportDAO {
  Map<TipoOfferta, Long> countOfferteByTipo(int userId);
  Map<TipoOfferta, Long> countAccettateByTipo(int userId);
  StatsPrezzi statsVenditeAccettate(int userId);
}
