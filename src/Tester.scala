import org.nlogo.api.{ CommandTask, ReporterTask, LogoException, Context }

// main object
object Tester {
  val tests = collection.mutable.ListBuffer[Test]()
  var setup: Option[CommandTask] = None
  var results: Iterable[TestResult] = Nil
  def addTest(t: Test) { tests += t }
  def run(context: Context)  {
    results = tests.map(t => t.run(context))
    clear()
  }
  private def clear() { tests.clear(); setup = None }
  def successes = results.collect{ case t: Pass => t }
  def failures = results.collect{ case t: Fail => t }
  def errors = results.collect{ case t: Error => t }
  def simpleSummary =
    (if(failures.size + errors.size == 0) "Test Run Passed" else "Test Run Failed") + " - " +
    "Total "+results.size+", Failed "+failures.size+", Errors "+errors.size+", Passed "+successes.size
  def summary =
    simpleSummary +
    (if(failures.size>0) ("\n"+failures.map(_.report).mkString("\n")) else "") +
    (if(errors.size>0) ("\n"+errors.map(_.report).mkString("\n")) else "")
  def details =
    simpleSummary + "\n" + results.map(_.report).mkString("\n")
}

// helpers
case class Test(name: String, command: CommandTask, reporter: ReporterTask, expectedResult: AnyRef) {
  import org.nlogo.nvm
  def run(c: Context) = {
    val context = c.asInstanceOf[nvm.ExtensionContext].nvmContext
    val workspace = c.asInstanceOf[nvm.ExtensionContext].workspace
    try {
      workspace.clearAll()
      Tester.setup.foreach(_.asInstanceOf[nvm.CommandLambda].perform(context, Array()))
      command.asInstanceOf[nvm.CommandLambda].perform(context, Array())
      val actualResult = reporter.asInstanceOf[nvm.ReporterLambda].report(context, Array())
      if(actualResult == expectedResult) pass else fail(actualResult, expectedResult)
    }
    catch {
      case e: LogoException =>
        err(e)
    }
  }
  def pass =
    Pass(this)
  def fail(actual: Any, expected: Any) =
    Fail(this, "expected: " + expected + " but got: " + actual)
  def err(t: LogoException) =
    Error(this, t)
}

trait TestResult {
  val test: Test
  def report: String
}
case class Pass(test: Test) extends TestResult {
  def report: String = "PASS '" + test.name + "'"
}
case class Fail(test: Test, reason: String) extends TestResult {
  def report: String = "FAIL '" + test.name + "' " + reason
}
case class Error(test: Test, e: LogoException) extends TestResult {
  def report: String = "ERROR '" + test.name + "' " + e.getMessage
}
