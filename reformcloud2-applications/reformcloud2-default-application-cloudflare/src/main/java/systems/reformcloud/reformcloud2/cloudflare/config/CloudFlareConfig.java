/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.cloudflare.config;

import com.google.gson.reflect.TypeToken;

public class CloudFlareConfig {

    public static final TypeToken<CloudFlareConfig> TYPE_TOKEN = new TypeToken<CloudFlareConfig>() {
    };
    private final String email;
    private final String apiToken;
    private final String domainName;
    private final String zoneId;
    private final String subDomain;

    public CloudFlareConfig(String email, String apiToken, String domainName, String zoneId, String subDomain) {
        this.email = email;
        this.apiToken = apiToken;
        this.domainName = domainName;
        this.zoneId = zoneId;
        this.subDomain = subDomain;
    }

    public String getEmail() {
        return email;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getSubDomain() {
        return subDomain;
    }
}
