# eclipse-arc

Eclipse-Plugin providing additional support for documenting software architecture

## Features

* Additional AsciiDoc templates for [Arc42](https://arc42.org/) snippets and inline [PlantUML](plantuml.com) diagrams (requires installed "Asciidoctor Editor" plugin)
* New File Wizard for Architecture Decision Records (asciidoc version of the template created by Michael Nygard)

### Ideas for the future

* "New Project" Wizard for a Maven based Arc42 compliant architecture documentation.

## Requirements / Dependencies

### Asciidoctor Editor

This plugin provides no own AsciiDoc editor but templates for the "Asciidoctor Editor" plugin. This has to be installed as well. 

Asciidoctor Editor Homepage: <https://github.com/de-jcup/eclipse-asciidoctor-editor> 


## Build

The build process is based on Maven and Eclipse Tycho. In order to build the project simply type:

	mvn clean install
	
## Related Links

* [AsciiDoc Home Page](http://asciidoc.org/)
* [Arc42](https://arc42.org/)
* [PlantUML](plantuml.com)
* [Documenting architecture decisions - Michael Nygard](http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions)
* [Asciidoctor Editor Homepage](https://github.com/de-jcup/eclipse-asciidoctor-editor) 
* [Maven](https://maven.apache.org/)
* [Eclipse Tycho](https://www.eclipse.org/tycho/) 