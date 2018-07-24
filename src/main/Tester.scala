import org.nlogo.api.{ AnonymousCommand, AnonymousReporter, Dump, Equality, LogoException, Context }
import org.nlogo.core.Syntax
import scala.collection.immutable.Vector

class Tester {
  private var tests = Vector[Test]()
  private var _results = new Results(Nil)
  def results = _results
  private val emptyTask = new AnonymousCommand {
    override def syntax = Syntax.commandSyntax()
    override def perform(c: Context, args: Array[AnyRef]) { }
  }
  var setup: AnonymousCommand = emptyTask
  def add(t: Test) { tests :+= t }
  def run(context: Context) {
    _results = new Results(tests.map(_.run(context, setup)))
  }
  def clear() {
    _results = new Results(Nil)
    tests = Vector[Test]()
    setup = emptyTask
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

case class Test(name: String, command: AnonymousCommand, reporter: AnonymousReporter, expected: AnyRef) {
  def run(context: Context, setup: AnonymousCommand): TestResult = {
    try {
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
