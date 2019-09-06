package de.klaro.reformcloud2.executor.api.proxprox;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import io.gomint.proxprox.ProxProxProxy;
import io.gomint.proxprox.api.plugin.Plugin;
import io.gomint.proxprox.api.plugin.annotation.Description;
import io.gomint.proxprox.api.plugin.annotation.Name;
import io.gomint.proxprox.api.plugin.annotation.Version;

@Name("ReformCloud2ProxProxExecutor")
@Version(major = 2, minor = 0)
@Description("The reformcloud executor api")
public final class ProxProxLauncher extends Plugin {

    @Override
    public void onStartup() {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();
    }

    @Override
    public void onInstall() {
        new ProxProxExecutor(this);
    }

    @Override
    public void onUninstall() {
        ProxProxProxy.getInstance().getSyncTaskManager().killAll();
        ProxProxExecutor.getInstance().getNetworkClient().disconnect();
    }
}
