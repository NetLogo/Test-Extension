import org.nlogo.api.{ DefaultClassManager, PrimitiveManager,
                       DefaultCommand, DefaultReporter,
                       Syntax, Context, Argument, LogoException }

// extension
class TestExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("setup", new SetupCommand)
    manager.addPrimitive("add", new AddCommand)
    manager.addPrimitive("run", new RunCommand)
    manager.addPrimitive("summary", new SummaryReporter)
    manager.addPrimitive("details", new DetailsReporter)
  }
}

// primitives
class SetupCommand extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax(Array(Syntax.CommandTaskType))
  def perform(args: Array[Argument], context: Context) {
    // could possibly check to see if there is a setup method here already...
    Tester.setup = Some(args(0).getCommandTask)
  }
}

class AddCommand extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax(
      Array(Syntax.StringType, Syntax.CommandTaskType,
            Syntax.ReporterTaskType, Syntax.WildcardType))
  def perform(args: Array[Argument], context: Context) {
    Tester.addTest(
      Test(args(0).getString,
           args(1).getCommandTask,
           args(2).getReporterTask,
           args(3).get))
  }
}

class RunCommand extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax()
  def perform(args: Array[Argument], context: Context) {
    Tester.run(context)
  }
}

class SummaryReporter extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(Syntax.StringType)
  def report(args: Array[Argument], context: Context) =
    Tester.summary
}

class DetailsReporter extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(Syntax.StringType)
  def report(args: Array[Argument], context: Context) =
    Tester.details
}
