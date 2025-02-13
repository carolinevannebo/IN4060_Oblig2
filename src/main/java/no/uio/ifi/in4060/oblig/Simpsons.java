package no.uio.ifi.in4060.oblig;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;

public class Simpsons {
    private static String inputPath;
    private static String outputPath;
    private static String serializationLanguage = null;
    private static InputStream inputStream;
    private static Reader reader;

    private static Model model;
    private static Map<String, String> namespaces;

    private static Resource foafPerson;
    private static Property rdfType;
    private static Property foafAge;
    private static Property foafName;
    private static Property famHasSpouse;
    private static Property famHasFather;
    private static Property famInfant;
    private static Property famMinor;
    private static Property famOld;

    private static Resource maggie;
    private static Resource mona;
    private static Resource abraham;
    private static Resource herb;

    public static void main(String[] args) {
        System.out.println("\n-------------CHECKING ARGUMENTS--------------");
        checkArguments(args);

        System.out.println("\n---------------CHECKING FILE-----------------");
        locateInputFile();
        checkSerializationFormat();

        System.out.println("\n---------------BUILDING MODEL----------------");
        buildModel();

        System.out.println("\n-------------ADDING INFORMATION--------------");
        addInformationToModel();
    }

    private static void buildModel() {
        model = ModelFactory.createDefaultModel();
        model.read(inputStream, null, serializationLanguage);
        //model.read(reader, null, serializationLanguage); // todo: don't know which to use
        namespaces = model.getNsPrefixMap();

        // define properties
        rdfType = getProperty("rdf", "type");
        foafAge = getProperty("foaf", "age");
        foafName = getProperty("foaf", "name");
        famHasSpouse = getProperty("fam", "hasSpouse");
        famHasFather = getProperty("fam", "hasFather");

        // new properties
        famInfant = model.createProperty("fam", "Infant");
        famMinor = model.createProperty("fam", "Minor");
        famOld = model.createProperty("fam", "Old");

        // define resources
        foafPerson = getResource("foaf", "Person");
        maggie = getResource("sim", "Maggie");
        mona = getResource("sim", "Mona");
        abraham = getResource("sim", "Abraham");
        herb = getResource("sim", "Herb");
    }

    private static void addInformationToModel() {
        maggie.addProperty(rdfType, foafPerson)
                .addProperty(foafName, "Maggie Simpson")
                .addProperty(foafAge, getXSDInt(1));
        mona.addProperty(rdfType, foafPerson)
                .addProperty(foafName, "Mona Simpson")
                .addProperty(foafAge, getXSDInt(70))
                .addProperty(famHasSpouse, abraham);
        abraham.addProperty(rdfType, foafPerson)
                .addProperty(foafName, "Abraham Simpson")
                .addProperty(foafAge, getXSDInt(78))
                .addProperty(famHasSpouse, mona);
        herb.addProperty(rdfType, foafPerson)
                .addProperty(famHasFather, foafPerson);

        //Iterator<Statement> foafAgeIterator = model.listStatements(maggie, foafName, (RDFNode) null);
        //Iterator<Statement> foafAgeIterator = model.listStatements((Resource) null, (Property) model.getProperty(fam+"hasFamilyMember"), (RDFNode) null);
        //Iterator<Statement> foafAgeIterator = model.listStatements((Resource) null, (Property) rdfType, foafPerson);
        //Iterator<Statement> foafAgeIterator = model.listStatements((Resource) null, (Property) famHasFather, (Resource) null);
        Iterator<Statement> foafAgeIterator = model.listStatements((Resource) null, (Property) foafAge, (RDFNode) null);
        while (foafAgeIterator.hasNext()) {
            Statement statement = foafAgeIterator.next();
            Resource subject = statement.getSubject();

            if (!subject.hasProperty(rdfType, foafPerson)) break;
            addAgeGroup(subject);
        }

        Iterator<Statement> rdfTypeIterator = model.listStatements((Resource) null, (Property) rdfType, (RDFNode) null);
        while (rdfTypeIterator.hasNext()) {
            Statement statement = rdfTypeIterator.next();
            printStatement(statement);
        }

    }

