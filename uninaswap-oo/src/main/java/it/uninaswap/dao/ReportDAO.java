package it.uninaswap.dao;

public interface ReportDAO {
  // Totali offerte inviate per tipologia
  int countTotVendita(int userId);
  int countTotScambio(int userId);
  int countTotRegalo (int userId);

  // Accettate per tipologia (tra quelle inviate)
  int countAccVendita(int userId);
  int countAccScambio(int userId);
  int countAccRegalo (int userId);

  // Statistiche vendite accettate (delle offerte inviate dall'utente)
  int    countVenditeAccettate(int userId); // = COUNT(*)
  Double avgVenditeAccettate  (int userId); // può essere null
  Double minVenditeAccettate  (int userId); // può essere null
  Double maxVenditeAccettate  (int userId); // può essere null
}
