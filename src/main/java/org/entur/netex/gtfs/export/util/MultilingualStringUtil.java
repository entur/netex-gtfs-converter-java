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

import java.io.Serializable;
import java.util.List;
import org.rutebanken.netex.model.MultilingualString;

/**
 * Utility class for extracting string values from MultilingualString.
 * In NeTEx 2.0 (netex-java-model 3.x), MultilingualString uses a mixed content model
 * where getContent() returns List&lt;Serializable&gt; instead of getValue() returning String.
 */
public final class MultilingualStringUtil {

  private MultilingualStringUtil() {}

  /**
   * Extract the string value from a MultilingualString.
   *
   * @param multilingualString the multilingual string
   * @return the string value, or null if the multilingual string is null or has no content
   */
  public static String getValue(MultilingualString multilingualString) {
    if (multilingualString == null) {
      return null;
    }
    List<Serializable> content = multilingualString.getContent();
    if (content == null || content.isEmpty()) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Serializable item : content) {
      if (item instanceof String s) {
        sb.append(s);
      }
    }
    String result = sb.toString();
    return result.isEmpty() ? null : result;
  }

  /**
   * Return true if the MultilingualString is null or has a blank value.
   */
  public static boolean isBlank(MultilingualString multilingualString) {
    String value = getValue(multilingualString);
    return value == null || value.isBlank();
  }
}
