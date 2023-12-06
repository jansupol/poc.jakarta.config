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

package poc.jakarta.config.internal.value.convertors;

import jakarta.config.ConfigException;
import poc.jakarta.config.value.NodeValueConvertor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class PrimitiveTypeConvertor implements NodeValueConvertor {

    private static enum PrimitiveTypes {
        BYTE(Byte.class, byte.class) {
            @Override
            public Object convert(String s) {
                return Byte.valueOf(s);
            }
        },
        SHORT(Short.class, short.class) {
            @Override
            public Object convert(String s) {
                return Short.valueOf(s);
            }
        },
        INTEGER(Integer.class, int.class) {
            @Override
            public Object convert(String s) {
                return Integer.valueOf(s);
            }
        },
        LONG(Long.class, long.class) {
            @Override
            public Object convert(String s) {
                return Long.valueOf(s);
            }
        },
        FLOAT(Float.class, float.class) {
            @Override
            public Object convert(String s) {
                return Float.valueOf(s);
            }
        },
        DOUBLE(Double.class, double.class) {
            @Override
            public Object convert(String s) {
                return Double.valueOf(s);
            }
        },
        BOOLEAN(Boolean.class, boolean.class) {
            @Override
            public Object convert(String s) {
                return Boolean.valueOf(s);
            }
        },
        CHAR(Character.class, char.class) {
            @Override
            public Object convert(String s) {
                if (s.length() != 1) {
                    throw new ConfigException("Cannot convert " + s + " to char");
                }
                return s.charAt(0);
            }
        };

        public static PrimitiveTypes forType(Class<?> type) {
            for (PrimitiveTypes primitive : PrimitiveTypes.values()) {
                if (primitive.supports(type)) {
                    return primitive;
                }
            }
            return null;
        }

        private final Class<?> wrapper;
        private final Class<?> primitive;

        private PrimitiveTypes(Class<?> wrapper, Class<?> primitive) {
            this.wrapper = wrapper;
            this.primitive = primitive;
        }

        public abstract Object convert(String s);

        public boolean supports(Class<?> type) {
            return type == wrapper || type == primitive;
        }
    }

    @Override
    public boolean isConvertible(Class<?> type, Type genericType, Class<?> defaultType) {
        if (PrimitiveTypes.forType(type) != null && CharSequence.class.isAssignableFrom(defaultType)) {
            return true;
        }
        if (Number.class.isAssignableFrom(type)) {
            final Constructor constructor;
            try {
                constructor = type.getDeclaredConstructor();
                if (constructor != null) {
                    return true;
                }
            } catch (NoSuchMethodException e) {
                // Other Number
            }
            if (AtomicInteger.class.isAssignableFrom(type) || AtomicLong.class.isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public <T> T convert(Class<T> type, Type genericType, Object defaultValue) {
        String value = defaultValue.toString();
        if (value.isEmpty()) {
            throw new ConfigException("Cannot convert empty value to " + type);
        }
        final PrimitiveTypes primitiveType = PrimitiveTypes.forType(type);
        if (primitiveType != null) {
            return (T) primitiveType.convert(value);
        }

        final Constructor constructor;
        try {
            constructor = type.getDeclaredConstructor();
            if (constructor != null) {
                try {
                    return type.cast(constructor.newInstance(value));
                } catch (Exception e) {
                    throw new ConfigException("Could not convert " + value + " to " + type);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (AtomicInteger.class.isAssignableFrom(type)) {
            return (T) new AtomicInteger((Integer) PrimitiveTypes.INTEGER.convert(value));
        }

        if (AtomicLong.class.isAssignableFrom(type)) {
            return (T) new AtomicLong((Long) PrimitiveTypes.LONG.convert(value));
        }

        throw new ConfigException("Unknown primitive type" + type);

    }
}
