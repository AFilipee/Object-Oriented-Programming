JAVADIR=/home/andre-filipe/Documents/PO/Projeto/project
JARNAME=mmt-app

all:
	(cd src; javac -cp $(JAVADIR)/po-uuilib.jar:$(JAVADIR)/mmt-core/mmt-core.jar -encoding UTF-8 `find . -name \*.java`)
	(cd src; jar cvf ../$(JARNAME).jar `find . -name \*.class -o -name \*.java` )

clean:
	$(RM) $(JARNAME).jar `find . -name \*.class`