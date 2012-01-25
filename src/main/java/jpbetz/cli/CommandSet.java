package jpbetz.cli;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Simple framework for creating command line applications using "sub commands".  Each sub command
 * uses the org.apache.commons.cli library.
 * 
 * {@link Command}s are registered with {@link CommandSet} to build a custom command line application.
 * 
 * See {@link Command} for details.
 * 
 * @author "Joe Betz<jbetz@linkedin.com>"
 *
 */
public class CommandSet {
	
	String _applicationName;
	Map<String, CommandSummary> _subCommands;
	
	public CommandSet(String name) {
		_applicationName = name;
		_subCommands = new TreeMap<String, CommandSummary>();
	}
	
	public void addSubCommand(CommandSummary subCommand) {
		if(subCommand != null) {
			_subCommands.put(subCommand.getName(), subCommand);
		}
	}
	
	public void addSubCommand(Class<? extends Command> subCommandClass) {
		addSubCommand(buildSubCommand(subCommandClass));
	}
	
	public void addSubCommands(Class<? extends Command> ...subCommandClasses) {
		for(Class<? extends Command> subCommandClass : subCommandClasses) {
			addSubCommand(subCommandClass);
		}
	}
	
	public void invoke(String[] args) {
		if(args.length == 0) {
			printHelp();
			return;
		}
		
		String subCommand = args[0].toLowerCase().trim();
		
		if(subCommand.equals("help")) {
			printHelp(args);
      return;
		}
		
		CommandSummary subCommandSummary = _subCommands.get(subCommand);
		
		if(subCommandSummary == null) {
			printHelp("Command not found: " + subCommand);
		} else {
			runSubCommand(subCommandSummary, Arrays.copyOfRange(args, 1, args.length));
		}
	}

	public void printHelp() {
  	System.out.println("usage: " + _applicationName + " <command> [<args>]");
  	System.out.println();
  	System.out.println("Available commands are:");
  	Formatter subCommandFormatter = new Formatter(System.out);
  	for(CommandSummary commandSummary : _subCommands.values()) {
  		subCommandFormatter.format("  %1$-20.19s %2$2s\n", commandSummary.getName(), commandSummary.getDescription());
  		//System.out.println("\t" + commandSummary.getName() + "\t\t" + commandSummary.getDescription());
  	}
  	System.out.println();
  	System.out.println("See '" + _applicationName + " help <command>' for more information on a specific command.");
  }

	public void printHelp(String message) {
  	System.out.println(message);
  	printHelp();
  }

	private void printHelp(String[] args) {
	  if (args.length == 1) {
	  	printHelp();
	  	return;
	  } else {
	  	printSubCommandHelp(args[1].toLowerCase().trim());
	  	return;
	  }
  }
	
	private void printSubCommandHelp(String helpCommand) {
		printSubCommandHelp(helpCommand, null);
	}

	private void printSubCommandHelp(String helpCommand, String message) {
  	CommandSummary subCommand = _subCommands.get(helpCommand);
  	if(subCommand == null) {
  		printHelp("Command not found: " + helpCommand);
  		return;
  	}
  	
    HelpFormatter formatter = new HelpFormatter();
    if(message != null) {
    	System.err.println(subCommand.getName() + ": " + message);
    	System.out.println();
    } else {
    	System.out.println(subCommand.getName() + ": " + subCommand.getDescription());
    }
  	formatter.printHelp(_applicationName + " " + subCommand.getName() + " [options] " + subCommand.getArgs().getUsageArgList(), subCommand.getOptions());
  	System.out.println();
  }
	
