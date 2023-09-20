package org.entur.netex.gtfs.export.stop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DefaultStopAreaRepositoryFactoryTest {

  @Test
  void testStopAreaRepositoryShouldThrowException() {
    DefaultStopAreaRepositoryFactory factory =
      new DefaultStopAreaRepositoryFactory();
    assertThrows(IllegalStateException.class, factory::getStopAreaRepository);
  }

  @Test
  void testStopAreaRepositoryShouldNotThrowException() {
    DefaultStopAreaRepositoryFactory factory =
      new DefaultStopAreaRepositoryFactory();
    factory.setStopAreaRepository(new DefaultStopAreaRepository());
    assertDoesNotThrow(factory::getStopAreaRepository);
  }
}
