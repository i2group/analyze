/*
 * MIT License
 *
 * Copyright (c) 2023, N. Harris Computer Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.security.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2group.disco.security.spi.DimensionValue;
import com.i2group.disco.security.spi.IDimensionValue;
import com.i2group.disco.security.spi.ISecurityDimensionValuesProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of {@link ISecurityDimensionValuesProvider} that adds dimension values to the
 * Security Compartment dimension from information in the sample JSON file.
 */
public final class SecurityCompartmentDimensionValuesProvider
    implements ISecurityDimensionValuesProvider
{
  private static final String SECURITY_COMPARTMENT_DIMENSION_ID = "SD-SC";
  private static final String SECURITY_COMPARTMENT_JSON_FILE_NAME =
      "security-compartment-dimension-values.jsonc";

  private List<IDimensionValue> mDimensionValues;

  @Override
  public List<IDimensionValue> getDimensionValues(final String dimensionId)
  {
    if (dimensionId.equals(SECURITY_COMPARTMENT_DIMENSION_ID))
    {
      return mDimensionValues;
    }
    throw new RuntimeException(
        String.format(
            "Unexpected dimension identifier for this provider. Expected '%s', but received '%s'.",
            SECURITY_COMPARTMENT_DIMENSION_ID, dimensionId));
  }

  @Override
  public void onStartup()
  {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    final List<JsonDimensionValue> jsonDimensionValues;

    // This example code sources dimension values for the Security Compartment dimension from a
    // JSON file. It uses a Jackson ObjectMapper to instantiate a list of JsonDimensionValue
    // objects directly from the file, and then converts them to SPI DimensionValue objects.
    try (InputStream jsonStream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(SECURITY_COMPARTMENT_JSON_FILE_NAME))
    {
      jsonDimensionValues = Arrays.asList(mapper.readValue(jsonStream, JsonDimensionValue[].class));
    }
    catch (IOException ex)
    {
      throw new RuntimeException(
          "Failed to load the JSON file named " + SECURITY_COMPARTMENT_JSON_FILE_NAME, ex);
    }

    mDimensionValues =
        jsonDimensionValues.stream().map(this::createDimensionValue).collect(Collectors.toList());
  }

  private IDimensionValue createDimensionValue(final JsonDimensionValue jsonDimensionValue)
  {
    return DimensionValue.create(
        jsonDimensionValue.getIdentifier(), jsonDimensionValue.getLabel(), jsonDimensionValue.getExplanation());
  }
}
