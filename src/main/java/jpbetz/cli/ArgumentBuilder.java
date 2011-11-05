package jpbetz.cli;

public class ArgumentBuilder {
	private String _argName;
	private Object _type;
	private boolean _isRequired = true;
	private boolean _isVararg = false;
	
	public ArgumentBuilder() {
	}
	
	public static ArgumentBuilder newBuilder() {
		return new ArgumentBuilder();
	}

	public ArgumentBuilder withArgName(String argName) {
		_argName = argName;
		return this;
	}
	
	public ArgumentBuilder withType(Object type) {
		_type = type;
		return this;
	}
	
	public ArgumentBuilder isRequired(boolean isRequired) {
		_isRequired = isRequired;
		return this;
	}
	
	public ArgumentBuilder isVararg(boolean isVararg) {
		_isVararg = isVararg;
		return this;
	}
	
	public Argument create() {
		return new Argument(_argName, _type, _isRequired, _isVararg);
	}
}
