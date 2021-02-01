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
package systems.reformcloud.cloudflare.config;

import java.util.Set;

public class CloudFlareConfig {

  private final Set<Entry> entries;

  public CloudFlareConfig(Set<Entry> entries) {
    this.entries = entries;
  }

  public Set<Entry> getEntries() {
    return this.entries;
  }

  public static class Entry {

    private final String email;
    private final String apiToken;
    private final String domainName;
    private final String zoneId;
    private final String subDomain;
    private final Set<String> targetProxyGroups;

    public Entry(String email, String apiToken, String domainName, String zoneId, String subDomain, Set<String> targetProxyGroups) {
      this.email = email;
      this.apiToken = apiToken;
      this.domainName = domainName;
      this.zoneId = zoneId;
      this.subDomain = subDomain;
      this.targetProxyGroups = targetProxyGroups;
    }

    public String getEmail() {
      return this.email;
    }

    public String getApiToken() {
      return this.apiToken;
    }

    public String getDomainName() {
      return this.domainName;
    }

    public String getZoneId() {
      return this.zoneId;
    }

    public String getSubDomain() {
      return this.subDomain;
    }

    public Set<String> getTargetProxyGroups() {
      return this.targetProxyGroups;
    }
  }
}
