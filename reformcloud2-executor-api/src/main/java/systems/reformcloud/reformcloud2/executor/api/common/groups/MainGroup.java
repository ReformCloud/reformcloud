package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class MainGroup implements Nameable {

    public static final TypeToken<MainGroup> TYPE = new TypeToken<MainGroup>() {};

    public MainGroup(String name, List<String> subGroups) {
        this.name = name;
        this.subGroups = subGroups;
    }

    private final String name;

    private final List<String> subGroups;

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public List<String> getSubGroups() {
        return subGroups;
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
