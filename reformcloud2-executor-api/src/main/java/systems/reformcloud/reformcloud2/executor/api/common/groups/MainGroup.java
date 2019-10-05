package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

public class MainGroup implements Nameable {

    public static final TypeToken<MainGroup> TYPE = new TypeToken<MainGroup>() {};

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
