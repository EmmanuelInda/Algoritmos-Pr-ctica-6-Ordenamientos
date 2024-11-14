File Edit Options Buffers Tools Makefile Help                                   
JFREECHART=/usr/share/java/jfreechart-1.0.19.jar

run: program
        java -cp out:$(JFREECHART) Main

program:
        javac -cp $(JFREECHART) -d out src/*.java src/*/*.java
