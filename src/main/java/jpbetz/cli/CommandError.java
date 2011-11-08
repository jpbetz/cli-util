package jpbetz.cli;

public class CommandError extends Exception {
  private static final long serialVersionUID = 1L;
  
  public CommandError(String message) {
  	super(message);
  }
  
  public CommandError(Throwable cause) {
  	super(cause);
  }
  
  public CommandError(String message, Throwable cause) {
  	super(message, cause);
  }
}
