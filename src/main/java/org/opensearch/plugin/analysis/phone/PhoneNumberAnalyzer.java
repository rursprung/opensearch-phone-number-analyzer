/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.phone;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.opensearch.common.settings.Settings;

/**
 * Analyzer for phone numbers, using {@link PhoneNumberTermTokenizer}.
 */
public class PhoneNumberAnalyzer extends Analyzer {
    private final boolean addNgrams;
    private final Settings settings;

    /**
     * @param addNgrams defines whether ngrams for the phone number should be added. Set to true for indexing and false for search.
     * @param settings the settings for the analyzer.
     */
    public PhoneNumberAnalyzer(final Settings settings, final boolean addNgrams) {
        this.addNgrams = addNgrams;
        this.settings = settings;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer tokenizer = new PhoneNumberTermTokenizer(this.settings, this.addNgrams);
        return new Analyzer.TokenStreamComponents(tokenizer, tokenizer);
    }
}
