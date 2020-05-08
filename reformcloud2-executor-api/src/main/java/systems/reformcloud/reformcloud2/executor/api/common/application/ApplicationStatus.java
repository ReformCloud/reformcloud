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
package systems.reformcloud.reformcloud2.executor.api.common.application;

/**
 * Represents the lifecycle of an application
 */
public enum ApplicationStatus {

    /**
     * The application is ready to get installed
     */
    INSTALLABLE,

    /**
     * The application is installed
     */
    INSTALLED,

    /**
     * The application is loaded
     */
    LOADED,

    /**
     * The application is enabled
     */
    ENABLED,

    /**
     * The application is ready to get disabled
     */
    PRE_DISABLE,

    /**
     * The application is disabled
     */
    DISABLED,

    /**
     * The application is uninstalling
     */
    UNINSTALLING,

    /**
     * The application is completely removed from the runtime
     */
    UNINSTALLED
}
