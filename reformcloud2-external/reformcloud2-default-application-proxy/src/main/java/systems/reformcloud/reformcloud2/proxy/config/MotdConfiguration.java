package systems.reformcloud.reformcloud2.proxy.config;

public class MotdConfiguration {

    public MotdConfiguration(String firstLine, String secondLine, String[] playerInfo, String protocol, long waitUntilNextInSeconds) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.playerInfo = playerInfo;
        this.protocol = protocol;
        this.waitUntilNextInSeconds = waitUntilNextInSeconds;
    }

    private final String firstLine;

    private final String secondLine;

    private final String[] playerInfo;

    private final String protocol;

    private final long waitUntilNextInSeconds;

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public String getProtocol() {
        return protocol;
    }

    public long getWaitUntilNextInSeconds() {
        return waitUntilNextInSeconds;
    }
}
