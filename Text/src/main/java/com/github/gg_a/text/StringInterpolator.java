/*
 * Copyright 2019 GG-A, <2018158885@qq.com, https://github.com/GG-A/Text>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gg_a.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.github.gg_a.tuple.Tuple;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;

/**
 * String Interpolator. <br>
 * 字符串插值器
 * @since 0.0.1
 */
public class StringInterpolator {

    private final Map<String, Object> valueMap;
    private final StringSubstitutor substitutor;
    public final static String DEFAULT_VALUE_DELIMITER = ":";
    public static final char DEFAULT_ESCAPE = '$';


    public StringInterpolator() {
        this(new HashMap<>());
    }

    public StringInterpolator(final Tuple... tuples) {
        this(DEFAULT_ESCAPE, new HashMap<>());
        tuplesPutToMap(tuples);
    }

    public StringInterpolator(final Map<String, Object> valueMap) {
        this(DEFAULT_ESCAPE, valueMap);
    }

    public StringInterpolator(final char escape, final Tuple... tuples) {
        this(escape, new HashMap<>());
        tuplesPutToMap(tuples);
    }

    public StringInterpolator(final char escape, final Map<String, Object> valueMap) {
        this(escape, DEFAULT_VALUE_DELIMITER, valueMap);
    }

    public StringInterpolator(final char escape, final String valueDelimiter, final Tuple... tuples) {
        this(escape, valueDelimiter, new HashMap<>());
        tuplesPutToMap(tuples);
    }

    public StringInterpolator(final char escape, final String valueDelimiter, final Map<String, Object> valueMap) {
        this(StringSubstitutor.DEFAULT_VAR_START, StringSubstitutor.DEFAULT_VAR_END,
                escape, valueDelimiter, valueMap);
    }

    public StringInterpolator(final String prefix, final String suffix, final char escape,
                              final String valueDelimiter, final Tuple... tuples) {
        this(prefix, suffix, escape, valueDelimiter, new HashMap<>());
        tuplesPutToMap(tuples);
    }

    public StringInterpolator(final String prefix, final String suffix, final char escape,
                              final String valueDelimiter, final Map<String, Object> valueMap) {
        this.valueMap = valueMap == null ? new HashMap<>() : valueMap;
        substitutor = new StringSubstitutor(StringLookupFactory.INSTANCE.mapStringLookup(this.valueMap),
                prefix, suffix, escape, valueDelimiter);
        setSubstitutionInValues(false);
        setSubstitutionInVariables(false);
        setUndefinedVariableException(false);
    }

    public static StringInterpolator of(final Tuple... tuples) {
        return new StringInterpolator(tuples);
    }

    public static StringInterpolator of(final Map<String, Object> map) {
        return new StringInterpolator(map);
    }

    public StringInterpolator add(Tuple... tuples) {
        tuplesPutToMap(tuples);
        return this;
    }

    public StringInterpolator set(Tuple... tuples) {
        valueMap.clear();
        tuplesPutToMap(tuples);
        return this;
    }

    public StringInterpolator add(Map<String, Object> valueMap) {
        if (valueMap != null) {
            this.valueMap.putAll(valueMap);
            substitutor.setVariableResolver(StringLookupFactory.INSTANCE.mapStringLookup(this.valueMap));
        }

        return this;
    }

    public StringInterpolator set(Map<String, Object> valueMap) {
        this.valueMap.clear();
        if (valueMap != null)  this.valueMap.putAll(valueMap);
        substitutor.setVariableResolver(StringLookupFactory.INSTANCE.mapStringLookup(this.valueMap));
        return this;
    }

    public StringInterpolator del(String... keys) {
        if (keys != null) {
            Arrays.stream(keys).forEach(valueMap::remove);
            substitutor.setVariableResolver(StringLookupFactory.INSTANCE.mapStringLookup(valueMap));
        }
        return this;
    }

    private void tuplesPutToMap(Tuple... tuples) {
        if (tuples != null) {
            Arrays.stream(tuples).filter(e -> e != null && e.arity() != 0)
                    .forEach(t -> valueMap.putAll(t.toMap()));
            substitutor.setVariableResolver(StringLookupFactory.INSTANCE.mapStringLookup(valueMap));
        }
    }

    public String parse(String source) {
        return substitutor.replace(source);
    }

    public String parse(StringBuffer source) {
        return substitutor.replace(source);
    }

    public StringInterpolator setSubstitutionInValues(final boolean enableSubstitutionInValues) {
        substitutor.setDisableSubstitutionInValues(!enableSubstitutionInValues);
        return this;
    }

    public StringInterpolator setSubstitutionInVariables(final boolean enableSubstitutionInVariables) {
        substitutor.setEnableSubstitutionInVariables(enableSubstitutionInVariables);
        return this;
    }

    public StringInterpolator setUndefinedVariableException(final boolean failOnUndefinedVariable) {
        substitutor.setEnableUndefinedVariableException(failOnUndefinedVariable);
        return this;
    }

    public boolean isEnableSubstitutionInValues() {
        return !substitutor.isDisableSubstitutionInValues();
    }

    public boolean isEnableSubstitutionInVariables() {
        return substitutor.isEnableSubstitutionInVariables();
    }

    public boolean isEnableUndefinedVariableException() {
        return substitutor.isEnableUndefinedVariableException();
    }

    public StringInterpolator setEscapeChar(final char escapeCharacter) {
        substitutor.setEscapeChar(escapeCharacter);
        return this;
    }

    public StringInterpolator setPreserveEscapes(final boolean preserveEscapes) {
        substitutor.setPreserveEscapes(preserveEscapes);
        return this;
    }

    public StringInterpolator setValueDelimiter(final char valueDelimiter) {
        substitutor.setValueDelimiter(valueDelimiter);
        return this;
    }

    public StringInterpolator setValueDelimiter(final String valueDelimiter) {
        substitutor.setValueDelimiter(valueDelimiter);
        return this;
    }

    public StringInterpolator setVariablePrefix(final char prefix) {
        substitutor.setVariablePrefix(prefix);
        return this;
    }

    public StringInterpolator setVariablePrefix(final String prefix) {
        substitutor.setVariablePrefix(prefix);
        return this;
    }

    public StringInterpolator setVariableSuffix(final char suffix) {
        substitutor.setVariableSuffix(suffix);
        return this;
    }

    public StringInterpolator setVariableSuffix(final String suffix) {
        substitutor.setVariableSuffix(suffix);
        return this;
    }

    public char getEscapeChar() {
        return substitutor.getEscapeChar();
    }

    public Map<String, Object> getValueMap() {
        return Collections.unmodifiableMap(valueMap);
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

}
