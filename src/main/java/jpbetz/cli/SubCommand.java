package jpbetz.cli;

import org.apache.commons.cli.Options;

public class SubCommand {
	private Command _instance;
	private String _name;
	private String _description;
	private Options _options;
	private Arguments _args;
	
	public SubCommand(Command instance, String name, String description, Options options, Arguments args) {
		_instance = instance;
		_name = name;
		_description = description;
		_options = options;
		_args = args;
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
}