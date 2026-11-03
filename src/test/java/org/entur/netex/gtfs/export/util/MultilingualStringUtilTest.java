/*
 *
 *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *  *
 *
 */

package org.entur.netex.gtfs.export.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import org.entur.netex2.model.MultilingualString;
import org.entur.netex2.model.ObjectFactory;
import org.entur.netex2.model.TextType;
import org.junit.jupiter.api.Test;

class MultilingualStringUtilTest {

  private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();

  @Test
  void testNullMultilingualString() {
    assertNull(MultilingualStringUtil.getValue(null));
    assertTrue(MultilingualStringUtil.isBlank(null));
  }

  @Test
  void testEmptyContent() {
    MultilingualString multilingualString = new MultilingualString();
    assertNull(MultilingualStringUtil.getValue(multilingualString));
    assertTrue(MultilingualStringUtil.isBlank(multilingualString));
  }

  @Test
  void testPlainText() {
    MultilingualString multilingualString = multilingualString("Oslo S");
    assertEquals("Oslo S", MultilingualStringUtil.getValue(multilingualString));
    assertFalse(MultilingualStringUtil.isBlank(multilingualString));
  }

  @Test
  void testPlainTextFragmentsAreConcatenated() {
    MultilingualString multilingualString = multilingualString("Oslo", " S");
    assertEquals("Oslo S", MultilingualStringUtil.getValue(multilingualString));
  }

  @Test
  void testWhitespaceOnlyText() {
    MultilingualString multilingualString = multilingualString("  \n  ");
    assertNull(MultilingualStringUtil.getValue(multilingualString));
    assertTrue(MultilingualStringUtil.isBlank(multilingualString));
  }

  @Test
  void testTextElement() {
    MultilingualString multilingualString = multilingualString(
      "\n  ",
      textElement("Oslo S", "no"),
      "\n"
    );
    assertEquals("Oslo S", MultilingualStringUtil.getValue(multilingualString));
    assertFalse(MultilingualStringUtil.isBlank(multilingualString));
  }

  @Test
  void testFirstTextElementIsUsedWhenSeveralLanguages() {
    MultilingualString multilingualString = multilingualString(
      textElement("Oslo sentralstasjon", "no"),
      textElement("Oslo Central Station", "en")
    );
    assertEquals(
      "Oslo sentralstasjon",
      MultilingualStringUtil.getValue(multilingualString)
    );
  }

  @Test
  void testBlankTextElementsAreSkipped() {
    MultilingualString multilingualString = multilingualString(
      textElement("", "no"),
      textElement(null, "sv"),
      textElement("Oslo S", "en")
    );
    assertEquals("Oslo S", MultilingualStringUtil.getValue(multilingualString));
  }

  @Test
  void testOnlyBlankTextElements() {
    MultilingualString multilingualString = multilingualString(
      "\n  ",
      textElement("  ", "no"),
      "\n"
    );
    assertNull(MultilingualStringUtil.getValue(multilingualString));
    assertTrue(MultilingualStringUtil.isBlank(multilingualString));
  }

  @Test
  void testPlainTextTakesPrecedenceOverTextElement() {
    MultilingualString multilingualString = multilingualString(
      "Oslo S",
      textElement("Oslo Central Station", "en")
    );
    assertEquals("Oslo S", MultilingualStringUtil.getValue(multilingualString));
  }

  private static MultilingualString multilingualString(
    Serializable... content
  ) {
    return new MultilingualString().withContent(content);
  }

  private static Serializable textElement(String value, String lang) {
    return NETEX_FACTORY.createMultilingualStringText(
      new TextType().withValue(value).withLang(lang)
    );
  }
}
