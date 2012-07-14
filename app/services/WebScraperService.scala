package services

import play.api.libs.json._
import com.ecyware.web.robot._
import akka.dispatch._

abstract trait WebScraperUtil {
    protected def findCompany(name:String):Future[Store]
}

trait WebScraperService extends WebScraperUtil {
   
  /**  Finds a company in the website of RegPub
   * @param name company
   * */
  protected def findCompany(name:String):Future[Store] = {
    val handler = new RegPubWebHandler
    handler.findCompany(name)
  }
}