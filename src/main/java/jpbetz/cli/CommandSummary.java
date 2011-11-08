package jpbetz.cli;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandSummary {
	private Command _instance;
	private String _name;
	private String _description;
	private Options _options;
	private Arguments _args;
	private Map<Option, Field> _optionFields;
	
	public CommandSummary(Command instance, String name, String description, Options options, Arguments args, Map<Option, Field> optionFields) {
		_instance = instance;
		_name = name;
		_description = description;
		_options = options;
		_args = args;
		_optionFields = optionFields;
	}
	
	public Command getInstance() {
    return _instance;
  }
	
	public String getName() {
    return _name;
  }
	
	public String getDescription() {
    return _description;
  }
	
	public Options getOptions() {
    return _options;
  }
	
	public Arguments getArgs() {
	  return _args;
  }
	
	public Map<Option, Field> getOptionFields() {
	  return _optionFields;
  }
}