package systems.reformcloud.reformcloud2.executor.api.common.utils;

import org.junit.Assert;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;

import java.util.UUID;

public class ValidateTest {

    @Test
    public void testValidate() {
        Assert.assertThrows(IllegalArgumentException.class, () -> Conditions.isTrue(System.currentTimeMillis() < 0));
        Assert.assertThrows(NullPointerException.class, () -> Conditions.nonNull(System.getProperty(UUID.randomUUID().toString())));
    }
}
