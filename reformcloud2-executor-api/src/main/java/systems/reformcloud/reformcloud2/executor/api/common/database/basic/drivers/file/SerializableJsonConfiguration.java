package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.derklaro.projects.deer.api.writer.FileWriter;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import javax.annotation.Nonnull;
import java.io.File;

public class SerializableJsonConfiguration extends JsonConfiguration implements FileWriter {

    public SerializableJsonConfiguration(JsonConfiguration parent) {
        super(parent.getJsonObject());
    }

    public SerializableJsonConfiguration(File file) {
        super(file);
    }

    public SerializableJsonConfiguration() {
        super();
    }

    @Nonnull
    @Override
    public String toWriteableString() {
        return this.toPrettyString();
    }
}
