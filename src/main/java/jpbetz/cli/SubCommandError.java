package jpbetz.cli;

public class SubCommandError extends Exception {
  private static final long serialVersionUID = 1L;
  
  public SubCommandError(String message) {
  	super(message);
  }
  
  public SubCommandError(Throwable cause) {
  	super(cause);
  }
  
  public SubCommandError(String message, Throwable cause) {
  	super(message, cause);
  }
}
