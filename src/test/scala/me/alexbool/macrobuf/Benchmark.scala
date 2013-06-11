package me.alexbool.macrobuf

import scala.util.Random

object Benchmark extends App {

  case class BenchmarkMessage(number: Int, optional: Option[Long], repeated: Seq[String], embedded: Seq[Embedded])
  case class Embedded(fieldOne: Int, fieldTwo: Long)

  def generate(count: Int): Seq[BenchmarkMessage] = Stream.continually(generate).take(count).to[Seq]

  def generate =
    BenchmarkMessage(
      number = Random.nextInt(),
      optional = if (Random.nextBoolean()) Some(Random.nextLong()) else None,
      repeated = Stream.continually(Random.alphanumeric.take(32).mkString).take(Random.nextInt(100)).to[Seq],
      embedded = Stream.continually(Embedded(fieldOne = Random.nextInt(), fieldTwo = Random.nextLong())).take(Random.nextInt(100)).to[Seq])

  def run[T](data: Seq[T], serializer: Serializer[Iterable[T]]) {
    val start = System.currentTimeMillis
    serializer.serialize(data)
    val end = System.currentTimeMillis
    println(s"Took ${end - start} millis")
  }

  println("Generating data...")
  val data = generate(10000)

  val reflectionSerializer = Protobuf.serializerForList[BenchmarkMessage]
  val macroSerializer      = Protobuf.macroSerializerForList[BenchmarkMessage]

  println("Reflection")
  run(data, reflectionSerializer)
  println("Macro")
  run(data, macroSerializer)
}
