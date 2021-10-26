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

package org.entur.netex.gtfs.export.repository;

import org.entur.netex.gtfs.export.model.GtfsService;
import org.entur.netex.gtfs.export.model.ServiceCalendarPeriod;
import org.entur.netex.gtfs.export.producer.DefaultGtfsServiceRepository;
import org.entur.netex.gtfs.export.producer.GtfsServiceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.OperatingPeriodRefStructure;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtfsServiceRepositoryTest {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();
    private static final String TEST_CODESPACE = "ENT";
    private static final String TEST_DAY_TYPE_ID = "ENT:DayType:1";
    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2021, 10, 15, 1, 1, 1);

    private static final String TEST_OPERATING_PERIOD_1_ID = "TEST_OPERATING_PERIOD_1_ID";
    private static final LocalDateTime TEST_OPERATING_PERIOD_1_START = LocalDateTime.of(2021, 10, 15, 1, 1, 1);
    private static final LocalDateTime TEST_OPERATING_PERIOD_1_END = LocalDateTime.of(2021, 11, 15, 1, 1, 1);

    private static final String TEST_OPERATING_PERIOD_2_ID = "TEST_OPERATING_PERIOD_2_ID";
    private static final LocalDateTime TEST_OPERATING_PERIOD_2_START = LocalDateTime.of(2021, 9, 15, 1, 1, 1);
    private static final LocalDateTime TEST_OPERATING_PERIOD_2_END = LocalDateTime.of(2021, 11, 5, 1, 1, 1);

    private static final String TEST_OPERATING_PERIOD_3_ID = "TEST_OPERATING_PERIOD_2_ID";
    private static final LocalDateTime TEST_OPERATING_PERIOD_3_START = LocalDateTime.of(2021, 11, 20, 1, 1, 1);
    private static final LocalDateTime TEST_OPERATING_PERIOD_3_END = LocalDateTime.of(2021, 12, 20, 1, 1, 1);

    @Test
    void testSingleDayTypeWithSingleAvailableDate() {
        DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);
        DayTypeAssignment dayTypeAssignment = createTestDayTypeAssignment(TEST_DAY_TYPE_ID, TEST_DATE, null);

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)).thenReturn(Set.of(dayTypeAssignment));

        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(TEST_CODESPACE, netexDatasetRepository);
        GtfsService service = gtfsServiceRepository.getServiceForDayTypes(Set.of(dayType));
        Assertions.assertNotNull(service);
        Assertions.assertEquals(TEST_DAY_TYPE_ID, service.getId());
        Assertions.assertTrue(service.getExcludedDates().isEmpty());
        Assertions.assertEquals(1, service.getIncludedDates().size());
        LocalDateTime actual = service.getIncludedDates().stream().findFirst().orElseThrow();
        Assertions.assertEquals(TEST_DATE, actual);
    }

    @Test
    void testSingleDayTypeWithSingleNonAvailableDate() {
        DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);
        DayTypeAssignment dayTypeAssignment = createTestDayTypeAssignment(TEST_DAY_TYPE_ID, TEST_DATE, false);

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)).thenReturn(Set.of(dayTypeAssignment));

        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(TEST_CODESPACE, netexDatasetRepository);
        GtfsService service = gtfsServiceRepository.getServiceForDayTypes(Set.of(dayType));
        Assertions.assertNotNull(service);
        Assertions.assertEquals(TEST_DAY_TYPE_ID, service.getId());
        Assertions.assertTrue(service.getIncludedDates().isEmpty());
        Assertions.assertTrue(service.getExcludedDates().isEmpty(), "Non available date should not be added if there is no period");
    }

    @Test
    void testSingleDayTypeWithSinglePeriod() {
        DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);
        OperatingPeriod operatingPeriod = createTestOperatingPeriod(TEST_OPERATING_PERIOD_1_ID, TEST_OPERATING_PERIOD_1_START, TEST_OPERATING_PERIOD_1_END);
        DayTypeAssignment dayTypeAssignment = createTestDayTypeAssignmentWithPeriod(TEST_DAY_TYPE_ID, TEST_OPERATING_PERIOD_1_ID);

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);
        when(netexDatasetRepository.getDayTypeByDayTypeAssignment(dayTypeAssignment)).thenReturn(dayType);
        when(netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)).thenReturn(Set.of(dayTypeAssignment));
        when(netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignment)).thenReturn(operatingPeriod);


        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(TEST_CODESPACE, netexDatasetRepository);
        GtfsService service = gtfsServiceRepository.getServiceForDayTypes(Set.of(dayType));
        Assertions.assertNotNull(service);
        Assertions.assertEquals(TEST_DAY_TYPE_ID, service.getId());
        Assertions.assertTrue(service.getIncludedDates().isEmpty());
        Assertions.assertTrue(service.getExcludedDates().isEmpty());
        ServiceCalendarPeriod serviceCalendarPeriod = service.getServiceCalendarPeriod();
        Assertions.assertNotNull(serviceCalendarPeriod);
        Assertions.assertEquals(7, serviceCalendarPeriod.getDaysOfWeek().size());
    }

    @Test
    void testSingleDayTypeWithTwoOverlappingPeriods() {
        DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);
        OperatingPeriod operatingPeriod1 = createTestOperatingPeriod(TEST_OPERATING_PERIOD_1_ID, TEST_OPERATING_PERIOD_1_START, TEST_OPERATING_PERIOD_1_END);
        DayTypeAssignment dayTypeAssignment1 = createTestDayTypeAssignmentWithPeriod(TEST_DAY_TYPE_ID, TEST_OPERATING_PERIOD_1_ID);

        OperatingPeriod operatingPeriod2 = createTestOperatingPeriod(TEST_OPERATING_PERIOD_2_ID, TEST_OPERATING_PERIOD_2_START, TEST_OPERATING_PERIOD_2_END);
        DayTypeAssignment dayTypeAssignment2 = createTestDayTypeAssignmentWithPeriod(TEST_DAY_TYPE_ID, TEST_OPERATING_PERIOD_2_ID);

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);

        when(netexDatasetRepository.getDayTypeByDayTypeAssignment(dayTypeAssignment1)).thenReturn(dayType);
        when(netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignment1)).thenReturn(operatingPeriod1);

        when(netexDatasetRepository.getDayTypeByDayTypeAssignment(dayTypeAssignment2)).thenReturn(dayType);
        when(netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignment2)).thenReturn(operatingPeriod2);

        when(netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)).thenReturn(Set.of(dayTypeAssignment1, dayTypeAssignment2));

        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(TEST_CODESPACE, netexDatasetRepository);
        GtfsService service = gtfsServiceRepository.getServiceForDayTypes(Set.of(dayType));
        Assertions.assertNotNull(service);
        Assertions.assertEquals(TEST_DAY_TYPE_ID, service.getId());
        Assertions.assertFalse(service.getIncludedDates().isEmpty(), "When a day type contains multiple periods, they are replaced by individual dates");

        Set<LocalDate> allIncludedDates = service.getIncludedDates().stream().map(LocalDateTime::toLocalDate).collect(Collectors.toSet());
        Set<LocalDate> allDatesInCombinedPeriods = TEST_OPERATING_PERIOD_2_START.toLocalDate().datesUntil(TEST_OPERATING_PERIOD_1_END.toLocalDate().plusDays(1)).collect(Collectors.toSet());
        Assertions.assertEquals(allDatesInCombinedPeriods.size(), allIncludedDates.size() );
        Assertions.assertTrue(allIncludedDates.containsAll(allDatesInCombinedPeriods));


        Assertions.assertTrue(service.getExcludedDates().isEmpty());
        ServiceCalendarPeriod serviceCalendarPeriod = service.getServiceCalendarPeriod();
        Assertions.assertNull(serviceCalendarPeriod, "When a day type contains multiple periods, they are replaced by individual dates");
    }

    @Test
    void testSingleDayTypeWithTwoNonOverlappingPeriods() {
        DayType dayType = createTestDayType(TEST_DAY_TYPE_ID);
        OperatingPeriod operatingPeriod1 = createTestOperatingPeriod(TEST_OPERATING_PERIOD_1_ID, TEST_OPERATING_PERIOD_1_START, TEST_OPERATING_PERIOD_1_END);
        DayTypeAssignment dayTypeAssignment1 = createTestDayTypeAssignmentWithPeriod(TEST_DAY_TYPE_ID, TEST_OPERATING_PERIOD_1_ID);

        OperatingPeriod operatingPeriod2 = createTestOperatingPeriod(TEST_OPERATING_PERIOD_3_ID, TEST_OPERATING_PERIOD_3_START, TEST_OPERATING_PERIOD_3_END);
        DayTypeAssignment dayTypeAssignment2 = createTestDayTypeAssignmentWithPeriod(TEST_DAY_TYPE_ID, TEST_OPERATING_PERIOD_3_ID);

        NetexDatasetRepository netexDatasetRepository = mock(NetexDatasetRepository.class);

        when(netexDatasetRepository.getDayTypeByDayTypeAssignment(dayTypeAssignment1)).thenReturn(dayType);
        when(netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignment1)).thenReturn(operatingPeriod1);

        when(netexDatasetRepository.getDayTypeByDayTypeAssignment(dayTypeAssignment2)).thenReturn(dayType);
        when(netexDatasetRepository.getOperatingPeriodByDayTypeAssignment(dayTypeAssignment2)).thenReturn(operatingPeriod2);

        when(netexDatasetRepository.getDayTypeAssignmentsByDayType(dayType)).thenReturn(Set.of(dayTypeAssignment1, dayTypeAssignment2));

        GtfsServiceRepository gtfsServiceRepository = new DefaultGtfsServiceRepository(TEST_CODESPACE, netexDatasetRepository);
        GtfsService service = gtfsServiceRepository.getServiceForDayTypes(Set.of(dayType));
        Assertions.assertNotNull(service);
        Assertions.assertEquals(TEST_DAY_TYPE_ID, service.getId());
        Assertions.assertFalse(service.getIncludedDates().isEmpty(), "When a day type contains multiple periods, they are replaced by individual dates");

        Set<LocalDate> allIncludedDates = service.getIncludedDates().stream().map(LocalDateTime::toLocalDate).collect(Collectors.toSet());
        Set<LocalDate> allDatesInFirstPeriod = TEST_OPERATING_PERIOD_1_START.toLocalDate().datesUntil(TEST_OPERATING_PERIOD_1_END.toLocalDate().plusDays(1)).collect(Collectors.toSet());
        Set<LocalDate> allDatesInSecondPeriod = TEST_OPERATING_PERIOD_3_START.toLocalDate().datesUntil(TEST_OPERATING_PERIOD_3_END.toLocalDate().plusDays(1)).collect(Collectors.toSet());
        Set<LocalDate> allDatesInCombinedPeriods = new HashSet<>(allDatesInFirstPeriod);
        allDatesInCombinedPeriods.addAll(allDatesInSecondPeriod);
        Assertions.assertEquals(allDatesInCombinedPeriods.size(), allIncludedDates.size() );
        Assertions.assertTrue(allIncludedDates.containsAll(allDatesInCombinedPeriods));


        Assertions.assertTrue(service.getExcludedDates().isEmpty());
        ServiceCalendarPeriod serviceCalendarPeriod = service.getServiceCalendarPeriod();
        Assertions.assertNull(serviceCalendarPeriod, "When a day type contains multiple periods, they are replaced by individual dates");
    }




    private DayType createTestDayType(String dayTypeId) {
        DayType dayType = NETEX_FACTORY.createDayType();
        dayType.setId(dayTypeId);
        return dayType;
    }

    private DayTypeAssignment createTestDayTypeAssignment(String dayTypeId, LocalDateTime date, Boolean isAvailable) {
        DayTypeAssignment dayTypeAssignment = NETEX_FACTORY.createDayTypeAssignment();
        DayTypeRefStructure daytypeRefStructure = NETEX_FACTORY.createDayTypeRefStructure();
        daytypeRefStructure.setRef(TEST_DAY_TYPE_ID);
        JAXBElement<DayTypeRefStructure> dayTypeRef = NETEX_FACTORY.createDayTypeRef(daytypeRefStructure);
        dayTypeAssignment.setDayTypeRef(dayTypeRef);
        dayTypeAssignment.setDate(date);
        dayTypeAssignment.setIsAvailable(isAvailable);
        return dayTypeAssignment;
    }

    private DayTypeAssignment createTestDayTypeAssignmentWithPeriod(String testDayTypeId, String operatingPeriodId) {
        DayTypeAssignment dayTypeAssignment = NETEX_FACTORY.createDayTypeAssignment();
        DayTypeRefStructure daytypeRefStructure = NETEX_FACTORY.createDayTypeRefStructure();
        daytypeRefStructure.setRef(TEST_DAY_TYPE_ID);
        JAXBElement<DayTypeRefStructure> dayTypeRef = NETEX_FACTORY.createDayTypeRef(daytypeRefStructure);
        dayTypeAssignment.setDayTypeRef(dayTypeRef);

        OperatingPeriodRefStructure operatingPeriodRef = NETEX_FACTORY.createOperatingPeriodRefStructure();
        operatingPeriodRef.setRef(operatingPeriodId);

        dayTypeAssignment.setOperatingPeriodRef(operatingPeriodRef);
        return dayTypeAssignment;
    }

    private OperatingPeriod createTestOperatingPeriod(String id, LocalDateTime start, LocalDateTime end) {
        OperatingPeriod operatingPeriod = NETEX_FACTORY.createOperatingPeriod();
        operatingPeriod.setId(id);
        operatingPeriod.setFromDate(start);
        operatingPeriod.setToDate(end);
        return operatingPeriod;
    }

}
