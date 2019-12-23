package systems.reformcloud.reformcloud2.proxy.config;

public class TabListConfiguration {

    public TabListConfiguration(String header, String footer, long waitUntilNextInSeconds) {
        this.header = header;
        this.footer = footer;
        this.waitUntilNextInSeconds = waitUntilNextInSeconds;
    }

    private final String header;

    private final String footer;

    private final long waitUntilNextInSeconds;

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public long getWaitUntilNextInSeconds() {
        return waitUntilNextInSeconds;
    }
}
