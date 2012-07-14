package services

import com.ecyware.web.robot._
import akka.dispatch._
import play.api.libs.json._
import play.api.Logger._

object RegPubServices
{
    def apply() = new RegPubServices()
}

class RegPubServices extends SearchService 
with DataModelService 
with WebScraperService {


    override def addCompany(items:Map[String,Object]):String = {
      super.addCompany(items)
    }
    def scrapCompany(name:String):Future[Store] = {
        this.findCompany(name)
    }
       
    def searchDocuments(query:String, pagePosition:Int, ops:Boolean):JsValue = {
        search(query, pagePosition, isExact=ops) \ "hits"
    }

    def searchTypeahead(query:String):JsValue = {
        val hits = typeahead(query)
        val companyNames = hits.map( hit => (hit \\ "fields").map( h => Map("item" -> h \ "nombre"))).flatten
        Json.toJson(companyNames)
    }

    def getByFicha(ficha:String):JsValue = {
      Json.parse(getDocumentByFicha(ficha))
    }
    def findByName(query:String):JsValue = {
        val hits = typeahead(query)
        val mappedItems = hits.map( hit => (hit \\ "fields").map( h =>  h \ "ficha")).flatten

        try {

            val ficha = mappedItems.head.asOpt[String]
            // look in data service
            val document = this.getDocumentByFicha(ficha.getOrElse(""))
            val jsonDoc = Json.parse(document)
            
            val directores = (jsonDoc \ "directores").as[List[String]] 
            val subscriptores = (jsonDoc \ "subscriptores").as[List[String]]
            val items = (directores ++ subscriptores).distinct
            val rels = relationships(query, items)
            val result = Map("document" -> jsonDoc, "stats" -> rels)
            Json.toJson(result)
        }
        catch {
            case e: Exception =>
            Json.parse("{}")
        }
    }
}

object Config { 

    import org.elasticsearch.common.transport.InetSocketTransportAddress

    val elasticSearchAddress = new InetSocketTransportAddress("localhost",9300)
    val regpubDb = "webdata"
    val regpubColl = "regpub"
}