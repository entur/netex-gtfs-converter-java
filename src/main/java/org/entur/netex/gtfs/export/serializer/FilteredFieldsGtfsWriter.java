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

package org.entur.netex.gtfs.export.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.onebusaway.csv_entities.schema.EntitySchema;
import org.onebusaway.csv_entities.schema.FieldMapping;
import org.onebusaway.gtfs.serialization.GtfsWriter;

/**
 * GTFSWriter that ignores specific GTFS fields.
 */
public class FilteredFieldsGtfsWriter extends GtfsWriter {

  private final Map<Class<?>, Collection<String>> filteredFields;

  public FilteredFieldsGtfsWriter(
    Map<Class<?>, Collection<String>> filteredFields
  ) {
    this.filteredFields = filteredFields;
  }

  @Override
  public void excludeOptionalAndMissingFields(
    Class<?> entityType,
    Iterable<Object> entities
  ) {
    super.excludeOptionalAndMissingFields(entityType, entities);

    filteredFields.forEach((key, value) -> {
      EntitySchema entitySchema = this.getEntitySchemaFactory().getSchema(key);
      removeFields(entitySchema, value);
    });
  }

  private static void removeFields(
    EntitySchema entitySchema,
    Collection<String> excludedFields
  ) {
    Iterator<FieldMapping> iterator = entitySchema.getFields().iterator();
    while (iterator.hasNext()) {
      FieldMapping field = iterator.next();
      Collection<String> fieldNames = new ArrayList<>();
      field.getCSVFieldNames(fieldNames);
      String fieldName = fieldNames.stream().findFirst().orElse("");
      if (excludedFields.contains(fieldName)) {
        iterator.remove();
      }
    }
  }
}
