# VnsGen

This is basically a wrapper around two existing systems:

* VerbNet Sense Disambiguation using ClearWSD
* Semantic Role Labeling using ClearNLP

See [ClearWSD GitHub page](https://github.com/jgung/ClearWSD) and 
[ClearNLP GitHub page](https://github.com/clir/clearnlp) for more information. 
Note that ClearWSD depends on an earlier version of ClearNLP.

## Instructions

Download the "VerbNet Classification Data and Models" from the ClearWSD site and extract the directory `vndata`.

Download [WordNet-3.0](http://wordnetcode.princeton.edu/3.0/WordNet-3.0.tar.gz) and extract into `vndata`.

## Usage

You can run it from SBT as follows:

* `sbt "run vsd inputFile"`
* `sbt "run srl inputFile"`
* `sbt "run merge inputFile"`
* `sbt "run all inputFile"`

where `inputFile` is the name of the input file. Running `merge` will merge the results of the VSD and SRL steps into a `.vns` file.

## Format for the Input File

Each line of the input file should have two sentences separated by a tab.

For example, if the input file is:
> A young girl is dancing    A young girl is standing on one leg

The output file will contain:
> A young girl|5:A0=PAG is/110.1 dancing/47.3    A young girl|5:A1=PPT is/110.1 standing/47.6 on one leg|5:A2=LOC
