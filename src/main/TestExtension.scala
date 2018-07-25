package org.nlogo.extensions.test

import org.nlogo.api.{ DefaultClassManager, PrimitiveManager,
                       Command, Reporter,
                       Context, Argument, LogoException }
import org.nlogo.core.Syntax

// extension
class TestExtension extends DefaultClassManager {

  val tester = new Tester

  override def load(manager: PrimitiveManager) {
    manager.addPrimitive("setup", SetupCommand)
    manager.addPrimitive("add", AddCommand)
    manager.addPrimitive("run", RunCommand)
    manager.addPrimitive("clear", ClearCommand)
    manager.addPrimitive("summary", SummaryReporter)
    manager.addPrimitive("details", DetailsReporter)
  }

  override def clearAll() {
    tester.clear()
  }

  // primitives
  object SetupCommand extends Command {
    override def getSyntax =
      Syntax.commandSyntax(List(Syntax.CommandType))
    override def perform(args: Array[Argument], context: Context) {
      tester.setup = args(0).getCommand
    }
  }

  object AddCommand extends Command {
    override def getSyntax =
      Syntax.commandSyntax(List(Syntax.StringType, Syntax.CommandType, Syntax.ReporterType, Syntax.WildcardType))
    override def perform(args: Array[Argument], context: Context) {
      tester.add(
        Test(args(0).getString,
             args(1).getCommand,
             args(2).getReporter,
             args(3).get))
    }
  }

  object RunCommand extends Command {
    override def getSyntax =
      Syntax.commandSyntax()
    override def perform(args: Array[Argument], context: Context) {
      tester.run(context)
    }
  }

  object ClearCommand extends Command {
    override def getSyntax =
      Syntax.commandSyntax()
    override def perform(args: Array[Argument], context: Context) {
      tester.clear()
    }
  }

  object SummaryReporter extends Reporter {
    override def getSyntax =
      Syntax.reporterSyntax(ret = Syntax.StringType)
    override def report(args: Array[Argument], context: Context) =
      tester.results.summary
  }

  object DetailsReporter extends Reporter {
    override def getSyntax =
      Syntax.reporterSyntax(ret = Syntax.StringType)
    override def report(args: Array[Argument], context: Context) =
      tester.results.details
  }

}
