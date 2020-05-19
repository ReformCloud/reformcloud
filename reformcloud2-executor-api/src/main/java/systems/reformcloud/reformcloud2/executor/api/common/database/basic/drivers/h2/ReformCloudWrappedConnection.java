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
package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.h2;

import org.jetbrains.annotations.ApiStatus;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@ApiStatus.Internal
public final class ReformCloudWrappedConnection implements Connection {

    private final Connection parent;

    public ReformCloudWrappedConnection(Connection parent) {
        this.parent = parent;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return this.parent.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.parent.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.parent.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.parent.nativeSQL(sql);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.parent.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.parent.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        this.parent.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.parent.rollback();
    }

    @Override
    public void close() {
        // DO NOTHING
    }

    public void disconnect() throws SQLException {
        this.parent.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.parent.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.parent.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.parent.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.parent.setReadOnly(readOnly);
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.parent.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.parent.setCatalog(catalog);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.parent.getTransactionIsolation();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.parent.setTransactionIsolation(level);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.parent.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.parent.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.parent.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.parent.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.parent.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.parent.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.parent.setTypeMap(map);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.parent.getHoldability();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.parent.setHoldability(holdability);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.parent.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.parent.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.parent.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.parent.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.parent.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.parent.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.parent.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.parent.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.parent.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.parent.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.parent.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.parent.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.parent.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.parent.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.parent.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.parent.setClientInfo(name, value);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.parent.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.parent.getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.parent.setClientInfo(properties);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.parent.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.parent.createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.parent.getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.parent.setSchema(schema);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.parent.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.parent.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.parent.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.parent.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.parent.isWrapperFor(iface);
    }
}
