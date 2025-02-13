JAVA_CP = "lib/*:."
SIMPSONS_FILE = "src/main/java/no/uio/ifi/in4060/oblig/simpsons.ttl"

java = java -cp $(JAVA_CP)
javac = javac -cp $(JAVA_CP)

%.class: %.java
        @$(javac) $<

output.ttl: Simpsons.class
        @$(java) $(basename $(<F)) $(SIMPSONS_FILE) $@

run_test: Test.class
        @$(java) $(basename $(<F)) $(SIMPSONS_FILE) $@
