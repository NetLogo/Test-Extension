import org.nlogo.api.{ DefaultClassManager, PrimitiveManager,
                       DefaultCommand, DefaultReporter,
                       Syntax, Context, Argument, LogoException }

// extension
class TestExtension extends DefaultClassManager {
  val tester = new Tester
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("setup", new SetupCommand(tester))
    manager.addPrimitive("add", new AddCommand(tester))
    manager.addPrimitive("run", new RunCommand(tester))
    manager.addPrimitive("clear", new ClearCommand(tester))
    manager.addPrimitive("summary", new SummaryReporter(tester))
    manager.addPrimitive("details", new DetailsReporter(tester))
  }
}

// primitives
class SetupCommand(tester: Tester) extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax(Array(Syntax.CommandTaskType))
  def perform(args: Array[Argument], context: Context) {
    tester.setup = args(0).getCommandTask
  }
}

class AddCommand(tester: Tester) extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax(
      Array(Syntax.StringType, Syntax.CommandTaskType,
            Syntax.ReporterTaskType, Syntax.WildcardType))
  def perform(args: Array[Argument], context: Context) {
    tester.add(
      Test(args(0).getString,
           args(1).getCommandTask,
           args(2).getReporterTask,
           args(3).get))
  }
}

class RunCommand(tester: Tester) extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax()
  def perform(args: Array[Argument], context: Context) {
    tester.run(context)
  }
}

class ClearCommand(tester: Tester) extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax()
  def perform(args: Array[Argument], context: Context) {
    tester.clear()
  }
}

class SummaryReporter(tester: Tester) extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(Syntax.StringType)
  def report(args: Array[Argument], context: Context) =
    tester.results.summary
}

class DetailsReporter(tester: Tester) extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(Syntax.StringType)
  def report(args: Array[Argument], context: Context) =
    tester.results.details
}
