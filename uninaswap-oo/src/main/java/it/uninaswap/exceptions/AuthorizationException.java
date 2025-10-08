package it.uninaswap.exceptions;

public class AuthorizationException extends BusinessException {
  private static final long serialVersionUID = 1L;

  public AuthorizationException(String message) { super(message); }

  public static AuthorizationException notAuthenticated() {
    return new AuthorizationException("Devi effettuare il login.");
  }

  public static AuthorizationException ownListing() {
    return new AuthorizationException("Non puoi fare offerte sul tuo annuncio.");
  }

  public static AuthorizationException forbidden(String msg) {
    return new AuthorizationException(
        (msg == null || msg.trim().isEmpty()) ? "Operazione non autorizzata." : msg.trim()
    );
  }
}
