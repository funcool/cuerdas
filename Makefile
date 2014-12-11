all: doc
doc:
	asciidoctor -o index.html README.adoc
