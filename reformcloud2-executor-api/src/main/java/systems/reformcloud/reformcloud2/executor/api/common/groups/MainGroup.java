package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;
import java.util.Objects;

public class MainGroup implements Nameable, SerializableObject {

    public static final TypeToken<MainGroup> TYPE = new TypeToken<MainGroup>() {
    };

    public MainGroup(String name, List<String> subGroups) {
        this.name = name;
        this.subGroups = subGroups;
    }

    private String name;

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

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeStringArray(this.subGroups);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.subGroups = buffer.readStringArray();
    }
}
