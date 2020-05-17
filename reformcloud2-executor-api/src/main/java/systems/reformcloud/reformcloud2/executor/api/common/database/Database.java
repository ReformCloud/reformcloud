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
package systems.reformcloud.reformcloud2.executor.api.common.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;

/**
 * Represents an database of the cloud system
 *
 * @param <V> The type of the connection or types in it
 */
public abstract class Database<V> {

    protected static final DependencyLoader DEPENDENCY_LOADER = new DefaultDependencyLoader();

    /**
     * Connects to the database
     *
     * @param host     The host of the database
     * @param port     The port of the database
     * @param userName The user which should be used
     * @param password The password of the user
     * @param table    The table which the cloud system should use
     */
    public abstract void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table);

    /**
     * @return If the connection is open and writeable
     */
    public abstract boolean isConnected();

    /**
     * Disconnects from the database
     */
    public abstract void disconnect();

    /**
     * Creates a new database table
     *
     * @param name The name of the new table
     * @return If the table got created successfully
     */
    public abstract boolean createDatabase(String name);

    /**
     * Deletes a database
     *
     * @param name The name of the database
     * @return If the table got deleted successfully
     */
    public abstract boolean deleteDatabase(String name);

    /**
     * Creates a {@link DatabaseReader} for the table
     *
     * @param table The table for which the reader should be for
     * @return The creates {@link DatabaseReader} for the table
     */
    @Nullable
    public abstract DatabaseReader createForTable(String table);

    /**
     * @return The type parameter depending to the database type
     */
    @NotNull
    public abstract V get();
}
