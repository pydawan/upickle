package ujson.circe

import ujson.{Transformable, Visitor}
import io.circe.{Json, JsonNumber}
object CirceJson extends ujson.AstTransformer[Json]{

  override def transform[T](j: Json, f: Visitor[_, T]) = j.fold(
    f.visitNull(),
    if (_) f.visitTrue() else f.visitFalse(),
    n => f.visitNumRaw(n.toDouble, -1),
    f.visitString(_),
    arr => transformArray(f, arr),
    obj => transformObject(f, obj.toList)
  )

  def visitArray(index: Int) = new AstArrVisitor(x => Json.arr(x:_*))

  def visitObject(index: Int) = new AstObjVisitor(vs => Json.obj(vs:_*))

  def visitNull(index: Int) = Json.Null

  def visitFalse(index: Int) = Json.False

  def visitTrue(index: Int) = Json.True

  def visitNum(s: CharSequence, decIndex: Int, expIndex: Int, index: Int) = {
    Json.fromJsonNumber(JsonNumber.fromString(s.toString).get)
  }

  def visitString(s: CharSequence, index: Int) = Json.fromString(s.toString)
}