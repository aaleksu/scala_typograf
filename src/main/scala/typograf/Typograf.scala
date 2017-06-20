package typograf

import scala.xml.{Elem,XML}
import java.io._

object Typograf extends App {
  val client = new SoapClient
  println(client.send("""- Это типограф? Нет, это "Типограф"!"""))
}

class SoapClient {
  def send(text: String): Unit = {
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

      println((result \\ "ProcessTextResult").text)
    }
    catch {
      case e: Exception => println("ERROR: " + e)
    }
  }

  def wrap(text: String): String = {
    s"""<?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Body>
    <ProcessText xmlns="http://typograf.artlebedev.ru/webservices/">
      <text>$text</text>
        <entityType>3</entityType>
        <useBr>0</useBr>
        <useP>1</useP>
        <maxNobr>3</maxNobr>
        <quotA>laquo raquo</quotA>
        <quotB>laquo raquo</quotB>
      </ProcessText>
    </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>"""
  }
}
