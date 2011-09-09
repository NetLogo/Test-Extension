import org.nlogo.api.{ CommandTask, ReporterTask, Dump, Equality, LogoException, Context }
import scala.collection.immutable.Vector

object Tester {
  private var tests = Vector[Test]()
  private var _results = new Results(Nil)
  def results = _results
  var setup: Option[CommandTask] = None
  def add(t: Test) { tests :+= t }
  def run(context: Context) {
    _results = new Results(tests.map(_.run(context)))
  }
  def clear() {
    _results = new Results(Nil)
    tests = Vector[Test]()
    setup = None
  }
}

sealed trait TestResult { def message: String }
case class Pass(message: String) extends TestResult
case class Failure(message: String) extends TestResult
case class Error(message: String) extends TestResult

class Results(results: Seq[TestResult]) {
  def successes = results.collect{case p: Pass => p}
  def failures = results.collect{case f: Failure => f}
  def errors = results.collect{case e: Error => e}
  def header =
    "Test Run " +
    (if(failures.isEmpty && errors.isEmpty) "Passed"
     else "Failed") +
    " - Total " + results.size +
    ", Failed " + failures.size +
    ", Errors " + errors.size +
    ", Passed " + successes.size
  def summary =
    (header +: (failures ++ errors).map(_.message))
      .mkString("\n")
  def details =
    (header +: results.map(_.message))
      .mkString("\n")
}

// helpers
case class Test(name: String, command: CommandTask, reporter: ReporterTask, expected: AnyRef) {
  def run(context: Context): TestResult = {
    context.asInstanceOf[org.nlogo.nvm.ExtensionContext]
      .workspace.clearAll()
    try {
      for(setup <- Tester.setup)
        setup.perform(context, Array())
      command.perform(context, Array())
      val actual = reporter.report(context, Array())
      if(Equality.equals(actual, expected))
        Pass("PASS '" + name + "'")
      else
        Failure("FAIL '" + name + "' " +
                "expected: " + Dump.logoObject(expected) +
                " but got: " + Dump.logoObject(actual))
    }
    catch {
      case e: LogoException =>
        Error("ERROR '" + name + "' " + e.getMessage)
    }
  }
}
