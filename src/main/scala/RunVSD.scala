import java.io.{File, PrintWriter}

import com.clearnlp.dependency.{DEPLib, DEPNode}
import edu.colorado.clear.wsd.classifier.VerbNetClassifier
import edu.colorado.clear.wsd.util.ClearNLPInterface

import scala.collection.JavaConversions._
import scala.io.Source

object RunVSD {

  def runVsd(inputFile: String, outputFile: String) {
    val dataPath = "vndata"
    val classifier = new VerbNetClassifier(new File(dataPath), false)
    val cnlp = new ClearNLPInterface

    val inputLines = Source.fromFile(inputFile).getLines()
    val outFile = new PrintWriter(new File(outputFile))

    for (line <- inputLines) {
      val sb = new StringBuilder

      for (sentence <- line.split("\t")) {
        val tree = cnlp.process(sentence)
        classifier.classify(tree)

        for (node: DEPNode <- tree if node.hasHead) {
          val sense = node.getFeat(DEPLib.FEAT_VN)

          sb.append(node.form)

          if (sense != null)
            sb.append("/" + sense)

          sb.append(" ")
        }

        sb.append("\t")
      }

      outFile.println(sb.toString().trim())
    }

    outFile.close()
  }

}
