import org.scalatest._
import scala.xml.transform._
import scala.xml._

class XMLSpec extends FlatSpec {

  val xml = <root><sub horse="Steed">Gumby</sub></root>

  "our xml" should "have a gumby attribute" in {

    assert((xml \ "sub").text == "Gumby")
    assert((xml \\ "@horse").text == "Steed")

    object ChangeGumby extends RewriteRule {
      override def transform(node : Node):Node = node match {

        case Elem(prefix, "sub", attribs, scope, children ) =>
          Elem(prefix, "sub", attribs, scope, true, Text("Great Gumby"))

        case other => other
      }
    }

    object xf extends RuleTransformer(ChangeGumby)

    val changed = xf(xml)

    assert((changed \\ "@horse").text == "Steed")

    assert((changed \ "sub").text == "Great Gumby")

    //
//    val pp = new PrettyPrinter(width = 2, step = 1)
//    Console println (pp format res)

//    val xmlChanged = change(xml)
//
//    assert((xml \\ "@gumby").text == "scrappy")

  }
}
