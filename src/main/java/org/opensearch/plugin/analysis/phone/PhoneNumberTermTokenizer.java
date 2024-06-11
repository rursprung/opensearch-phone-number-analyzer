/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.analysis.phone;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.opensearch.common.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This tokenizes a phone number into its individual parts, using {@link PhoneNumberUtil}.
 */
public final class PhoneNumberTermTokenizer extends Tokenizer {
    private final boolean addNgrams;
    private final Settings settings;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private Iterator<String> tokenIterator;

    /**
     * @param addNgrams defines whether ngrams for the phone number should be added. Set to true for indexing and false for search.
     * @param settings the settings for the analyzer.
     */
    public PhoneNumberTermTokenizer(final Settings settings, final boolean addNgrams) {
        super();
        this.addNgrams = addNgrams;
        this.settings = settings;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenIterator = null;
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        if (tokenIterator == null) {
            tokenIterator = getTokens().iterator();
        }
        if (tokenIterator.hasNext()) {
            termAtt.append(tokenIterator.next());
            return true;
        }
        return false;
    }

    private List<String> getTokens() throws IOException {
        final var tokens = new ArrayList<String>();

        var input = IOUtils.toString(this.input);

        tokens.add(input);

        // Rip off the "tel:" or "sip:" prefix
        if (input.indexOf("tel:") == 0 || input.indexOf("sip:") == 0) {
            tokens.add(input.substring(0, 4));
            input = input.substring(4);
        }

        final var startIndex = input.startsWith("+") ? 1 : 0;
        // Add the complete input but skip a leading +
        tokens.add(input.substring(startIndex));

        // Drop anything after @. Most likely there's nothing of interest
        final var posAt = input.indexOf('@');
        if (posAt != -1) {
            input = input.substring(0, posAt);

            // Add a token for the raw unmanipulated address. Note this could be a username (sip) instead of telephone
            // number so take it as is
            tokens.add(input.substring(startIndex));
        }

        // Let google's libphone try to parse it
        final var phoneUtil = PhoneNumberUtil.getInstance();
        Optional<String> countryCode = Optional.empty();
        try {
            // ZZ is the generic "I don't know the country code" region. Google's libphone library will try to infer it.
            final var region = this.settings.get("phone-region", "ZZ");
            final var numberProto = phoneUtil.parse(input, region);
            if (numberProto != null) {
                // Libphone likes it!
                countryCode = Optional.of(String.valueOf(numberProto.getCountryCode()));
                input = String.valueOf(numberProto.getNationalNumber());

                // Add Country code, extension, and the number as tokens
                tokens.add(countryCode.get());
                tokens.add(countryCode.get() + input);
                if (!StringUtils.isEmpty(numberProto.getExtension())) {
                    tokens.add(numberProto.getExtension());
                }

                tokens.add(input);
            }
        } catch (final NumberParseException | StringIndexOutOfBoundsException e) {
            // Libphone didn't like it, no biggie. We'll just ngram the number as it is.
        }

        // ngram the phone number, e.g. 19198243333 produces 9, 91, 919, etc
        if (this.addNgrams && NumberUtils.isCreatable(input)) {
            for (int count = 1; count <= input.length(); ++count) {
                final var token = input.substring(0, count);
                tokens.add(token);
                // If there was a country code, add more ngrams such that 19198243333 produces 19, 191, 1919, etc
                countryCode.ifPresent(s -> tokens.add(s + token));
            }
        }

        return tokens;
    }

}
