package jpbetz.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Arguments {
	private List<Argument> _arguments;
	
	private Map<String, Argument> _argumentsByName;
	private Set<Argument> _argumentSet;
	
	private boolean _isSealed;
	
	public Arguments() {
		_arguments = new ArrayList<Argument>();
		_argumentsByName = new HashMap<String, Argument>();
		_argumentSet = new HashSet<Argument>();
		_isSealed = false;
	}
	
	public Argument addArgument(Argument arg) {
		if(arg.isVararg() || !arg.isRequired()) {
			if(_isSealed) {
				throw new IllegalArgumentException("Illegal arguments defined.  No additional arguments may be defined after first optional or vararg argument.");
			}
			_isSealed = true;
		}			
		
		_arguments.add(arg);
		_argumentSet.add(arg);
		
		if(arg.hasArgName()) {
			_argumentsByName.put(arg.getArgName(), arg);
		}
	
		return arg;
	}
	
	public boolean hasArg(Argument arg) {
		return _argumentSet.contains(arg);
	}
	
	public boolean hasArg(String argName) {
		return _argumentsByName.containsKey(argName);
	}
	
	public Argument getArg(String argName) {
		return _argumentsByName.get(argName);
	}
	
	public int indexOf(Argument arg) {
		return _arguments.indexOf(arg);
	}
	
	public Collection<Argument> getArguments() {
		return Collections.unmodifiableCollection(_arguments);
	}
	
	public String getUsageArgList() {
		StringBuilder builder = new StringBuilder();
		int i = 1;
		for(Argument argument : getArguments()) {
			builder.append(argument.printUsage(i++));
			builder.append(" ");
		}
		return builder.toString();
	}
}
