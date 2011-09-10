import org.nlogo.api.{ DefaultClassManager, PrimitiveManager,
                       DefaultCommand, DefaultReporter,
                       Syntax, Context, Argument, LogoException }

// extension
class TestExtension extends DefaultClassManager {

  val tester = new Tester

  def load(manager: PrimitiveManager) {
    manager.addPrimitive("setup", SetupCommand)
    manager.addPrimitive("add", AddCommand)
    manager.addPrimitive("run", RunCommand)
    manager.addPrimitive("clear", ClearCommand)
    manager.addPrimitive("summary", SummaryReporter)
    manager.addPrimitive("details", DetailsReporter)
  }

  // primitives
  object SetupCommand extends DefaultCommand {
    override def getSyntax =
      Syntax.commandSyntax(Array(Syntax.CommandTaskType))
    def perform(args: Array[Argument], context: Context) {
      tester.setup = args(0).getCommandTask
    }
  }

  object AddCommand extends DefaultCommand {
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

  object RunCommand extends DefaultCommand {
    override def getSyntax =
      Syntax.commandSyntax()
    def perform(args: Array[Argument], context: Context) {
      tester.run(context)
    }
  }

  object ClearCommand extends DefaultCommand {
    override def getSyntax =
      Syntax.commandSyntax()
    def perform(args: Array[Argument], context: Context) {
      tester.clear()
    }
  }

  object SummaryReporter extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(Syntax.StringType)
    def report(args: Array[Argument], context: Context) =
      tester.results.summary
  }

  object DetailsReporter extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(Syntax.StringType)
    def report(args: Array[Argument], context: Context) =
      tester.results.details
  }

}
