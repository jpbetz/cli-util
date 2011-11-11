package jpbetz.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.TypeHandler;

/**
 * Decorates apache.commons.cli.CommandLine with additional utilities for handling options,
 * arguments and stdin. 
 * 
 * @author "Joe Betz<jbetz@linkedin.com>"
 *
 */
public class CommandContext {
	private CommandLine _commandLine;
	private Arguments _arguments;
	private Map<Argument, String> _argValues;
	private List<String> _varargValues;
	
	public CommandContext(CommandLine commandLine, Arguments arguments) throws ParseException {
		_commandLine = commandLine;
		_arguments = arguments;
		parse();
	}
	
	/**
	 * Parses and verifies the command line options.
	 * 
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked") // marshall from apache commons cli
	private void parse() throws ParseException {
		_argValues = new HashMap<Argument, String>();
		_varargValues = new ArrayList<String>();
    List<String> argList = _commandLine.getArgList();
		
		int required = 0;
		boolean hasOptional = false;
		boolean hasVarargs = false;
		for(Argument argument : _arguments.getArguments()) {
			if(argument.isRequired()) {
				required++;
			} else {
				hasOptional = true;
			}
			if(argument.isVararg()) {
				hasVarargs = true;
			}
		}
		int allowed = hasOptional? required+1 : required;
		
		if(argList.size() < required) {
			throw new ParseException("Not enough arguments provided.  " + required + " required, but only " + argList.size() + " provided.");
		}
		
		if(!hasVarargs) {
			if(argList.size() > allowed) {
				throw new ParseException("Too many arguments provided.  Only " + allowed + " allowed, but " + argList.size() + " provided.");
			}
		}
		
		int index = 0;
		boolean finalArgEncountered = false;
		for(Argument argument : _arguments.getArguments()) {
			
			if(finalArgEncountered) throw new IllegalStateException("Illegal arguments defined.  No additional arguments may be defined after first optional or vararg argument.");
			
			if(argument.isRequired() && !argument.isVararg()) { // the normal case
				if(index <= argList.size()) {
					_argValues.put(argument, argList.get(index));
				} else {
					throw new IllegalStateException("not enough arguments"); // should not happen given above size check
				}
			}
			else { // it's the last argument, either it's optional or a vararg
				finalArgEncountered = true;
				
				if(argument.isVararg()) {
					_varargValues = argList.subList(Math.min(index, argList.size()), argList.size());
					
					if(argument.isRequired() && _varargValues.size() < 1) {
						throw new IllegalStateException("not enough arguments"); // should not happen given above size check
					}
				}
				else { // if it's a optional
					if(index < argList.size()) {
						_argValues.put(argument, argList.get(index));
					}
				}
			}
			index++;
		}
	}
	
	@SuppressWarnings("unchecked")
  public List<Object> getArgList() {
		return (List<Object>)_commandLine.getArgList();
	}
	
	public String[] getArgs() {
		return _commandLine.getArgs();
	}

	public Object getOptionObject(char opt) {
		return _commandLine.getOptionObject(opt);
	}
	
	@SuppressWarnings("deprecation")
  public Object getOptionObject(String opt) {
		return _commandLine.getOptionObject(opt);
	}
	
	@SuppressWarnings("deprecation")
	public Object getOptionObject(Option option) {
		return _commandLine.getOptionObject(option.getOpt());
	}
	
	public Option[] getOptions() {
		return _commandLine.getOptions();
	}
	
	public String getOptionValue(char opt) {
		return _commandLine.getOptionValue(opt);
	}
	
	public String getOptionValue(String opt) {
		return _commandLine.getOptionValue(opt);
	}
	
	public String getOptionValue(Option option) {
		return _commandLine.getOptionValue(option.getOpt());
	}
	
	public String getOptionValue(char opt, String defaultValue) {
		return _commandLine.getOptionValue(opt, defaultValue);
	}
	
	public String getOptionValue(String opt, String defaultValue) {
		return _commandLine.getOptionValue(opt, defaultValue);
	}
	
	public String getOptionValue(Option option, String defaultValue) {
		return _commandLine.getOptionValue(option.getOpt(), defaultValue);
	}
	
	public String[] getOptionValues(char opt) {
		return _commandLine.getOptionValues(opt);
	}
	
	public String[] getOptionValues(String opt) {
		return _commandLine.getOptionValues(opt);
	}
	
	public String[] getOptionValues(Option option) {
		return _commandLine.getOptionValues(option.getOpt());
	}
	
	public boolean hasOption(char opt) {
		return _commandLine.hasOption(opt);
	}
	
	public boolean hasOption(String opt) {
		return _commandLine.hasOption(opt);
	}
	
	public boolean hasOption(Option option) {
		return _commandLine.hasOption(option.getOpt());
	}
	
	public boolean hasArg(Argument arg) {
		if(arg.isVararg()) {
			return !_varargValues.isEmpty();
		} else {
			return _argValues.containsKey(arg);
		}
	}
	
	public boolean hasArg(String argName) {
		Argument arg = _arguments.getArg(argName);
		if(arg == null) return false;
		return hasArg(arg);
	}

	public String getArgValue(String argName) {
		Argument arg = _arguments.getArg(argName);
		if(arg == null) return null;
		return getArgValue(arg);
	}
	
	public String getArgValue(Argument arg) {
		if(arg.isVararg()) {
			if(_varargValues.isEmpty()) return null;
			else return _varargValues.get(0);
		} else {
			return _argValues.get(arg);
		}
	}

	public List<String> getArgValues(String argName) {
		Argument arg = _arguments.getArg(argName);
		if(arg == null) return null;
		return getArgValues(arg);
	}
	
	public List<String> getArgValues(Argument arg) {
		if(arg.isVararg()) {
			return _varargValues;
		} else {
			return Collections.singletonList(getArgValue(arg));
		}
	}
	
	@SuppressWarnings("unchecked") // marshall between unchecked apache cli methods
  public <T> List<T> getArgObjects(Argument arg) {
		List<String> values = getArgValues(arg);
		if(arg.getType() == null) return (List<T>)values; // TODO: should throw exception if no type set
		
		List<T> results = new ArrayList<T>();
		for(String value : values) {
			try {
				results.add((T)parseValue(value, arg.getType()));
			} catch (ParseException e) {
				System.err.println("Exception found converting an arg to desired type: " + e.getMessage());
			}
		}
		return results;
	}
	
	@SuppressWarnings("unchecked") // marshall between unchecked apache cli methods
	private <T> T parseValue(String value, Object type) throws ParseException {
		return (T)TypeHandler.createValue(value, type);
	}
	
	public <T> List<? extends Object> getArgObjects(String argName) {
		Argument arg = _arguments.getArg(argName);
		if(arg == null) return null;
		return getArgObjects(arg);
	}
	
	@SuppressWarnings("unchecked") // marshall between unchecked apache cli methods
	public <T> T getArgObject(Argument arg) {
		String value = getArgValue(arg);
		if(arg.getType() == null) return (T)value;
		else if(value == null) return null;
		else {
			try {
				return (T)parseValue(value, arg.getType());
			} catch (ParseException e) {
				System.err.println("Exception found converting an arg to desired type: " + e.getMessage());
				return null;
			}
		}
	}
	
	public Object getArgObject(String argName) {
		Argument arg = _arguments.getArg(argName);
		if(arg == null) return null;
		return getArgObject(arg);
	}
	
	@SuppressWarnings("unchecked")
  public Iterator<Object> iterator() {
		return (Iterator<Object>)_commandLine.iterator();
	}

	/*
	@SuppressWarnings("unchecked") // marshal from apache commons lib
	public String readStdin() throws IOException {
    List<String> stdin = (List<String>)IOUtils.readLines(System.in);
		String input = StringUtils.join(stdin.toArray(new String[0]));
	  return input;
  }
  */
}
