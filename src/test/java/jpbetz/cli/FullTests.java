package jpbetz.cli;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FullTests extends TestCase {
  
  @SubCommand(name="test1", description="Test 1")
  public static class Test1 implements Command
  {
		@Arg(name="arg1")
		private String arg1; // intentionally made private to test if we can set it using reflection
		
		@Arg(name="arg2")
		protected File arg2; // intentionally made protected to test if we can set it using reflection

		@Opt(opt="n", longOpt="number", description="A number")
		public Number number = 1;
		
		@Opt(opt="f", longOpt="flag", description="A flag")
		boolean hasFlag; // intentionally made package protected to test if we can set it using reflection
	  
		@Override
	  public void exec(CommandContext commandLine) throws CommandError, Exception {
			assertEquals("text", arg1);
			assertEquals(new File("out.txt"), arg2);
			assertEquals(3, number.intValue());
			assertEquals(true, hasFlag);
		}
  }
  
  @SubCommand(name="test2", description="Test 2")
  public static class Test2 implements Command
  {
		@Opt(opt="f", longOpt="flag", description="A flag")
		boolean hasFlag; // intentionally made package protected to test if we can set it using reflection
	  
		@Opt(opt="f2", longOpt="flag2", description="A flag")
		boolean hasFlag2; // intentionally made package protected to test if we can set it using reflection

		
		@Override
	  public void exec(CommandContext commandLine) throws CommandError, Exception {
			assertEquals(true, hasFlag);
			assertEquals(false, hasFlag2);
		}
  }
  
  public static final CommandSet app1 = new CommandSet("test-app");
  static {
  	app1.addSubCommands(Test1.class);
  	app1.addSubCommands(Test2.class);
  }
  
	/**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public FullTests( String testName )
  {
      super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
      return new TestSuite( FullTests.class );
  }
  
  public void testHappyPath()
  {
  	String[] args = "test1 -f -n 3 text out.txt".split("\\s+");
  	app1.invoke(args);
  	
  }
  
  public void testPrimitives()
  {
  	String[] args = "test2 -f".split("\\s+");
  	app1.invoke(args);
  }
}
