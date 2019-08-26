package de.klaro.reformcloud2.executor.api.common.groups.basic;

import de.klaro.reformcloud2.executor.api.common.groups.MainGroup;

import java.util.List;

public final class DefaultMainGroup extends MainGroup {

    public DefaultMainGroup(String name, List<String> subGroups) {
        super(name, subGroups);
    }
}
