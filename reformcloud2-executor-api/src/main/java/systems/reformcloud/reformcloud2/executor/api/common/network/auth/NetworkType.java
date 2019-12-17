package systems.reformcloud.reformcloud2.executor.api.common.network.auth;

public enum NetworkType {

  /**
   * The current auth component is a process (Process to Controller auth)
   */
  PROCESS,

  /**
   * The current auth component is a client (Client to Controller auth)
   */
  CLIENT,

  /**
   * The current auth component is a node (Node to Node auth)
   */
  NODE
}
