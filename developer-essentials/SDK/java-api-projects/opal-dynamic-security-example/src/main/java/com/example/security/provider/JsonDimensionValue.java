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

/** A dimension value from the sample JSON file, which uses different terminology from i2 Analyze. */
public class JsonDimensionValue
{
  private String mIdentifier;
  private String mLabel;
  private String mExplanation;

  /**
   * Constructs a new {@link JsonDimensionValue}.
   */
  public JsonDimensionValue()
  {
  }

  /**
   * Sets the identifier of the dimension value.
   * 
   * @param identifier The dimension value identifier to set.
   */
  public void setIdentifier(final String identifier)
  {
    mIdentifier = identifier;
  }

  /**
   * Sets the label associated with the dimension value, which will become the display name.
   * 
   * @param label The label to set.
   */
  public void setLabel(final String label)
  {
    mLabel = label;
  }

  /**
   * Sets the explanation associated with the dimension value, which will become the description.
   * 
   * @param explanation The explanation to set.
   */
  public void setExplanation(final String explanation)
  {
    mExplanation = explanation;
  }

  /**
   * Gets the dimension value identifier.
   * 
   * @return See above.
   */
  public String getIdentifier()
  {
    return mIdentifier;
  }

  /**
   * Gets the label associated with the dimension value.
   * 
   * @return See above.
   */
  public String getLabel()
  {
    return mLabel;
  }

  /**
   * Gets the explanation associated with the dimension value.
   * 
   * @return See above.
   */
  public String getExplanation()
  {
    return mExplanation;
  }
}
