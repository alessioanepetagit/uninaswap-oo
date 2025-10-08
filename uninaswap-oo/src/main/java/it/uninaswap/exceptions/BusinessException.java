package it.uninaswap.exceptions;

public class BusinessException extends Exception {
  private static final long serialVersionUID = 1L;

  // opzionale: codice/chiave errore (per logging/UI)
  private final String code;

  public BusinessException() {
    super();
    this.code = null;
  }

  public BusinessException(String message) {
    super(message);
    this.code = null;
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
    this.code = null;
  }

  public BusinessException(Throwable cause) {
    super(cause);
    this.code = null;
  }

  /** Nuovo: costruttore a due stringhe usato da altre classi */
  public BusinessException(String code, String message) {
    super(message != null ? message : code);
    this.code = code;
  }

  public String getCode() { return code; }
}
