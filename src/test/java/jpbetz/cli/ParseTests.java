package jpbetz.cli;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

/**
 * Verify arguments are parsed correctly.
 */
public class ParseTests 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ParseTests( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ParseTests.class );
    }
    
    @SubCommand(name="test1", description="Test 1")
    public static class Test1 implements Command
    {
  		@Arg(name="arg1", optional=true)
  		public String text;

  		@Opt(opt="n", longOpt="number", description="A number")
  		public Number number = 1;
  		
  		@Opt(opt="f", longOpt="flag", description="A flag")
  		public boolean hasFlag;
  	  
  		@Override
  	  public void exec(CommandContext commandLine) throws CommandError, Exception {}
    }
    
    @SubCommand(name="test2", description="Test 2")
    public static class Test2 implements Command
    {
  		@Arg(name="arg1", optional=true, isVararg=true)
  		public String[] text;
  	  
  		@Override
  	  public void exec(CommandContext commandLine) throws CommandError, Exception {}
    }
    
    @SubCommand(name="test3", description="Test 3")
    public static class Test3 implements Command
    {
    	@Opt(opt="f", longOpt="file", description="A file")
  		public File file;
    	
    	@Opt(opt="s", longOpt="string", description="A string")
  		public String string;
    	
    	@Opt(opt="n", longOpt="number", description="A number")
  		public Number number;
    	
    	@Opt(opt="c", longOpt="class", description="A class")
  		public Class<?> clazz;
  	  
  		@Override
  	  public void exec(CommandContext commandLine) throws CommandError, Exception {}
    }
    
    public static final CommandSet app1 = new CommandSet("test-app");
    static {
    	app1.addSubCommands(Test1.class);
    	app1.addSubCommands(Test2.class);
    	app1.addSubCommands(Test3.class);
    }
    
    public void testHappyPath() throws Exception
    {
  		CommandContext context = parse(app1, "test1 -n 3 X");
  		assertTrue(context.hasOption("n"));
  		assertEquals(3, ((Number)context.getOptionObject("n")).intValue());
  		assertEquals("X", context.getArgValue("arg1"));
    }
    
    public void testOptionals() throws Exception
    {
  		CommandContext context = parse(app1, "test1");
  		assertFalse(context.hasOption("f"));
  		assertFalse(context.hasOption("n"));
  		assertFalse(context.hasArg("arg1"));
    }
    
    public void testVarargs() throws Exception
    {
    	{
	    	CommandContext context = parse(app1, "test2");
	  		assertFalse(context.hasArg("arg1"));
    	}
    	{
	    	CommandContext context = parse(app1, "test2 X");
	  		assertTrue(context.hasArg("arg1"));
	  		List<String> values = context.getArgValues("arg1");
	  		assertEquals(1, values.size());
    	}
    	{
	    	CommandContext context = parse(app1, "test2 X Y Z");
	  		assertTrue(context.hasArg("arg1"));
	  		List<String> values = context.getArgValues("arg1");
	  		assertEquals(3, values.size());
    	}
    }
    
    public void testTypes() throws Exception
    {
    	CommandContext context = parse(app1, "test3 -n 3 -f out.txt -s text -c jpbetz.cli.CliTest");
    	assertEquals(3, ((Number)context.getOptionObject("n")).intValue());
    	assertEquals(new File("out.txt"), context.getOptionObject("f"));
    	assertEquals("text", context.getOptionObject("s"));
    	assertEquals(ParseTests.class, context.getOptionObject("c"));
    }

		private CommandContext parse(CommandSet app, String args) throws ParseException {
			String[] parts = args.split("\\s+");
	    CommandSummary test1 = app._subCommands.get(parts[0]);
  		CommandLineParser parser = new GnuParser();
  		CommandLine line = parser.parse(test1.getOptions(), Arrays.copyOfRange(parts, 1, parts.length));
  		CommandContext context = new CommandContext(line, test1.getArgs());
	    return context;
    }
}
