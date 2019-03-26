import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.Future

import scala.concurrent.ExecutionContext

class Server(router: Router, host: String, port:Int)(implicit system: ActorSystem, ex: ExecutionContext, mat: ActorMaterializer) {

  def bind(): Future[ServerBinding] = Http().bindAndHandle(router.route, host, port)
}
