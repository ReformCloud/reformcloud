package systems.reformcloud.reformcloud2.signs.util.sign.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.signs.util.sign.config.util.LayoutContext;

import java.io.Serializable;
import java.util.List;

public class SignLayout implements Serializable, Cloneable {

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

    private final LayoutContext context;

    private final String target;

    private final boolean searchingLayoutWhenFull;

    private final boolean showMaintenanceProcessesOnSigns;

    private final List<SignSubLayout> searchingLayouts;

    private final List<SignSubLayout> waitingForConnectLayout;

    private final List<SignSubLayout> emptyLayout;

    private final List<SignSubLayout> onlineLayout;

    private final List<SignSubLayout> fullLayout;

    private final List<SignSubLayout> maintenanceLayout;

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
}
