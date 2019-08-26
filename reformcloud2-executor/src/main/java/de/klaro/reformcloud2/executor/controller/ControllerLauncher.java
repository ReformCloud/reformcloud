package de.klaro.reformcloud2.executor.controller;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;

public final class ControllerLauncher {

    public static synchronized void main(String[] args) {
        StringUtil.sendHeader();
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();

        new ControllerExecutor();
    }
}
