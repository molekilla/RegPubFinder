package services

import play.api.libs.json._
import org.elasticsearch.action.index._
import org.elasticsearch.common.settings._
import org.elasticsearch.client.transport._
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.action.search._
import org.elasticsearch.index.query._
import org.elasticsearch.search.sort._
import org.elasticsearch.common.xcontent._
import org.elasticsearch.search._
import org.elasticsearch.node.{Node,NodeBuilder}
import org.elasticsearch.index.query.FilterBuilders._
import org.elasticsearch.index.query._
import org.elasticsearch.search.facet._

abstract trait SearchUtil {
    protected def search(query:String, pagePosition:Int, isExact:Boolean):JsValue
    protected def typeahead(query:String):Seq[JsValue]
    protected def relationships(query:String, directores:Seq[String]):JsValue
}

trait SearchService extends SearchUtil {
    import NodeBuilder.nodeBuilder

    val settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build()
    val regPubIndex = "regpub"

    // regpub fields
    val fields = List("fechaRegistro", "ficha","documento","nombre", "dignatarios","directores","subscriptores","tasaUnicaAgenteResidente")

  /** Finds the count of relationships for each director in a company
   * @param query Query text to look up
   * @param isExact True for AND , False for OR
   *  */
    protected def search(query:String,pagePosition:Int, isExact:Boolean):JsValue = {
        val client = new TransportClient(settings).addTransportAddress(Config.elasticSearchAddress)
        val operator = if ( isExact ) QueryStringQueryBuilder.Operator.AND else QueryStringQueryBuilder.Operator.OR
        val queryTerm = QueryBuilders.queryString(query)
        .useDisMax(true)
        .autoGeneratePhraseQueries(false)
        .defaultOperator(operator)
        .field("nombre", 0.99f)
        .field("dignatarios", 0.8f)
        .field("subscriptores",0.5f)
        .field("directores",0.7f)
        val builder = client.prepareSearch(regPubIndex)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        //.setQuery(termQuery("_all", query))
        .setQuery(queryTerm)
        
        val pageSize = 20
        val pageFrom = (if (pagePosition == 0) 1 else (scala.math.abs(pagePosition) - 1)) * pageSize
        val response = builder.setFrom(pageFrom).setSize(pageSize).execute().actionGet()
        client.close
        Json.parse(response.toString)
    }
    
   /** Finds the count of relationships for each director in a company
   * @param query Query text to look up
   * @param directores Sequence of directores to look up
   *  */
    protected def relationships(query:String, directores:Seq[String]):JsValue = {
        val client = new TransportClient(settings).addTransportAddress(Config.elasticSearchAddress)
     
        val queryWithAnd = (name:String) =>   { 
           QueryBuilders.queryString(name).defaultOperator(QueryStringQueryBuilder.Operator.AND)
        }
        val mainQueryTerm = queryWithAnd(query)
        
        val builder = client.prepareSearch(regPubIndex)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        //.setQuery(termQuery("_all", query))
        .setQuery(QueryBuilders.matchAllQuery)
        .addField("nombre")
        .setNoFields
        //.addFacet(FacetBuilders.termsFacet("directores").field("directores").field("subscriptores") )
        directores.foreach { 
          d =>
            val filterQuery = queryWithAnd(d)
            val filterTerm = FilterBuilders.queryFilter(filterQuery)
             builder.addFacet(
                 FacetBuilders.filterFacet(d).filter(filterTerm)
                 )
        }

        val response = builder.execute().actionGet()  
        client.close
        Json.parse(response.toString) \ "facets"
    }
    

  /** Finds 10 matches and returns an array of company names
   * @param query Query text to look up
   *  */
    protected def typeahead(query:String):Seq[JsValue] = {
        val client = new TransportClient(settings)
        .addTransportAddress(Config.elasticSearchAddress)

        val queryTerm = QueryBuilders.queryString(query)
        .useDisMax(true)
        .autoGeneratePhraseQueries(true)
        .field("nombre", 1)
        .field("dignatarios", 0.8f)
        .field("subscriptores",0.7f)
        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
        val builder = client.prepareSearch(regPubIndex)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(queryTerm)
        .addField("nombre")
        .addField("ficha")
        .addField("directores")

        val response = builder.setFrom(0).setSize(10).execute().actionGet()
        client.close

        Json.parse(response.toString) \ "hits" \\ "hits"

    }
}