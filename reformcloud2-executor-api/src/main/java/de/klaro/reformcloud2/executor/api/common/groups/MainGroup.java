package de.klaro.reformcloud2.executor.api.common.groups;

import de.klaro.reformcloud2.executor.api.common.utility.Nameable;

import java.util.List;

public class MainGroup implements Nameable {

    public MainGroup(String name, List<String> subGroups) {
        this.name = name;
        this.subGroups = subGroups;
    }

    private String name;

    private List<String> subGroups;

    @Override
    public String getName() {
        return name;
    }

    public List<String> getSubGroups() {
        return subGroups;
    }
}
