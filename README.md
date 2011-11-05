cli-util
========

cli-util is an extension of apache common's java command line interface (commons-cli) 
library. It adds sub commands, more type safely and a declarative style for defining 
commands, their options, flags and arguments.

Example
-------

    public class BullhornApplication {
      public static void main(String[] args) {
        SubCommandShell app = new SubCommandShell("bullhorn");
        app.addSubCommands(Yell.class);
        app.invoke(args);
      }
    }
  
    @CliCommand(name="yell", description="Yell stuff")
    public class Yell implements Command {
      @CliArgument public static final Argument TEXT_ARG = ArgumentBuilder.newBuilder()
        .isRequired(false).withArgName("<Text to yell>").create();
      
      @CliOption public static final Option  REPEAT_OPT = OptionBuilder
        .withLongOpt("repeat").hasArg(true).withType(Number.class)
        .withDescription("Number of times to yell the text").create("n");
      
      @Override
      public void exec(CommandLineArgs commandLine) throws SubCommandError, Exception {
        int yells = (commandLine.hasOption(REPEAT_OPT) ? (Number)commandLine.getOptionObject(REPEAT_OPT) : 1).intValue();
        String text = commandLine.hasArg(TEXT_ARG) ? commandLine.getArgValue(TEXT_ARG) : "Hello World!";
        for(int i = 0; i < yells; i++) System.out.println(text);
      }
    }


Try it out
-----------

Open your .bashrc, .profile, or whatever you use and add a CLIUTIL_HOME environment variable 
pointing to your cli-util working directory. Also, the cli bin to the PATH. 
Lastly, Make sure the JAVA_HOME environment variable is pointing to a JDK.

For bash style shells: 

    export CLIUTIL_HOME=/Users/<username>/projects/cli-util
    export PATH=$PATH:$CLIUTIL_HOME/sample-bin

Build stack:

    $ cd $CLIUTIL_HOME
    $ mvn package

Run the cli:

    $ bullhorn yell


Classpath Setup Hints
-------------------------------

Each command line script (e.g. sample-bin/bullhorn) requires a classpath be setup.
Setting up a classpath correctly is depends a lot on the particulars of the
project it is added to.  Some hints:

1. Use an project home environment variable.  Traditionally this is a <project_name>_HOME variable.
2. Make all locations in the classpath relative to the project home environment variable where possible.
3. Autogenerate the classpath with your build tool.
4. Pass arguments through to java app with "$@" (include the quotes)
5. Provide a <project_name>_OPT environment variable that is passed in to the jvmargs that can be optionally set.

### Maven

The simplest approach is to using the jar-with-dependencies assembly build plugin (see pom.xml)
and then reference it in the shell script.

pom.xml:

    <project>
      ...
      <build>
        <plugins>
          <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>2.2.1</version>
            <configuration>
              <descriptorRefs>
                <descriptorRef>jar-with-dependencies</descriptorRef>
              </descriptorRefs>
            </configuration>
            <executions>
              <execution>
                <id>make-bullhorn</id> <!-- this is used for inheritance merges -->
                <phase>package</phase> <!-- bind to the packaging phase -->
                <goals>
                  <goal>single</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    </project>

shell script:

    java -cp $CLIUTIL_HOME/target/cli-util-1.0-SNAPSHOT-jar-with-dependencies.jar \
      $CLIUTIL_OPTS \
      jpbetz.cli.BullhornApplication \
      "$@"

### Gradle

Add a custom task to generate the classpath.  This can either be used to generate the shell script, or
be written to a plain file which is used in the shell script.

build.gradle:

    task cliClasspath << {
      new File("$projectDir/sample-bin/classpath").withWriter { out ->
         
        runtimeClasspath.each {File file ->
            out.print file.absolutePath + ':'
        }
      }
    }
    
    compileJava.dependsOn cliClasspath

shell script:

    java -cp `cat $CLIUTIL_HOME/sample-bin/classpath` \
      $CLIUTIL_OPTS \
      jpbetz.cli.BullhornApplication \
      "$@"

### Ant

??? Suggestions welcome.