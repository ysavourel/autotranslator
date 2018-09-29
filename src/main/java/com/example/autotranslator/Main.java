package com.example.autotranslator;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("Auto-Translator");
		
		String inputPath = null;
		String srcLang = null;
		String trgLang = null;
		
		for (int i=0; i<args.length; i++) {
			String arg = args[i];
			switch( i ) {
			case 0: // Source
				srcLang = arg;
				break;
			case 1: // Target
				trgLang = arg;
				break;
			case 2: // Input
				inputPath = arg;
				break;
			}
		}

		if( srcLang == null || trgLang == null || inputPath == null ) {
			System.out.println("Usage: java -jar autotranslator.jar <sourceLanguageCode> <targetLanguageCode> <inputFile>");
			System.out.println("Example: en fr test1.docx");
			if( srcLang == null ) {
				System.out.println("The <sourceLanguageCode> parameter is missing.");
			}
			if( trgLang == null ) {
				System.out.println("The <targetLanguageCode> parameter is missing.");
			}
			if( inputPath == null ) {
				System.out.println("The <inputFile> parameter is missing.");
			}
			System.exit(1);
		}

		System.out.println("Source: "+srcLang);
		System.out.println("Target: "+trgLang);
		System.out.println(" Input: "+inputPath);
		
		Translator translator = new Translator();
		translator.process(inputPath, srcLang, trgLang);
	}

}
