package it.uninaswap.exceptions;
/** Errori tecnici/di infrastruttura (non mostrabili in chiaro all’utente). */
public class InfrastructureException extends RuntimeException {
	private static final long serialVersionUID = 1L;

  private final String code;
  public InfrastructureException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
  public String getCode() { return code; }

  public static InfrastructureException db(String message, Throwable cause) {
    return new InfrastructureException("DB_ERROR", message, cause);
  }
}