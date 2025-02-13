# IN4060
## Mandatory Exercise no. 2

---
**Delivery file:** Simpsons.java or Simpsons.py

### File should take two arguments
1. path to an RDF file (URL or local path)
2. filename to which the output shall be written

You shall assume that the RDF serialisation of both files is correctly indicated by their file extension:
- `.rdf` = RDF/XML
- `.ttl` = Turtle
- `.n3` = N3
- `.nt` = N-Triples

### Create a model from the input file
1. add triples to this model
2. add more triples to the model based on data in the model
3. output the model to file

The steps must be carried out in this sequence. Each step is explained in detail below.

### Read input
Your program shall read the RDF file given in the first argument to the program and create a Jena model or rdflib Graph from it.

**Tip Jena:** It seems that Jena is not so good at discovering the RDF serialisation used in files
by itself, especially for files addressed with an URL. A solution to this problem is to make a
method that gets the file extension of the filename by the use of string functions and returns
the appropriate RDF serialisation format in Jena’s predefined strings. You can then use your
method both for reading input and writing to file in the correct serialisation format.

> You can assume that the input file has defined the following prefix–namespaces:
>
> | Prefix | Namespace                                   |
> |--------|---------------------------------------------|
> | xsd    | http://www.w3.org/2001/XMLSchema#           |
> | rdf    | http://www.w3.org/1999/02/22-rdf-syntax-ns# |
> | sim    | http://www.ifi.uio.no/IN3060/simpsons#      |
> | fam    | http://www.ifi.uio.no/IN3060/family#        |
> | foaf   | http://xmlns.com/foaf/0.1/                  |

**Tip rdflib:** After parsing the input file to a graph you can iterate through `graph.namespaces()`
to get access to `(prefix, namespace)` tuples. You need to store the prefixes as variables with
`prefix = Namespace(namespace)` then bind them to the graph with `graph.bind("prefix",
prefix)` before you can use them when adding triples to the graph. See the EX example in the
lecture slides.

### Adding information
Your program shall then _add_ the following information about Maggie, Mona, Abraham and
Herb, whose identifiers are respectively `sim:Maggie`, `sim:Mona`, `sim:Abraham` and `sim:Herb`,
to the model read from the input file. You will need to use the class `foaf:Person`, the predicates
`rdf:type`, `foaf:age`, `foaf:name`, `fam:hasSpouse` and `fam:hasFather`, and the standard xsd
datatypes.<sup>1</sup> You shall assume that the prefixes sim and fam are defined in the model that
your program has read in the section above, so do not hardcode these namespaces in your
program, but get them from the model you have created. (Hint: Model is a subinterface of
`PrefixMapping`.)

Add the following information to the model using Jena methods:
- Maggie is a person, whose name is _"Maggie Simpson"_ and age is **1**. The datatype of the
  age value is `xsd:int`.
- Mona is a person, whose name is _"Mona Simpson"_ and age is **70**. The datatype of the age
  value is `xsd:int`.
- Abraham is a person, whose name is _"Abraham Simpson"_ and age is **78**. The datatype of
  the age value is `xsd:int`.
- Abraham is the spouse of Mona, and Mona is the spouse of Abraham.
- Herb is a person.
- Herb has a father (but we do not know who it is, i.e., use a blank node.).

<sup>1</sup> _the intended meaning of these classes and predicates is "as expected" and is listed in the previous mandatory exercise._

### Locate, read and write information
For each person in the model, i.e., for every resource of type `foaf:Person`, add the following
information according to the person’s age:
- if `age < 2 year`, then add a triple stating that the person is of type `fam:Infant`,
- if `age < 18 year`, then add a triple stating that the person is of type `fam:Minor`,
- if `age > 70 year`, then add a triple stating that the person is of type `fam:Old`.

**Example:** If the input model contains the triples
```
sim:Someone rdf:type foaf:Person ;
            foaf:age "75"^^xsd:int .
```
then your program shall add the triple
```
sim:Someone rdf:type fam:Old .
```
to the model

### Write to file
Finally, write the model to the file given as second argument to the program and in the RDF
serialisation as specified by the file’s extension.

### Executing program: Jena
> Program must successfully compile with the command
> ```bash
> javac -cp "lib/*:." src/main/java/no/uio/ifi/in4060/oblig/Simpsons.java
> ```
> where `path/to/jena/lib/*` contains the Jena jar files

> Running the command
> ```bash
> java -cp "lib/*:." Simpsons src/main/java/no/uio/ifi/in4060/oblig/simpsons.ttl src/main/java/no/uio/ifi/in4060/oblig/output.ttl
> ```
> where `oblig/2/simpsons.ttl` is a file containing the prefixes as specified in the exercise text
(rdf file from oblig1 file should do), shall produce an RDF Turtle file as specified by the exercise.
To do this you can use the following `Makefile`:
> ```makefile
> JAVA_CP = "lib/jena/lib/*:."
> SIMPSONS_FILE = oblig/2/simpsons.ttl
> 
> java = java -cp $(JAVA_CP)
> javac = javac -cp $(JAVA_CP)
> 
> %.class: %.java
>       @$(javac) $<
> 
> output.ttl: Simpsons.class
>       @$(java) $(basename $(<F)) $(SIMPSONS_FILE) $@
> 
> run_test: Test.class
>       @$(java) $(basename $(<F)) $(SIMPSONS_FILE) $@
> ```
> Change the variable `JAVA_CP` to contain the path to where you keep your Jena jar files. Placing
this Makefile in the same folder as your java program and writing
> ```bash
> make output.ttl
> ```
> should compile your java program and create the file `output.ttl` as specified in this exercise.
>
> Below is a tiny **test program** called `Test.java` you can use to test the above `Makefile` and that
compiling and running java works. Running
> ```bash
> make run_test
> ```
> from inside the same folder as where the Makefile and Test.java is located should output
> the two input arguments given to the program.
> ```java
> public class Test{
>   public static void main(String args[]){
>       System.out.println("Hello, the two input arguments are: \n\t(1) "
>           + args[0] + " \n\t(2) "
>           + args[1]);
>   }
> }
> ```
> The `Makefile` and `Test.java` is available for download at http://www.uio.no/studier/emner/matnat/ifi/IN3060/v21/obliger/oblig2_files/.

Note that you can only upload the output of running your java program to Mr. Oblig, and not
the java or python program. You should be careful to test the program with different outputs,
e.g., with different values of the prefixes sim and fam, and different ages—and of course the
limit cases, e.g., age 17 and age 18—as input, before handing in.

### Executing program: rdflib
```bash
# package needed
pip install rdflib
```

> Navigate to the folder where you have your python file and open the commandline/terminal.
> Your program should run successfully with the command:
> ```bash
> python3 Simpsons.py oblig/2/simpsons.ttl output.ttl
> ```
> where `oblig/2/simpsons.ttl` is the path to the file containing the prefixes and `output.ttl` is the
> path to the file containing the output of your program. Make sure that the rdflib package is
> installed in the environment you run the program in (venv, conda, etc).

If you chose to solve the mandatory with rdflib then the prefix "rdf" will give a yellow question
mark in Mr. Oblig. This is because the rdf prefix is technically not used in the serialization
of the graph. Rdflib will therefore omitt the prefix. This is known and will not affect the final
grade.