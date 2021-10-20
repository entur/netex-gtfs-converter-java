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

package org.entur.netex.gtfs.export.exception;

/**
 * Base class for exceptions that occur during the NeTEx to GTFS org.entur.netex.gtfs.org.entur.netex.gtfs.export process.
 * These exceptions are caused by inconsistencies in the input NeTEx dataset.
 * The operation is in general not retryable.
 */
public class GtfsExportException extends RuntimeException {

    public GtfsExportException(Throwable cause) {
        super(cause);
    }

    public GtfsExportException(String message) {
        super(message);
    }

    public GtfsExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
