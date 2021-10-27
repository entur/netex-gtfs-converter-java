package org.entur.netex.gtfs.export;

import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.ServiceJourney;

public class TestUtil {

    private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();


    public static ServiceJourney createTestServiceJourney(String serviceJourneyId, String dayTypeId) {
        ServiceJourney serviceJourney = new ServiceJourney();
        serviceJourney.setId(serviceJourneyId);
        DayTypeRefs_RelStructure dayTypeStruct = NETEX_FACTORY.createDayTypeRefs_RelStructure();
        serviceJourney.setDayTypes(dayTypeStruct);
        DayTypeRefStructure dayTypeRefStruct = NETEX_FACTORY.createDayTypeRefStructure();
        dayTypeRefStruct.setRef(dayTypeId);
        serviceJourney.getDayTypes().getDayTypeRef().add(NETEX_FACTORY.createDayTypeRef(dayTypeRefStruct));
        return serviceJourney;
    }
}
