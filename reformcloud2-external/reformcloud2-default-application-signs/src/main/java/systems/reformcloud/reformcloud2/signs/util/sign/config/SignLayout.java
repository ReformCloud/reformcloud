package systems.reformcloud.reformcloud2.signs.util.sign.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.signs.util.sign.config.util.LayoutContext;

import java.util.List;

public class SignLayout implements SerializableObject, Cloneable {

    public SignLayout() {
    }

    public SignLayout(
            @NotNull LayoutContext context,
            @Nullable String target,
            boolean searchingLayoutWhenFull,
            boolean showMaintenanceProcessesOnSigns,
            @NotNull List<SignSubLayout> searchingLayouts,
            @NotNull List<SignSubLayout> waitingForConnectLayout,
            @NotNull List<SignSubLayout> emptyLayout,
            @NotNull List<SignSubLayout> onlineLayout,
            @NotNull List<SignSubLayout> fullLayout,
            @NotNull List<SignSubLayout> maintenanceLayout) {
        this.context = context;
        this.target = target;
        this.searchingLayoutWhenFull = searchingLayoutWhenFull;
        this.showMaintenanceProcessesOnSigns = showMaintenanceProcessesOnSigns;
        this.searchingLayouts = searchingLayouts;
        this.waitingForConnectLayout = waitingForConnectLayout;
        this.emptyLayout = emptyLayout;
        this.onlineLayout = onlineLayout;
        this.fullLayout = fullLayout;
        this.maintenanceLayout = maintenanceLayout;
    }

    private LayoutContext context;

    private String target;

    private boolean searchingLayoutWhenFull;

    private boolean showMaintenanceProcessesOnSigns;

    private List<SignSubLayout> searchingLayouts;

    private List<SignSubLayout> waitingForConnectLayout;

    private List<SignSubLayout> emptyLayout;

    private List<SignSubLayout> onlineLayout;

    private List<SignSubLayout> fullLayout;

    private List<SignSubLayout> maintenanceLayout;

    public LayoutContext getContext() {
        return context;
    }

    public String getTarget() {
        return target;
    }

    public boolean isSearchingLayoutWhenFull() {
        return searchingLayoutWhenFull;
    }

    public boolean isShowMaintenanceProcessesOnSigns() {
        return showMaintenanceProcessesOnSigns;
    }

    public List<SignSubLayout> getSearchingLayouts() {
        return searchingLayouts;
    }

    public List<SignSubLayout> getWaitingForConnectLayout() {
        return waitingForConnectLayout;
    }

    public List<SignSubLayout> getEmptyLayout() {
        return emptyLayout;
    }

    public List<SignSubLayout> getOnlineLayout() {
        return onlineLayout;
    }

    public List<SignSubLayout> getFullLayout() {
        return fullLayout;
    }

    public List<SignSubLayout> getMaintenanceLayout() {
        return maintenanceLayout;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeInt(this.context.ordinal());
        buffer.writeString(this.target);
        buffer.writeBoolean(this.searchingLayoutWhenFull);
        buffer.writeBoolean(this.showMaintenanceProcessesOnSigns);
        buffer.writeObjects(this.searchingLayouts);
        buffer.writeObjects(this.waitingForConnectLayout);
        buffer.writeObjects(this.emptyLayout);
        buffer.writeObjects(this.onlineLayout);
        buffer.writeObjects(this.fullLayout);
        buffer.writeObjects(this.maintenanceLayout);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.context = LayoutContext.values()[buffer.readInt()];
        this.target = buffer.readString();
        this.searchingLayoutWhenFull = buffer.readBoolean();
        this.showMaintenanceProcessesOnSigns = buffer.readBoolean();
        this.searchingLayouts = buffer.readObjects(SignSubLayout.class);
        this.waitingForConnectLayout = buffer.readObjects(SignSubLayout.class);
        this.emptyLayout = buffer.readObjects(SignSubLayout.class);
        this.onlineLayout = buffer.readObjects(SignSubLayout.class);
        this.fullLayout = buffer.readObjects(SignSubLayout.class);
        this.maintenanceLayout = buffer.readObjects(SignSubLayout.class);
    }
}
