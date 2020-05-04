package client

import java.io.File

import io.youi.client.HttpClient
import io.youi.http.content.Content
import io.youi.net._
import scribe.Execution.global

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object TestClient {
  def main(args: Array[String]): Unit = {
    val future = send()
    val response = Await.result(future, 15.seconds)
    scribe.info(s"Response: $response")
  }

  def send(): Future[String] = HttpClient
    .url(url"http://localhost:3000")
    .post
    .content(Content.file(new File("data.json")))
    .send()
    .map(_.content.map(_.asString).getOrElse("No content returned"))
}
