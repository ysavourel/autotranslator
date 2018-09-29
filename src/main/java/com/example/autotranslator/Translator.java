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

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.IPipelineDriver;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.openxml.OpenXMLFilter;
import net.sf.okapi.steps.common.FilterEventsToRawDocumentStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.leveraging.LeveragingStep;
import net.sf.okapi.steps.msbatchtranslation.MSBatchTranslationStep;

public class Translator {

	private final IFilterConfigurationMapper fcMapper;
	private final IPipelineDriver driver;
	
	public Translator() {
		// Create and initialize the filter configurations mapping
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations(OpenXMLFilter.class.getName());
		fcMapper.addConfigurations(HtmlFilter.class.getName());
		
		// Create and initialize the pipeline
		driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		
		// First step is always extraction
		driver.addStep(new RawDocumentToFilterEventsStep());
	}

	public String guessConfiguration(String extension) {
		Iterator<FilterConfiguration> configs = fcMapper.getAllConfigurations();
		while (configs.hasNext()) {
			FilterConfiguration fc = configs.next();
			if (fc.extensions != null) {
				if (fc.extensions.contains(extension)) {
					return fc.configId;
				}
			}
		}
		return null; // No configuration found
	}

	public void process(String inputPath,
		String srcLang,
		String trgLang,
		String engine)
	{
		// Guess the configuration to use based on the file extension
		String extension = Util.getExtension(inputPath);
		String configId = guessConfiguration(extension);
		if (configId == null) {
			throw new RuntimeException("Format not supported for " + inputPath);
		}
		// Ensure the path is absolute
		inputPath = new File(inputPath).getAbsolutePath();
		
		// Create the raw-document object (location, encoding, languages, etc.)
		RawDocument rd = new RawDocument(new File(inputPath).toURI(),
			"UTF-8", // Many filter auto-detect the encoding, this is just a fall-back default
			LocaleId.fromBCP47(srcLang),
			LocaleId.fromBCP47(trgLang),
			configId);

		// Construct the output path
		String outputPath = Util.getDirectoryName(inputPath)
			+ File.separator
			+ Util.getFilename(inputPath, false)
			+ ("."+engine.toUpperCase()+"-"+trgLang)
			+ extension;
		// Create the item to process and add it to the batch
		BatchItemContext item = new BatchItemContext(rd, new File(outputPath).toURI(), null);
		driver.addBatchItem(item);

		// Add the step to perform the machine translation
		switch( engine ) {
		case "gl":
			setupGoogleMtStep();
			break;
		case "ms":
			setupMicrosoftMtStep();
			break;
		default:
			throw new RuntimeException("Unexpected engine: "+engine);
		}
		
		// Add the step to write back the events
		driver.addStep(new FilterEventsToRawDocumentStep());

		// Execute the pipeline
		driver.processBatch();
	}

	private void setupMicrosoftMtStep() {
		// Get the key from the configuration file
		String azureKey = null;
		try( FileInputStream fis = new FileInputStream(new File("autotranslator.config")) ) {
			Properties prop = new Properties();
			prop.load(fis);
			azureKey = prop.getProperty("azureKey");
		}
		catch (Exception e) {
			throw new RuntimeException("Error loading the configuration file.", e);
		}
		
		// Construct the Leveraging step
		MSBatchTranslationStep msbt = new MSBatchTranslationStep();
		net.sf.okapi.steps.msbatchtranslation.Parameters mbtParams 
			= (net.sf.okapi.steps.msbatchtranslation.Parameters) msbt.getParameters();
		mbtParams.setAnnotate(false);
		mbtParams.setFillTarget(true);
		mbtParams.setMarkAsMT(true);
		mbtParams.setOnlyWhenWithoutCandidate(true);
		mbtParams.setMakeTmx(false);
		mbtParams.setCategory("generalnn"); // NMT
		mbtParams.setAzureKey(azureKey);
		driver.addStep(msbt);
	}

	private void setupGoogleMtStep() {
		// Get the key from the configuration file
		String googleKey = null;
		try( FileInputStream fis = new FileInputStream(new File("autotranslator.config")) ) {
			Properties prop = new Properties();
			prop.load(fis);
			googleKey = prop.getProperty("googleKey");
		}
		catch (Exception e) {
			throw new RuntimeException("Error loading the configuration file.", e);
		}

		// Construct the Leveraging step
		LeveragingStep step = new LeveragingStep();
		net.sf.okapi.steps.leveraging.Parameters params 
			= (net.sf.okapi.steps.leveraging.Parameters)step.getParameters();
		params.setMakeTMX(false);
		params.setFillTarget(true);
		params.setFillIfTargetIsEmpty(true);
		params.setFillIfTargetIsSameAsSource(false);
		params.setUseTargetPrefix(false);

		params.setResourceClassName("net.sf.okapi.connectors.google.GoogleMTv2Connector");
		net.sf.okapi.connectors.google.GoogleMTv2Parameters p = new net.sf.okapi.connectors.google.GoogleMTv2Parameters();
		p.setApiKey(googleKey);
		p.setUsePBMT(false);
		params.setResourceParameters(p.toString());
		
		driver.addStep(step);
	}

}
