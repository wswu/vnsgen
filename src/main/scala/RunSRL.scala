import java.io._

import com.clearnlp.nlp.NLPGetter
import com.clearnlp.nlp.decode.SRLDecoder

import scala.collection.immutable.VectorBuilder
import scala.io.Source

object RunSRL {

  def decode(inputFile: String, outputFile: String): Unit = {
    val decoder = new SRLDecoder() {
      val tokenizer = NLPGetter.getTokenizer("en")
      val segmenter = NLPGetter.getSegmenter("en", tokenizer)
      val components = NLPGetter.getComponents("general-en", "en", getModes("raw"))

      def decode(inputFile: String, outputFile: String) {
        val out = new PrintStream(new File(outputFile))
        val sentences = Source.fromFile(inputFile).getLines().flatMap(_.split("\t"))

        for (sent <- sentences) {
          decode(new BufferedReader(new StringReader(sent)), out, segmenter, components)
        }

        out.close()
      }
    }

    decoder.decode(inputFile, outputFile)
  }

  def merge(vsdFile: String, srlFile: String, outputFile: String) {
    val vsdLines = Source.fromFile(vsdFile).getLines().map(_.split("\t"))
    val srlLines = Source.fromFile(srlFile).getLines()
    val fout = new PrintWriter(new File(outputFile))

    val sent1 = new VectorBuilder[String]()
    val sent2 = new VectorBuilder[String]()
    var first = true

    while (srlLines.hasNext) {
      val line = srlLines.next()

      if (line.isEmpty) {
        first = !first
        if (first) {
          val vsd = vsdLines.next()
          val labeled1 = process(sent1.result(), vsd(0))
          val labeled2 = process(sent2.result(), vsd(1))
          fout.println(labeled1 + "\t" + labeled2)

          sent1.clear()
          sent2.clear()
        }
      } else {
        if (first) {
          sent1 += line
        } else {
          sent2 += line
        }
      }
    }

    fout.close()
  }

  /**
   * Processes one sentence from SRL output.
   *
   * @param sentence lines in a Vector("index word lemma POS feats headIndex depRel role", ...)
   *                 (where the spaces between the words are actually tabs)
   * @return a string in the form "word[|role1[;role2] ] ..." if it has a role
   */
  def process(sentence: Vector[String], vsdSentence: String): String = {
    val grid = sentence.map { line =>
      val arr = line.split("\t")
      val word = arr(1)
      val pos = arr(3)
      val depRel = arr(6)
      val roles = arr(7)
      Array(word, pos, depRel, roles)
    }

    // if a preposition gets labeled as an argument, transfer the argument to its pobj
    for (i <- 0 until grid.length) {
      if (grid(i)(3) != "_" && grid(i)(1) == "IN" || grid(i)(1) == "RP") {
        //
        var pobjIndex = i
        while (pobjIndex < grid.length && grid(pobjIndex)(2) != "pobj") {
          pobjIndex += 1
        }

        // pobjIndex will go OOB in "the dog is looking on" (on = prep)
        if (pobjIndex < grid.length) {
          grid(pobjIndex)(3) = grid(i)(3)
          grid(i)(3) = "_"
        }
      }
    }

    // rehyphenate words
    /*
    go through each line in the grid
    if it is a dash, then
      remove the role for the previous word
      if anything else depends on that word, remove their role too
      join previous word with next word
      if any word's head index is greater than that of the dash, then
        shift that index down by two
     */
    for (i <- 1 until grid.length if grid(i)(0) == "-") {
      grid(i - 1)(3) = "_" // remove roles for the previous word

      for (j <- 0 until grid.length if grid(j)(3).contains(i + ":")) {
        val roles = grid(j)(3).split(';').filterNot(_.startsWith(i + ":"))
        grid(j)(3) = roles.mkString(";")
      }

      grid(i + 1)(0) = grid(i - 1)(0) + "-" + grid(i + 1)(0)
      grid(i - 1)(0) = null
      grid(i)(0) = null

      for (j <- 0 until grid.length) {
        val roles = grid(j)(3).split(';').map { x =>
          val arr = x.split(':') // arr(0) = headIndex, arr(1) = argNumber
          if (arr.length > 1 && arr(0).toInt > i)
            (arr(0).toInt - 2) + ":" + arr(1)
          else
            x
        }
        grid(j)(3) = roles.mkString(";")
      }
    }

    // attach SRL suffixes
    val newGrid = vsdSentence.split(' ').zip(grid).filterNot(_._2(0) == null)
    newGrid.map { case (vsdWord, Array(word, pos, depRel, roles)) =>
      val vsdTag = if (vsdWord.contains('/')) vsdWord.substring(vsdWord.indexOf('/')) else ""
      val suffix = roles match {
        case "_" => "" // no role
        case _ => "|" + roles // something other than '_' indicates there are roles
      }
      word + vsdTag + suffix
    }.mkString(" ")
  }

}
