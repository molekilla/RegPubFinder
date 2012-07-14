package controllers

import play.api.libs.ws._
import play.api.libs.iteratee._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.libs.Akka
import services.RegPubServices
import com.ecyware.web.robot._
import akka.dispatch.Await

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def scrapCompany(name:String) = Action {
      val regPub = RegPubServices().scrapCompany(name)
      val regPubPromise = Akka.asPromise(regPub)
      val promiseResult = regPubPromise.getWrappedPromise
 
    Async {
      promiseResult.map {
        s=> 
          if ( s.dataItems.size > 0 )
          {
            // save into mongodb and get json
            val ficha = RegPubServices().addCompany(s.dataItems)
            val doc = RegPubServices().getByFicha(ficha)
            Ok(doc)
          } else {
            Ok("{}")
          }
      }
    }
         
  }
  
  def search(ops:String, pagePosition:Int, searchText:String) = Action {
    val opsSetting = if ( ops.toLowerCase == "or") false else true
    val searchResults = RegPubServices().searchDocuments(searchText, pagePosition, opsSetting)
    Ok(searchResults)
  }
  
  def findByName(searchText:String) = Action {
    val searchResults = RegPubServices().findByName(searchText)
    Ok(searchResults)
  }
  
  def typeahead(searchText:String) = Action {
    val searchResults = RegPubServices().searchTypeahead(searchText)
    Ok(searchResults)
  }
  
  def about = Action {
    Ok(views.html.about())
  }
  
  def annotate = Action(parse.json) { request =>
    (request.body \ "name").asOpt[String].map { name =>
      Ok("Hello " + name)
    }.getOrElse {
      BadRequest("Missing parameter [name]")
    }
  }
  
  def show(ficha:String) = Action {
    TODO
  }
  
}