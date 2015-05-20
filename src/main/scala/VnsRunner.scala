object VnsRunner {

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("usage: sbt \"run do_what inputFile\"")
      println("  do_what:")
      println("    vsd        run VerbNet sense disambiguation (generates .vsd file)")
      println("    srl        run semantic role labeling (generates .srl file)")
      println("    convert    merge .vsd and .srl files into a .vns file")
      println("    all        do all of the above")
      println("  inputFile: this is the input file (just the sentences, not vsd or srl)")
      System.exit(0)
    }

    val doWhat = args(0)
    val inputFile = args(1)
    val vsdFile = inputFile + ".vsd"
    val srlFile = inputFile + ".srl"
    val outputFile = inputFile + ".vns"

    doWhat match {
      case "vsd" =>
        RunVSD.runVsd(inputFile, vsdFile)
      case "srl" =>
        RunSRL.decode(inputFile, srlFile)
      case "convert" =>
        RunSRL.merge(vsdFile, srlFile, outputFile)
      case "all" =>
        RunVSD.runVsd(inputFile, vsdFile)
        RunSRL.decode(inputFile, srlFile)
        RunSRL.merge(vsdFile, srlFile, outputFile)
    }
  }

}
