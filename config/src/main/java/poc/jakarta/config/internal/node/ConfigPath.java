/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package poc.jakarta.config.internal.node;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class ConfigPath {

    private final List<ConfigPathSegment> segments;

    private ConfigPath(List<ConfigPathSegment> segments) {
        this.segments = segments;
    }

    public List<ConfigPathSegment> getSegments() {
        return segments;
    }

    public ConfigPathSegment getLastSegment() {
        return segments.get(segments.size() - 1);
    }

    public static final class ConfigPathSegment implements Comparable<ConfigPathSegment>, CharSequence {
        public ConfigPathSegment(String name) {
            this.name = name.trim();
        }

        private String name;

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(ConfigPathSegment o) {
            return name.compareTo(o.name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConfigPathSegment that = (ConfigPathSegment) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public int length() {
            return name.length();
        }

        @Override
        public char charAt(int index) {
            return name.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return name.subSequence(start, end);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Builder {
        private final List<ConfigPathSegment> segments;

        Builder(List<ConfigPathSegment> segments) {
            this.segments = segments;
        }

        public ConfigPath build() {
            return new ConfigPath(segments);
        }

        public Builder append(String path) {
            final List<ConfigPathSegment> segments = ConfigSegmentParser.parse(path);
            this.segments.addAll(segments);
            return this;
        }
    }

    public static Builder from(String path) {
        final List<ConfigPathSegment> segments = ConfigSegmentParser.parse(path);
        return new Builder(segments);
    }

    public static Builder from(ConfigPath path) {
        final List<ConfigPathSegment> segments = new LinkedList<>(path.getSegments());
        return new Builder(segments);
    }

    @Override
    public String toString() {
        return segments.stream().map(ConfigPathSegment::toString).reduce("", (a, b) -> a + (a.isEmpty() ? "" : ".") + b);
    }
}

