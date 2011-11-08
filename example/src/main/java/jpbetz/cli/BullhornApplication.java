package jpbetz.cli;


public class BullhornApplication {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
		CommandSet app = new CommandSet("bullhorn");
		app.addSubCommands(Yell.class);
		app.invoke(args);
	}
}
