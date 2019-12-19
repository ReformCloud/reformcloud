package systems.reformcloud.reformcloud2.executor.api.common.groups.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;

import java.util.List;

public final class DefaultMainGroup extends MainGroup {

    public DefaultMainGroup(String name, List<String> subGroups) {
        super(name, subGroups);
    }
}
