package jpbetz.cli;

import org.apache.commons.cli.Option;

/**
 * Provides a command for use in a {@link SubCommandShell}, which will search each command for
 * {@link CliCommand}, {@link CliArgument} and {@link CliOption} annotations.
 * 
 * The {@link CliCommand} annotation provides a short name and description for the command and
 * should be used to annotate each {@link Command} instance.
 * 
 * The {@link CliArgument} and {@link CliOption} annotations are markers to identify fields
 * that are of type {@link Argument} or {@link Option}.  All fields marked with these annotations
 * will be by {@link SubCommandShell} to parse and type check the command line arguments provided.
 * 
 * {@link Command#exec(CommandLineArgs)} will then be called with a {@link CommandLineArgs} instance
 * containing the parsed arguments and options.
 * 
 * @author "Joe Betz<jbetz@linkedin.com>"
 *
 */
public interface Command
{
  void exec(CommandLineArgs commandLine) throws SubCommandError, Exception;
}
