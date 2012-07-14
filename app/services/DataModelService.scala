package services

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb._
import com.mongodb.ServerAddress
import com.mongodb.util._
import scalaj.collection.s2j._

abstract trait RegpubDataUtil {
    protected def getDocumentByFicha(ficha:String):String
    protected def addCompany(item: Map[String,Object]):String
}


trait DataModelService extends RegpubDataUtil {
    val mongoCollection = MongoConnection()("webdata")("regpub")

    protected def addCompany(items: Map[String,Object]):String = {
      val ficha = if (items.containsKey("ficha")) Some(items("ficha").toString) else None
   
      if ( ficha.isDefined ) { 
        val document = items.asDBObject
        val existingItems = mongoCollection.find(MongoDBObject("ficha" -> ficha)) 
        if ( existingItems.length == 0 )
        {
          mongoCollection += document
        }
      }
      
      ficha.getOrElse("")
    }
    
    protected def getDocumentByFicha(ficha:String):String = {
        mongoCollection.ensureIndex("ficha")
        val cursor = mongoCollection.find(MongoDBObject("ficha" -> ficha))
        val document = if ( cursor.hasNext ) Some(cursor.next.asDBObject) else None
        if ( document.isDefined )
        {
            JSON.serialize(document)
        } else {
            "{}"
        }
    }

}
