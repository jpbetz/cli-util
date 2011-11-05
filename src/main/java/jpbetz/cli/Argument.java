package jpbetz.cli;

/**
 * A command line argument.
 * 
 * @author "Joe Betz<jbetz@linkedin.com>"
 *
 */
public class Argument {
	private final String _argName;
	private final Object _type;
	private final boolean _isRequired;
	private final boolean _isVararg;
	
	public Argument(Object type) {
		this(null, type, true, false);
	}
	
	public Argument(String argName) {
		this(argName, String.class, true, false);
	}

	public Argument(String argName, Object type, boolean isRequired, boolean isVararg) {
		_argName = argName;
		_type = type;
		_isRequired = isRequired;
		_isVararg = isVararg;
	}
	
	public boolean hasArgName() {
		return _argName != null;
	}

	public String getArgName() {
  	return _argName;
  }
	
	public boolean hasType() {
		return _type != null;
	}

	public Object getType() {
  	return _type;
  }

	public boolean isRequired() {
  	return _isRequired;
  }

	public boolean isVararg() {
  	return _isVararg;
  }
	
	public String printUsage(int argPosition) {
		if(isVararg()) {
			if(isRequired()) {
				return printTitle(argPosition) + " [" + printTitle(argPosition) + "...]" ;
			} else {
				return "[" + printTitle(argPosition) + "...]" ;
			}
		} else if (isRequired()) {
			return printTitle(argPosition);
		} else {
			return "[" + printTitle(argPosition) + "]" ;
		}
	}
	
	private String printTitle(int argPosition) {
		if(hasArgName()) {
			return "<" + getArgName() + ">";
		} else if (hasType()) {
			return getType().toString();
		} else {
			return "ARG" + argPosition;
		}
	}
}
