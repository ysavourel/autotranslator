# autotranslator
Simple MT-based translator of documents using the [Okapi Framework](https://okapiframework.org).

## To build
To build the tool, from the project root directory:

    mvn package

## Requirements to run
The tool uses [Microsoft Translator](https://www.microsoft.com/en-us/translator/) MT engine by default. You must have a key to access the engine. That key must be stored in a file named `autotranslator.config` in the working directory. The content must be:

    azureKey=<your-azure-key>

## To run
To run the tool, from the project root directory:

    java -jar target/autotranslator.jar en fr test1.docx

The parameters are:
- The code of the language of the document (source language)
- The code of the language to translate into (target language)
- the document to translate

The output is generated in the same directory as the input document. For example the French output for `test1.docx` is `test1.out-fr.docx`.

the formats supported by default are Microsoft Office files (.docx, .pptx, .xlsx) and HTML. You can easily add any of the other file formats supported by the [Okapi filters](https://okapiframework.org/wiki/index.php?title=Filters).
