/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.phone;

import org.apache.lucene.analysis.Analyzer;
import org.opensearch.index.analysis.AnalyzerProvider;
import org.opensearch.index.analysis.TokenizerFactory;
import org.opensearch.indices.analysis.AnalysisModule;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.Plugin;

import java.util.Map;


/**
 * This plugin provides an analyzer and tokenizer for fields which contain phone numbers, supporting a variety of formats
 * (with/without international calling code, different country formats, etc.).
 */
public class PhoneNumberAnalyzerPlugin extends Plugin implements AnalysisPlugin {

    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return Map.of(
                "phone", (indexSettings, environment, name, settings) -> new PhoneNumberAnalyzerProvider(indexSettings, "phone", settings, true),
                "phone-search", (indexSettings, environment, name, settings) -> new PhoneNumberAnalyzerProvider(indexSettings, "phone-search", settings, false)
        );
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return Map.of(
                "phone", (indexSettings, environment, name, settings) -> new PhoneNumberTermTokenizerFactory(indexSettings, settings, "phone", true),
                "phone-search", (indexSettings, environment, name, settings) -> new PhoneNumberTermTokenizerFactory(indexSettings, settings, "phone-search", false)
        );
    }
}
