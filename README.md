# autotranslator
Simple MT-based translator of documents using the [Okapi Framework](https://okapiframework.org).

## To build
To build the tool, from the project root directory:

    mvn package

## Requirements to run
The tool uses [Microsoft Translator](https://www.microsoft.com/en-us/translator/) MT engine by default. You must have a key to access the engine. That key must be stored in a file named `autotranslator.config` in the working directory. For Microsoft Translator the file must include:

    azureKey=<your-azure-key>
	
For Google Translate, the file must include:

    googleKey=<your-google-key>

## To run
To run the tool, from the project root directory:

    java -jar target/autotranslator.jar en fr test1.docx
    java -jar target/autotranslator.jar en fr test1.docx ms
    java -jar target/autotranslator.jar en fr test1.docx gl

The parameters are:
- The code of the language of the document (source language)
- The code of the language to translate into (target language)
- The document to translate
- Optionally, `ms` or `gl` to use either the Microsoft Translator engine or the Google Translate engine. By default `ms` is used.

The output is generated in the same directory as the input document. For example the French output for `test1.docx` when using Microsoft Translator is `test1.MS-fr.docx`. When using the Google Translate it is `test1.GL-fr.docx`.

The formats supported by default are Microsoft Office files (.docx, .pptx, .xlsx) and HTML. You can easily add any of the other file formats supported by the [Okapi filters](https://okapiframework.org/wiki/index.php?title=Filters).
