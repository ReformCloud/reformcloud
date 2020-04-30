package systems.reformcloud.reformcloud2.signs.util.sign.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class SignSubLayout implements SerializableObject {

    public SignSubLayout() {
    }

    public SignSubLayout(String[] lines, String block, int subID) {
        this.lines = lines;
        this.block = block;
        this.subID = subID;
    }

    private String[] lines;

    private String block;

    private int subID;

    public String[] getLines() {
        return lines;
    }

    public String getBlock() {
        return block;
    }

    public int getSubID() {
        return subID;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeStringArrays(this.lines);
        buffer.writeString(this.block);
        buffer.writeInt(this.subID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.lines = buffer.readStringArrays();
        this.block = buffer.readString();
        this.subID = buffer.readInt();
    }
}
