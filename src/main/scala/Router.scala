import akka.http.scaladsl.server.{Directives, Route}

import scala.util.{Failure, Success}

trait Router {
  def route: Route

}

class TodoRouter(todoRepository: TodoRepository) extends Router with Directives with TodoDirectives with ValidatorDirectives {
  //These imports are used for parsing
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("todos"){
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(todoRepository.all()) { todos =>
            complete(todos)
        }
      } ~ post {//POST: Expect it to be a CreateTodo, save if no error
        entity(as[CreateTodo]) { createTodo =>
          validateWith(CreateTodoValidator)(createTodo){
            handleWithGeneric(todoRepository.create(createTodo)) { todos =>
              complete(todos)
            }
          }
        }
      }
    }~ path(Segment) { id: String =>
      put {
        entity(as[UpdateTodo]) { updateTodo =>
          validateWith(UpdateTodoValidator)(updateTodo){
            handle(todoRepository.update(id, updateTodo)) {
              case TodoRepository.TodoNotFound(_) =>
                ApiError.todoNotFound(id)
              case _ =>
                ApiError.generic
            }{ todo =>
              complete(todo)
            }
          }

        }
      }
    } ~ path("done"){
      get {
        handleWithGeneric(todoRepository.done()) { todos =>
          complete(todos)
        }
      }
    } ~ path ("pending"){
      get {
        handleWithGeneric(todoRepository.pending()) { todos =>
          complete(todos)
        }
      }
    }
  }
}