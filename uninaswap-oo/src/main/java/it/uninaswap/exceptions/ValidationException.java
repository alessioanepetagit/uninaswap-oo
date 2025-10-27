package it.uninaswap.exceptions;

public class ValidationException extends BusinessException {
  private static final long serialVersionUID = 1L;

  public ValidationException(String message) {
    super("VALIDATION", message);
  }

  
  public static ValidationException positivePrice() {
    return new ValidationException("Il prezzo deve essere un valore positivo.");
  }

  public static ValidationException emptyMessage() {
    return new ValidationException("Il messaggio/descrizione non può essere vuoto.");
  }

  public static ValidationException wrongType(String atteso) {
    return new ValidationException("Tipologia annuncio non corretta: atteso " + atteso + ".");
  }

  public static ValidationException notFound(String what) {
    return new ValidationException(what + " non trovato.");
  }

  public static ValidationException priceNotLower(double offerto, double richiesto) {
    return new ValidationException(
        "L'offerta deve essere inferiore al prezzo richiesto (" + richiesto + "€). Offerto: " + offerto + "€."
    );
  }
}