	private CommandSummary buildSubCommand(Class<? extends Command> commandClass) {
  	try {
      Command instance = commandClass.newInstance();
      SubCommand cliCommand = commandClass.getAnnotation(SubCommand.class);
      
      if(cliCommand != null) {
  	    Options options = new Options();
  	    Arguments args = new Arguments();
      
  			Map<Option, Field> optionFields = new HashMap<Option, Field>();
  
      	for(Field field : commandClass.getDeclaredFields()) {
      		field.setAccessible(true);
    			Opt optionAnnotations = field.getAnnotation(Opt.class);
    			Arg argumentAnnotations = field.getAnnotation(Arg.class);
    			
    			if(optionAnnotations != null && argumentAnnotations != null) {
    				System.err.println("error: " + commandClass + " field " + field.getName() + " has both @Arg and @Opt annotations, only one is allowed per field.");
    				return null;
    			}
    			
    			if(optionAnnotations != null) {
    				Option option = extractOption(field, optionAnnotations);
    				options.addOption(option);
    				optionFields.put(option, field);
    			}
    			
    			if(argumentAnnotations != null) {
    				Argument argument = extractArgument(field, argumentAnnotations);
    				args.addArgument(argument);
    			}
    		}
  
  	    return new CommandSummary(instance, cliCommand.name(), cliCommand.description(), options, args, optionFields);
      } else {
      	System.err.println("warning: " + commandClass + " is missing @SubCommand annotation, ignoring.");
      	return null;
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

	public void runSubCommand(CommandSummary command, String[] args) {
		Options options = command.getOptions();
		Arguments arguments = command.getArgs();
		CommandLineParser parser = new GnuParser();
		try {
	    CommandLine commandLine = parser.parse(options, args);
	    CommandContext input = new CommandContext(commandLine, arguments);
	    try {
	    	Command instance = command.getInstance();
	    	injectArguments(arguments, input, instance);
	    	injectOptions(command, options, input, instance);
	      command.getInstance().exec(input);
	    } catch (CommandError e) {
	    	printSubCommandHelp(command.getName(), "error: " + e.getMessage());
	    	System.exit(1);
      } catch (Exception e) {
	      e.printStackTrace();
	      System.exit(1);
      }
    } catch (ParseException e) {
    	printSubCommandHelp(command.getName(), "error: " + e.getMessage());
    	System.exit(1);
    }
	}

	@SuppressWarnings("unchecked") // marshal from apache cli
  private void injectOptions(CommandSummary command, Options options, CommandContext input, Command instance) throws IllegalAccessException {
	  for(Option option: (Collection<Option>)options.getOptions()) {
	  	Field field = command.getOptionFields().get(option);
	  	if(option.hasArg()) {
	  		if(!input.hasOption(option)) {
	  			continue;
	  		}
	  		if(option.hasValueSeparator()) {
	  			Object[] values = input.getOptionValues(option);
	  			field.set(instance, values);
	  		} else {
	  			
	  			Object value = input.getOptionObject(option);
	  			field.set(instance, value);
	  		}
	  	} else {
        if (input.hasOption(option))
        {
          field.setBoolean(instance, input.hasOption(option));
        }
	  	}
	  }
  }

	private void injectArguments(Arguments arguments, CommandContext input,
      Command instance) throws IllegalAccessException {
	  for(Argument arg: arguments.getArguments()) {
	  	if(arg.isVararg()) {
	  		List<? extends Object> values = input.getArgObjects(arg);
	  		arg.getField().set(instance, values);
	  	} else {
	  		Object value = input.getArgObject(arg);
	  		if(value != null) {
	  			arg.getField().set(instance, value);
	  		}
	  	}
	  }
  }
	
	private Argument extractArgument(Field field, Arg argumentAnnotations) {
	  ArgumentBuilder builder = ArgumentBuilder.newBuilder(field);
	  builder.withArgName(argumentAnnotations.name());
	  builder.isVararg(argumentAnnotations.isVararg());
	  builder.isRequired(!argumentAnnotations.optional());
	  builder.withType(argumentAnnotations.isVararg() ? argumentAnnotations.type() : field.getType());
	  Argument argument = builder.create();
	  return argument;
  }
	
	private static final Set<Class<?>> allowedTypes = new HashSet<Class<?>>();
	
	static {
		allowedTypes.add(Number.class);
		allowedTypes.add(File.class);
		allowedTypes.add(FileInputStream.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);
		allowedTypes.add(Class.class);
		allowedTypes.add(File[].class);
		allowedTypes.add(Class.class);
		allowedTypes.add(URL.class);
		allowedTypes.add(boolean.class);
	}

	@SuppressWarnings("static-access") // marshal from apache commons cli
  private Option extractOption(Field field, Opt optionAnnotations) {
	  OptionBuilder builder = OptionBuilder.withDescription(optionAnnotations.description());
	  
	  boolean hasArg = !(field.getType().equals(boolean.class) || field.getType().equals(Boolean.class));
	  builder.hasArg(hasArg);
	  
	  if(!allowedTypes.contains(field.getType())) {
	  	System.out.println("warning: type " + field.getType() + " not allowed for field " + field.getName());
	  }
	  
	  if(hasArg) {
	  	if(optionAnnotations.argName().trim().equals("")) {
	  		builder.withArgName(field.getType().getSimpleName().toLowerCase());
	  	} else {
	  		builder.withArgName(optionAnnotations.argName());
	  	}
	  }
	  
	  if(!optionAnnotations.longOpt().trim().equals("")) {
	  	builder.withLongOpt(optionAnnotations.longOpt());
	  }
	  
	  builder.isRequired(optionAnnotations.required());
	  builder.withType(field.getType());
	  Option option = OptionBuilder.create(optionAnnotations.opt());
	  return option;
  }
}
