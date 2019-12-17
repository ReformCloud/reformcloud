package systems.reformcloud.reformcloud2.signs.util.sign.config;

public class SignSubLayout {

  public SignSubLayout(String[] lines, String block, int subID) {
    this.lines = lines;
    this.block = block;
    this.subID = subID;
  }

  private final String[] lines;

  private final String block;

  private final int subID;

  public String[] getLines() { return lines; }

  public String getBlock() { return block; }

  public int getSubID() { return subID; }
}
