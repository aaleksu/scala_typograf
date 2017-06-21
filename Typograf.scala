import scala.xml.{Elem,XML}
import java.io._

// usage:
// compile: scalac Typograf.scala
// run: scala Typograf
// or
// scala Typograf "- Это \"Типограф\?""
object Typograf {
  def main(args: Array[String]): Unit = {
  	var text: String = ""
    if(args.length > 0) {
      text = args(0)
    }
    else {
      text = readLine("Текст, пожалуйста: ")
    }
    println((new Typograf).send(text))
  }
}

class Typograf(val entityType: Int = 3, 
			   val useBr: Int = 0, 
			   val useP: Int = 1, 
			   val maxNobr: Int = 3, 
			   val quotA: String = "laquo raquo",
			   val quotB: String = "laquo raquo"
			  ) {
  def send(text: String): String = {
    val url = new java.net.URL("http://typograf.artlebedev.ru/webservices/typograf.asmx")
    val outs = wrap(text).getBytes
    val conn = url.openConnection.asInstanceOf[java.net.HttpURLConnection]
    try {
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.setInstanceFollowRedirects(true)
      conn.setRequestProperty("Content-Length", outs.length.toString)
      conn.setRequestProperty("Content-Type", "text/xml")
      conn.getOutputStream.write(outs)
      conn.getOutputStream.close

      val reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))
      val result = XML.loadString(Stream.continually(reader.readLine()).takeWhile(_ != null).mkString("\n"))
      (result \\ "ProcessTextResult").text
    }
    catch {
      case e: Exception => {
        println("ERROR: " + e)
        e.toString
      }
    }
  }

  def wrap(text: String): String = {
    """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
      <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
          <ProcessText xmlns="http://typograf.artlebedev.ru/webservices/">
            <text>""" + text + """</text>
            <entityType>""" + entityType + """</entityType>
            <useBr>""" + useBr + """</useBr>
            <useP>""" + useP + """</useP>
            <maxNobr>""" + maxNobr + """</maxNobr>
            <quotA>""" + quotA + """</quotA>
            <quotB>""" + quotB + """</quotB>
          </ProcessText>
        </SOAP-ENV:Body>
      </SOAP-ENV:Envelope>"""
  }
}
