package systems.reformcloud.reformcloud2.executor.node.process.log;

import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class NodeProcessScreenHandler {

    private NodeProcessScreenHandler() {
        throw new UnsupportedOperationException();
    }

    private static final Collection<NodeProcessScreen> SCREENS = new ArrayList<>();

    public static void registerScreen(NodeProcessScreen screen) {
        SCREENS.add(screen);
    }

    public static void unregisterScreen(UUID uniqueID) {
        Streams.filterToReference(SCREENS, e -> e.getUniqueID().equals(uniqueID)).ifPresent(SCREENS::remove);
    }

    public static ReferencedOptional<NodeProcessScreen> getScreen(UUID uniqueID) {
        return Streams.filterToReference(SCREENS, e -> e.getUniqueID().equals(uniqueID));
    }

}
