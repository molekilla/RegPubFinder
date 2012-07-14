import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class TestSpec extends Specification {

  "Requesting Varela from search" should {
     "get me some valid companies in json data" in {
      val Some(result) = routeAndCall(FakeRequest(GET, "/search/AND/varela/1"))
      
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      charset(result) must beSome("utf-8")
   //contentAsString(result) must contain("Hello Bob")
   }
}

  "Requesting to scrap company" should {
    "succeed" in {
      val Some(result) = routeAndCall(FakeRequest(GET, "/scrapCompany/ADMIOS"))
      // will always fail because call is async!
      status(result) must equalTo(OK)
    }
  }
}