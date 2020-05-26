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
package systems.reformcloud.reformcloud2.executor.api.bungee.util;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EmptyProxiedPlayer implements ProxiedPlayer {

    public EmptyProxiedPlayer(@NotNull PendingConnection pendingConnection) {
        this.pendingConnection = pendingConnection;
    }

    private final PendingConnection pendingConnection;

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String s) {
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents) {
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent baseComponent) {
    }

    @Override
    public void connect(ServerInfo serverInfo) {
    }

    @Override
    public void connect(ServerInfo serverInfo, ServerConnectEvent.Reason reason) {
    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {
    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
    }

    @Override
    public void connect(ServerConnectRequest serverConnectRequest) {
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sendData(String s, byte[] bytes) {
    }

    @Override
    public PendingConnection getPendingConnection() {
        return this.pendingConnection;
    }

    @Override
    public void chat(String s) {
    }

    @Override
    public ServerInfo getReconnectServer() {
        return null;
    }

    @Override
    public void setReconnectServer(ServerInfo serverInfo) {
    }

    @Override
    public String getUUID() {
        return this.pendingConnection.getUUID();
    }

    @Override
    public UUID getUniqueId() {
        return this.pendingConnection.getUniqueId();
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public byte getViewDistance() {
        return 0;
    }

    @Override
    public ChatMode getChatMode() {
        return null;
    }

    @Override
    public boolean hasChatColors() {
        return false;
    }

    @Override
    public SkinConfiguration getSkinParts() {
        return null;
    }

    @Override
    public MainHand getMainHand() {
        return null;
    }

    @Override
    public void setTabHeader(BaseComponent baseComponent, BaseComponent baseComponent1) {
    }

    @Override
    public void setTabHeader(BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {
    }

    @Override
    public void resetTabHeader() {
    }

    @Override
    public void sendTitle(Title title) {
    }

    @Override
    public boolean isForgeUser() {
        return false;
    }

    @Override
    public Map<String, String> getModList() {
        return null;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public String getName() {
        return this.pendingConnection.getName();
    }

    @Override
    public void sendMessage(String s) {
    }

    @Override
    public void sendMessages(String... strings) {
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
    }

    @Override
    public Collection<String> getGroups() {
        return null;
    }

    @Override
    public void addGroups(String... strings) {
    }

    @Override
    public void removeGroups(String... strings) {
    }

    @Override
    public boolean hasPermission(String s) {
        return ProxyServer.getInstance().getPluginManager().callEvent(new PermissionCheckEvent(this, s, false)).hasPermission();
    }

    @Override
    public void setPermission(String s, boolean b) {
    }

    @Override
    public Collection<String> getPermissions() {
        return null;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.pendingConnection.getAddress();
    }

    @Override
    public SocketAddress getSocketAddress() {
        return this.pendingConnection.getSocketAddress();
    }

    @Override
    public void disconnect(String s) {
    }

    @Override
    public void disconnect(BaseComponent... baseComponents) {
    }

    @Override
    public void disconnect(BaseComponent baseComponent) {
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Unsafe unsafe() {
        return null;
    }
}
