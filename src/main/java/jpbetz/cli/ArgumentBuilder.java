package jpbetz.cli;

import java.lang.reflect.Field;

public class ArgumentBuilder {
	private Field _field;
	private String _argName;
	private Object _type;
	private boolean _isRequired = true;
	private boolean _isVararg = false;
	
	public ArgumentBuilder(Field field) {
		_field = field;
	}
	
	public static ArgumentBuilder newBuilder(Field field) {
		return new ArgumentBuilder(field);
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
		return new Argument(_field, _argName, _type, _isRequired, _isVararg);
	}
}
