package systems.reformcloud.reformcloud2.cloudflare.config;

import com.google.gson.reflect.TypeToken;

public class CloudFlareConfig {

    public static final TypeToken<CloudFlareConfig> TYPE_TOKEN = new TypeToken<CloudFlareConfig>() {};

    public CloudFlareConfig(String email, String apiToken, String domainName, String zoneId, String subDomain) {
        this.email = email;
        this.apiToken = apiToken;
        this.domainName = domainName;
        this.zoneId = zoneId;
        this.subDomain = subDomain;
    }

    private final String email;

    private final String apiToken;

    private final String domainName;

    private final String zoneId;

    private final String subDomain;

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
