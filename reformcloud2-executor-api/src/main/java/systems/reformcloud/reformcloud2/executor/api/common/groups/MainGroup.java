package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;
import java.util.Objects;

public class MainGroup implements Nameable {

    public static final TypeToken<MainGroup> TYPE = new TypeToken<MainGroup>() {};

    public MainGroup(String name, List<String> subGroups) {
        this.name = name;
        this.subGroups = subGroups;
    }

    private final String name;

    private List<String> subGroups;

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    public List<String> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<String> subGroups) {
        this.subGroups = subGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MainGroup)) return false;
        MainGroup mainGroup = (MainGroup) o;
        return Objects.equals(getName(), mainGroup.getName()) &&
                Objects.equals(getSubGroups(), mainGroup.getSubGroups());
    }
}
