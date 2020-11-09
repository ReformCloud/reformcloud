/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.signs.util.sign;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

import java.util.Collection;
import java.util.UUID;

public class CloudSign implements SerializableObject {

    public static final TypeToken<Collection<CloudSign>> COLLECTION_SIGN_TYPE = new TypeToken<>() {
    };

    private String group;
    private CloudLocation location;
    private UUID uniqueID;
    private ProcessInformation currentTarget;

    public CloudSign() {
    }

    public CloudSign(String group, CloudLocation location) {
        this.group = group;
        this.location = location;
        this.uniqueID = UUID.randomUUID();
        this.currentTarget = null;
    }

    public String getGroup() {
        return this.group;
    }

    public CloudLocation getLocation() {
        return this.location;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public ProcessInformation getCurrentTarget() {
        return this.currentTarget;
    }

    public void setCurrentTarget(ProcessInformation currentTarget) {
        this.currentTarget = currentTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CloudSign)) {
            return false;
        }

        CloudSign sign = (CloudSign) o;
        return sign.getUniqueID().equals(this.getUniqueID());
    }

    @Override
    public int hashCode() {
        return this.getUniqueID().hashCode();
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.group);
        buffer.writeObject(this.location);
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.group = buffer.readString();
        this.location = buffer.readObject(CloudLocation.class);
        this.uniqueID = buffer.readUniqueId();
    }
}
