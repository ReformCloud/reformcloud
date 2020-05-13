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
package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

import org.jetbrains.annotations.NotNull;

public final class DefaultRepositories {

    public static final Repository MAVEN_CENTRAL = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "Central";
        }

        @NotNull
        @Override
        public String getURL() {
            return "https://repo.maven.apache.org/maven2/";
        }
    };

    public static final Repository JITPACK = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "JitPack";
        }

        @NotNull
        @Override
        public String getURL() {
            return "https://jitpack.io/";
        }
    };

    public static final Repository SONATYPE = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "SonaType";
        }

        @NotNull
        @Override
        public String getURL() {
            return "https://oss.sonatype.org/content/repositories/releases/";
        }
    };

    public static final Repository J_CENTER = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "JCenter";
        }

        @NotNull
        @Override
        public String getURL() {
            return "http://jcenter.bintray.com/";
        }
    };

    private DefaultRepositories() {
        throw new UnsupportedOperationException();
    }
}
