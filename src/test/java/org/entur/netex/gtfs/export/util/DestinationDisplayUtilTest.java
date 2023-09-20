package org.entur.netex.gtfs.export.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.entur.netex.gtfs.export.repository.NetexDatasetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Via_VersionedChildStructure;
import org.rutebanken.netex.model.Vias_RelStructure;

class DestinationDisplayUtilTest {

  private static final ObjectFactory NETEX_FACTORY = new ObjectFactory();

  private static final String MAIN_FRONT_TEXT = "Test-FrontText";
  private static final String VIA1_FRONT_TEXT = "Test-Via1";
  private static final String VIA2_FRONT_TEXT = "Test-Via2";

  private static final String MAIN_DESTINATION_DISPLAY_ID =
    "ENT:DestinationDisplay:1";
  private static final String VIA1_DESTINATION_DISPLAY_ID =
    "ENT:DestinationDisplay:2";
  private static final String VIA2_DESTINATION_DISPLAY_ID =
    "ENT:DestinationDisplay:3";

  @Test
  void testDestinationDisplayWithoutVia() {
    DestinationDisplay mainDestinationDisplay = createTestDestinationDisplay(
      MAIN_DESTINATION_DISPLAY_ID,
      MAIN_FRONT_TEXT
    );
    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    String frontTextWithComputedVias =
      DestinationDisplayUtil.getHeadSignFromDestinationDisplay(
        mainDestinationDisplay,
        netexDatasetRepository
      );
    Assertions.assertEquals(MAIN_FRONT_TEXT, frontTextWithComputedVias);
  }

  @Test
  void testDestinationDisplayWithOneVia() {
    DestinationDisplay mainDestinationDisplay = createTestDestinationDisplay(
      MAIN_DESTINATION_DISPLAY_ID,
      MAIN_FRONT_TEXT
    );
    DestinationDisplay via1DestinationDisplay = createTestDestinationDisplay(
      VIA1_DESTINATION_DISPLAY_ID,
      VIA1_FRONT_TEXT
    );
    addVia(mainDestinationDisplay, via1DestinationDisplay);

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(
      netexDatasetRepository.getDestinationDisplayById(
        VIA1_DESTINATION_DISPLAY_ID
      )
    )
      .thenReturn(via1DestinationDisplay);

    String frontTextWithComputedVias =
      DestinationDisplayUtil.getHeadSignFromDestinationDisplay(
        mainDestinationDisplay,
        netexDatasetRepository
      );

    Assertions.assertEquals(
      MAIN_FRONT_TEXT + " via " + VIA1_FRONT_TEXT,
      frontTextWithComputedVias
    );
  }

  @Test
  void testDestinationDisplayWithTwoVia() {
    DestinationDisplay mainDestinationDisplay = createTestDestinationDisplay(
      MAIN_DESTINATION_DISPLAY_ID,
      MAIN_FRONT_TEXT
    );
    DestinationDisplay via1DestinationDisplay = createTestDestinationDisplay(
      VIA1_DESTINATION_DISPLAY_ID,
      VIA1_FRONT_TEXT
    );
    DestinationDisplay via2DestinationDisplay = createTestDestinationDisplay(
      VIA2_DESTINATION_DISPLAY_ID,
      VIA2_FRONT_TEXT
    );
    addVia(
      mainDestinationDisplay,
      via1DestinationDisplay,
      via2DestinationDisplay
    );

    NetexDatasetRepository netexDatasetRepository = mock(
      NetexDatasetRepository.class
    );
    when(
      netexDatasetRepository.getDestinationDisplayById(
        VIA1_DESTINATION_DISPLAY_ID
      )
    )
      .thenReturn(via1DestinationDisplay);
    when(
      netexDatasetRepository.getDestinationDisplayById(
        VIA2_DESTINATION_DISPLAY_ID
      )
    )
      .thenReturn(via2DestinationDisplay);

    String frontTextWithComputedVias =
      DestinationDisplayUtil.getHeadSignFromDestinationDisplay(
        mainDestinationDisplay,
        netexDatasetRepository
      );

    Assertions.assertEquals(
      MAIN_FRONT_TEXT + " via " + VIA1_FRONT_TEXT + '/' + VIA2_FRONT_TEXT,
      frontTextWithComputedVias
    );
  }

  private DestinationDisplay createTestDestinationDisplay(
    String id,
    String frontText
  ) {
    DestinationDisplay destinationDisplay = new DestinationDisplay();
    destinationDisplay.setId(id);
    MultilingualString frontTextMultilingualString = new MultilingualString();
    frontTextMultilingualString.setValue(frontText);
    destinationDisplay.setFrontText(frontTextMultilingualString);
    return destinationDisplay;
  }

  private void addVia(
    DestinationDisplay destinationDisplay,
    DestinationDisplay... vias
  ) {
    Vias_RelStructure viasRelStructure =
      NETEX_FACTORY.createVias_RelStructure();
    destinationDisplay.setVias(viasRelStructure);

    for (DestinationDisplay via : vias) {
      DestinationDisplayRefStructure destinationDisplayRefStructure =
        NETEX_FACTORY.createDestinationDisplayRefStructure();
      destinationDisplayRefStructure.setRef(via.getId());
      Via_VersionedChildStructure viaVersionedChildStructure = NETEX_FACTORY
        .createVia_VersionedChildStructure()
        .withDestinationDisplayRef(destinationDisplayRefStructure);
      destinationDisplay.getVias().getVia().add(viaVersionedChildStructure);
    }
  }
}
