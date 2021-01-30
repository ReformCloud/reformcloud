/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.cloudflare;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.application.Application;
import systems.reformcloud.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.cloudflare.api.CloudFlareHelper;
import systems.reformcloud.cloudflare.listener.ProcessListener;
import systems.reformcloud.cloudflare.update.CloudFlareAddonUpdater;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.language.LanguageFileHolder;
import systems.reformcloud.language.TranslationHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReformCloudApplication extends Application {

  private static final ProcessListener LISTENER = new ProcessListener();
  private static boolean loaded = false;
  private final ApplicationUpdateRepository applicationUpdateRepository = new CloudFlareAddonUpdater(this);

  @Override
  public void onEnable() {
    try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("language-cloudflare.properties")) {
      Properties properties = new Properties();
      properties.load(stream);
      TranslationHolder.registerLanguageFileHolder(LanguageFileHolder.properties("en", properties));
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    if (CloudFlareHelper.init(this.getDataDirectory().resolve("config.json"))) {
      System.err.println(TranslationHolder.translate("cloudflare-first-init"));
      return;
    }

    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(LISTENER);
    CloudFlareHelper.loadAlreadyRunning();
    loaded = true;
  }

  @Override
  public void onPreDisable() {
    if (!loaded) {
      return;
    }

    CloudFlareHelper.handleStop();
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).unregisterListener(LISTENER);
  }

  @Nullable
  @Override
  public ApplicationUpdateRepository getUpdateRepository() {
    return this.applicationUpdateRepository;
  }
}
