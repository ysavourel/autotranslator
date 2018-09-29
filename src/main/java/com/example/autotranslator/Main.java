/*===========================================================================
  Copyright (C) 2018 by Yves Savourel
-----------------------------------------------------------------------------
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
===========================================================================*/

package com.example.autotranslator;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("=== Auto-Translator =====================================================");
		
		String inputPath = null;
		String srcLang = null;
		String trgLang = null;
		String engine = "ms";
		
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
			case 3: // MT engine
				engine = arg;
				break;
			}
		}

		if( srcLang == null || trgLang == null || inputPath == null ) {
			System.out.println("Usage: java -jar autotranslator.jar <sourceLanguageCode> <targetLanguageCode> <inputFile> [ms|gl]");
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
		System.out.println("Engine: "+engine);
		
		Translator translator = new Translator();
		translator.process(inputPath, srcLang, trgLang, engine);
	}

}
