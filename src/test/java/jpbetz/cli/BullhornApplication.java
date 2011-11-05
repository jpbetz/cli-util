package jpbetz.cli;


public class BullhornApplication {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
		SubCommandShell app = new SubCommandShell("bullhorn");
		app.addSubCommands(Yell.class);
		app.invoke(args);
	}
}
