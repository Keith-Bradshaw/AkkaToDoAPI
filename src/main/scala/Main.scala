import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

object Main {
  def main(args: Array[String]) {
    val host = "0.0.0.0"
    val port = 9000


    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val todoRepository = new InMemoryTodoRepository(Seq(
      Todo("1", "Keep Learning About Akka", "Tutorials!!!", false),
      Todo("2", "Stand Up @ 11:30", "Updates for the day", true)
    ))


    val router = new TodoRouter(todoRepository)
    val server = new Server(router, host,port)

    val bindingFuture = server.bind()

    println(s"Server online at http://${host}:${port}/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}