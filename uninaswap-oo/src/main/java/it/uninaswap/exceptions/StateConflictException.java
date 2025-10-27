package it.uninaswap.exceptions;

public class StateConflictException extends BusinessException {
  private static final long serialVersionUID = 1L;

  public StateConflictException(String message) {
    super("STATE_CONFLICT", message);
  }

  public static StateConflictException alreadyEvaluated() {
    return new StateConflictException("L'offerta non è più modificabile: è già stata valutata.");
  }

  public static StateConflictException alreadyWithdrawn() {
    return new StateConflictException("L'offerta è già stata ritirata.");
  }

  public static StateConflictException notSeller() {
    return new StateConflictException("Non sei il venditore/proprietario dell'annuncio di questa offerta.");
  }
}
