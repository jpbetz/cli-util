cli-util
========

cli-util is an extension of apache common's java command line interface (commons-cli) 
library. It adds sub commands, more type safely and a declarative style for defining 
commands, their options, flags and arguments.

If you use Maven, SBT or Gradle, you can easily get the your CLI built and installed to
be easily run from the command line at any time, detailed instructions below.

Example
-------

BullhornApplication.java

    ...
    public static void main(String[] args) {
      CommandSet app = new CommandSet("bullhorn");
      app.addSubCommands(Yell.class);
      app.invoke(args);
    }
    ...
    
Yell.java


    @SubCommand(name="yell", description="Yell stuff")
    public class Yell implements Command {
      @Arg(name="Text to yell")
      public String text;
  
      @Opt(opt="n", longOpt="repeat", description="Number of times to yell the text")
      public Number yells = 0;
    
      @Override
      public void exec(CommandContext commandLine) throws CommandError, Exception {
        for(int i = 0; i < yells.intValue(); i++) System.out.println(text);
      }
    }


Try it out
-----------

cli-util is hosted central maven repository, so all you need to do to use it in a maven (or ivy or gradle) project is add the dependency:

    <dependency>
      <groupId>com.github.jpbetz</groupId>
      <artifactId>cli-util</artifactId>
      <version>1.0</version>
    </dependency>
    
(Alternative, one may set version to 1.1-SNAPSHOT to get the latest build snapshots)

Next, open your .bashrc, .profile, or whatever you use and add a BULLHORN_HOME environment variable 
pointing to your cli-util working directory. Also, the cli bin to the PATH. 
Lastly, Make sure the JAVA_HOME environment variable is pointing to a JDK.

For bash style shells: 

    export BULLHORN_HOME=<cli-util-path>/cli-util/example
    export PATH=$PATH:$BULLHORN_HOME/bin

Build it:

    $ cd $BULLHORN_HOME
    $ mvn package

Run the cli:

    $ bullhorn yell

Classpath Setup Hints
-------------------------------

Command line interfaces should be run from a simple shell script.  For java, the shell script
load a classpath.  Setting up a classpath correctly is depends a lot on the particulars of the
project it is added to.  Some hints:

1. Require a "project home" environment variable.  Usually this is a PROJECT_NAME_HOME variable.
2. Make locations in the classpath relative to the project home environment variable when possible.
3. If the classpath is non-trivial or may change, autogenerate the classpath with your build tool.
4. Pass arguments through to java app with "$@" (include the quotes)
5. Provide a PROJECT_NAME_OPT environment variable that is passed in to the jvmargs that can be optionally set.

See sample-bin/bullhorn for an example bash shell script.

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

    #!/bin/bash
    java -cp $BULLHORN_HOME/target/bullhorn-1.0-SNAPSHOT-jar-with-dependencies.jar \
      $BULLHORN_OPTS \
      jpbetz.cli.BullhornApplication \
      "$@"

### SBT

Add the "sbt-assembly" plugin to your project and your build.sbt file.

project/plugins.sbt:

    resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

    addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.0")

build.sbt:

    import AssemblyKeys._

    assemblySettings

Build the assembly using sbt:

    $ sbt
    sbt> assembly
    sbt> exit

This compiles a "uber jar" in target named <project-name>-assembly-<version>.jar.

shell script:

    #!/bin/bash
    java -cp `cat $BULLHORN_HOME/target/bullhorn-assembly-1.0-SNAPSHOT.jar` \
      $BULLHORN_OPTS \
      jpbetz.cli.BullhornApplication \
      "$@"

### Gradle

Add a custom task to generate the classpath.  This can either be used to generate the shell script, or
be written to a plain file which is used in the shell script.

build.gradle:

    task cliClasspath << {
      new File("$projectDir/bin/classpath").withWriter { out ->
        runtimeClasspath.each { File file -> out.print file.absolutePath + ':' }
      }
    }
    compileJava.dependsOn cliClasspath

shell script:

    #!/bin/bash
    java -cp `cat $BULLHORN_HOME/bin/classpath` \
      $BULLHORN_OPTS \
      jpbetz.cli.BullhornApplication \
      "$@"

### Ant

??? Suggestions welcome.