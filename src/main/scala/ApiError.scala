import akka.http.scaladsl.model.{StatusCode, StatusCodes}

final case class ApiError private(statusCode: StatusCode, message: String)

object ApiError {
  private def apply(statusCode: StatusCode, message: String): ApiError = new ApiError(statusCode, message)
  val generic: ApiError = new ApiError(StatusCodes.InternalServerError, message = "Unknown Error.")
  val emptyTitleField: ApiError = new ApiError(StatusCodes.BadRequest, message = "The title field cannot be left blank.")

  def todoNotFound(id: String): ApiError =
    new ApiError(StatusCodes.NotFound, s"THe todo with id: $id could not be found.")
}