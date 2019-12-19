package systems.reformcloud.reformcloud2.executor.api;

import java.util.HashMap;
import java.util.Map;

public enum ExecutorType {

    CONTROLLER(1, true),

    CLIENT(2, true),

    NODE(4, true),

    API(3, true),

    UNKNOWN(-1, false);

    ExecutorType(int id, boolean supported) {
        this.id = id;
        this.supported = supported;
    }

    private final int id;

    private final boolean supported;

    public int getId() {
        return id;
    }

    public boolean isSupported() {
        return supported;
    }

    /* ============================== */
    
    private static final Map<Integer, ExecutorType> BY_ID = new HashMap<>();

    static {
        for (ExecutorType executorType : values()) {
            BY_ID.put(executorType.getId(), executorType);
        }
    }

    public static ExecutorType getByID(int id) {
        return BY_ID.getOrDefault(id, UNKNOWN);
    }

}
