package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

public interface Repository {

    /**
     * @return The name of the repository
     */
    String getName();

    /**
     * @return The url of the repository
     */
    String getURL();
}
