package jpbetz.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

@SuppressWarnings("static-access")
@CliCommand(name="yell", description="Yell stuff")
public class Yell implements Command {
	@CliArgument public static final Argument TEXT_ARG = ArgumentBuilder.newBuilder()
		.isRequired(false).withArgName("<Text to yell>").create();
  
	@CliOption public static final Option  REPEAT_OPT = OptionBuilder
		.withLongOpt("repeat").hasArg(true).withType(Number.class)
		.withDescription("Number of times to yell the text").create("n");
  
	@Override
  public void exec(CommandLineArgs commandLine) throws SubCommandError, Exception {
    int yells = (commandLine.hasOption(REPEAT_OPT) ? (Number)commandLine.getOptionObject(REPEAT_OPT) : 1).intValue();
    String text = commandLine.hasArg(TEXT_ARG) ? commandLine.getArgValue(TEXT_ARG) : "Hello World!";
    for(int i = 0; i < yells; i++) System.out.println(text);
  }
}
