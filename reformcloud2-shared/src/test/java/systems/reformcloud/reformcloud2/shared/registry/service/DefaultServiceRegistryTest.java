package systems.reformcloud.reformcloud2.shared.registry.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;
import systems.reformcloud.reformcloud2.executor.api.registry.service.exception.ProviderImmutableException;
import systems.reformcloud.reformcloud2.executor.api.registry.service.exception.ProviderNotRegisteredException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultServiceRegistryTest {

  private final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();

  @Test
  @Order(1)
  void testSetProvider() {
    this.serviceRegistry.setProvider(AService.class, new AService(), true);
    Assertions.assertEquals(1, this.serviceRegistry.getRegisteredServices().size());
    Assertions.assertThrows(ProviderImmutableException.class, () -> this.serviceRegistry.setProvider(AService.class, new AService()));
  }

  @Test
  @Order(2)
  void testGetProvider() {
    Assertions.assertTrue(this.serviceRegistry.getProvider(AService.class).isPresent());
    Assertions.assertFalse(this.serviceRegistry.getProvider(DefaultServiceRegistryTest.class).isPresent());
  }

  @Test
  @Order(3)
  void testGetProviderUnchecked() {
    Assertions.assertNotNull(this.serviceRegistry.getProviderUnchecked(AService.class));
    Assertions.assertThrows(ProviderNotRegisteredException.class, () -> this.serviceRegistry.getProviderUnchecked(DefaultServiceRegistryTest.class));
  }

  @Test
  @Order(4)
  void testUnregisterService() {
    Assertions.assertThrows(ProviderImmutableException.class, () -> this.serviceRegistry.unregisterService(AService.class));
    Assertions.assertThrows(ProviderNotRegisteredException.class, () -> this.serviceRegistry.unregisterService(DefaultServiceRegistryTest.class));
  }

  public static class AService {
  }
}
