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
package systems.reformcloud.reformcloud2.permissions.gomint;

import io.gomint.entity.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.permissions.gomint.manager.DefaultPermissionManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class GoMintUtil {

    private GoMintUtil() {
        throw new UnsupportedOperationException();
    }

    private static final Field PERM;

    static {
        try {
            Class<?> clazz = Class.forName("io.gomint.server.entity.EntityPlayer");
            PERM = clazz.getDeclaredField("permissionManager");
            PERM.setAccessible(true);

            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        Field modifiers = Field.class.getDeclaredField("modifiers");
                        modifiers.setAccessible(true);
                        modifiers.setInt(PERM, modifiers.getModifiers() & ~Modifier.FINAL);
                        return null;
                    } catch (NoSuchFieldException exception) {
                        throw new IllegalStateException("Modifiers field not found", exception); // Should never happen
                    } catch (IllegalAccessException exception) {
                        throw new RuntimeException(exception); // Should never happen
                    }
                }
            });
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to find gomint EntityPlayer implementation", exception);
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Unable to find the \"permissionManager\" field in GoMint EntityPlayer impl", exception);
        }
    }

    public static void inject(@NotNull EntityPlayer entityPlayer) {
        if (PERM == null) {
            return;
        }

        try {
            PERM.set(entityPlayer, new DefaultPermissionManager(entityPlayer.getUUID()));
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }
}