    private static void addAgeGroup(Resource person) {
        int age = person.getProperty(foafAge).getObject().asLiteral().getInt();

        if (age < 2) {
            person.addProperty(rdfType, famInfant);
        } else if (age < 18) {
            person.addProperty(rdfType, famMinor);
        } else if (age > 70) {
            person.addProperty(rdfType, famOld);
        }
    }

    private static RDFNode getXSDInt(int number) {
        return model.createTypedLiteral(number, XSDDatatype.XSDint);
    }

    private static void printStatement(Statement statement) {
        System.out.println(statement.getSubject() + " " +
                statement.getPredicate() + " " +
                statement.getObject().toString()
        );
    }

    private static Resource getResource(String prefix, String identifier) {
        String namespace = namespaces.get(prefix);
        return model.getResource(namespace + identifier);
    }

    private static Property getProperty(String prefix, String identifier) {
        String namespace = namespaces.get(prefix);
        return model.getProperty(namespace + identifier);
    }

    private static void checkSerializationFormat() {
        if (inputPath.endsWith(".rdf")) {
            serializationLanguage = "RDF/XML";
        } else if (inputPath.endsWith(".n3")) {
            serializationLanguage = "N3";
        } else if (inputPath.endsWith(".nt")) {
            serializationLanguage = "N-TRIPLE";
        } else {
            throw new Error("\n-------------UNKNOWN SERIALIZATION FORMAT-------------" +
                    "\nSupported formats are: .rdf (RDF/XML), .n3 (N-TRIPLE), .nt (N-TRIPLE)" +
                    "\nApparently Jena has removed Turtle \uD83D\uDE43 sorry for the inconvenience..." +
                    "\n-----------------------------------------------------"
            );
        }
        System.out.println("Using serialization language: " + serializationLanguage);
    }

    private static void locateInputFile() {
        File inputFile = new File(inputPath);
        try {
            inputStream = new FileInputStream(inputPath);
            reader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            try {
                inputPath = System.getProperty("user.dir") + "/src/main/java/no/uio/ifi/in4060/oblig/" + inputPath;
                inputStream = new FileInputStream(inputPath);
                inputFile = new File(inputPath);
                reader = new FileReader(inputFile);
            } catch (FileNotFoundException ex) {
                throw new Error("\n-------------COULD NOT FIND FILE-------------" +
                        "\nPlease provide the correct path to the RDF file" +
                        "\n-------------------------------------------\n" + ex
                );
            }
        }
        inputFile = new File(inputPath);
        System.out.println("Located input RDF file: " + inputFile.getAbsolutePath());
    }

    private static void checkArguments(String[] args) {
        if (args.length != 2) {
            throw new Error("\n-------------MISSING ARGUMENTS-------------" +
                    "\nNeed the following arguments to run program:" +
                    "\n\t1. Path to an RDF file (URL or local path)" +
                    "\n\t2. Filename to which the output shall be written" +
                    "\n-------------------------------------------"
            );
        }

        inputPath = args[0];
        outputPath = args[1];
        System.out.println("Arguments used: " + inputPath + ", " + outputPath);

        checkFileExtension(inputPath);
        checkFileExtension(outputPath);
    }

    private static void checkFileExtension(String fileName) {
        if (!isValidFileExtension(fileName)) {
            throw new Error("\n-------------WRONG FILE EXTENSION-------------" +
                    "\nFile must have one of the following extensions: " +
                    "\n\t.rdf, .ttl, .n3, .nt" +
                    "\n----------------------------------------------"
            );
        }
        System.out.println("Valid file extension for " + fileName);
    }

    private static boolean isValidFileExtension(String fileName) {
        List<String> allowedFileExtensions = Arrays.asList(".rdf", ".ttl", ".n3", ".nt");
        return allowedFileExtensions.stream().anyMatch(fileName::endsWith);
    }
}