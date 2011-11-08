package jpbetz.cli;

import org.apache.commons.cli.Option;

/**
 * Provides a command for use in a {@link CommandSet}, which will search each command for
 * {@link SubCommand}, {@link Arg} and {@link Opt} annotations.
 * 
 * The {@link SubCommand} annotation provides a short name and description for the command and
 * should be used to annotate each {@link Command} instance.
 * 
 * The {@link Arg} and {@link Opt} annotations are markers to identify fields
 * that are of type {@link Argument} or {@link Option}.  All fields marked with these annotations
 * will be by {@link CommandSet} to parse and type check the command line arguments provided.
 * 
 * {@link Command#exec(CommandContext)} will then be called with a {@link CommandContext} instance
 * containing the parsed arguments and options.
 * 
 * @author "Joe Betz<jbetz@linkedin.com>"
 *
 */
public interface Command
{
  void exec(CommandContext commandLine) throws CommandError, Exception;
}
