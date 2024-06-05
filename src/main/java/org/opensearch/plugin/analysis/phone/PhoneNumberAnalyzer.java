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

/**
 * Analyzer for phone numbers, using {@link PhoneNumberTermTokenizer}.
 */
public class PhoneNumberAnalyzer extends Analyzer {
    private final boolean addNgrams;

    /**
     * @param addNgrams defines whether ngrams for the phone number should be added. Set to true for indexing and false for search.
     */
    public PhoneNumberAnalyzer(final boolean addNgrams) {
        this.addNgrams = addNgrams;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer tokenizer = new PhoneNumberTermTokenizer(this.addNgrams);
        // TODO: UniqueTokenFilter
        return new Analyzer.TokenStreamComponents(tokenizer, tokenizer);
    }
}
