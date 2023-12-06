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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class ConfigSegmentParser {
    enum S {
        BEGIN(0),
        SLASH_1(1),
        STAR_1(2),
        COMMENT(3),
        STAR_2(4),
        SLASH_2(5),
        CONTINUE(6),
        SLASH_3(7),
        SEPAR(8);

        private int value;

        S(int value) {
            this.value = value;
        }
    }

    private static S[][] transionTable = new S[][]{
/*         BEGIN    , SLASH_1  ,  STAR_1  , COMMENT  , STAR_2   , SLASH2   , CONTINUE , SLASH3   , SEPARATOR    */
/* '/' */ {S.SLASH_1, S.SLASH_1, S.COMMENT, S.COMMENT, S.SLASH_2, S.SLASH_3, S.SLASH_3, S.SLASH_3, S.SLASH_1},
/* '*' */ {S.BEGIN, S.STAR_1, S.STAR_2, S.STAR_2, S.STAR_2, S.CONTINUE, S.CONTINUE, S.STAR_1, S.BEGIN},
/* '.' */ {S.SEPAR, S.SEPAR, S.COMMENT, S.COMMENT, S.COMMENT, S.SEPAR, S.SEPAR, S.SEPAR, S.SEPAR},
/*  c  */ {S.BEGIN, S.BEGIN, S.COMMENT, S.COMMENT, S.COMMENT, S.CONTINUE, S.CONTINUE, S.BEGIN, S.BEGIN}
    };

    private static String[][] addSymbolTable = new String[][]{
            /*         BEGIN    , SLASH_1  ,  STAR_1  , COMMENT  , STAR_2   , SLASH2   , CONTINUE , SLASH3   , SEPARATOR    */
            /* '/' */ {null, "/" /*duplicate*/, null, null, null, null, null, "/", null},
            /* '*' */ {"*", null, null, null, null, "*", "*", null, "*"},
            /* '.' */ {null, "/" /*previous*/, null, null, null, null, null, "/", null},
            /*  c  */ {"c", "/c", null, null, null, "c", "c", "/c", "c"}
    };

    private static boolean[][] exceptionTable = new boolean[][]{
            /*         BEGIN    , SLASH_1  ,  STAR_1  , COMMENT  , STAR_2   , SLASH2   , CONTINUE , SLASH3   , SEPARATOR    */
            /* '/' */ {false, false, false, false, false, false, false, false, false},
            /* '*' */ {false, false, false, false, false, false, false, true, false},
            /* '.' */ {false, false, false, false, false, false, false, false, false},
            /*  c  */ {false, false, false, false, false, false, false, false, false},
    };

    static List<ConfigPath.ConfigPathSegment> parse(String path) {
        if (path == null) {
            return Collections.EMPTY_LIST;
        }
        final List<ConfigPath.ConfigPathSegment> segments = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        String append = null;
        S state = S.BEGIN;
        boolean exception = false;
        int tableIndex = -1;
        for (int i = 0; i != path.length(); i++) {
            char c = path.charAt(i);
            switch (c) {
                case '/':
                    tableIndex = 0;
                    break;
                case '*':
                    tableIndex = 1;
                    break;
                case '.':
                    tableIndex = 2;
                    break;
                default:
                    tableIndex = 3;
                    break;
            }
            append = addSymbolTable[tableIndex][state.value];
            exception = exceptionTable[tableIndex][state.value];
            state = transionTable[tableIndex][state.value];

            if (append != null) {
                sb.append(append.replace('c', c));
            }
            if (exception) {
                throw new ConfigPathException("Config path segment cannot contain two comments:" + path);
            }
            if (S.SEPAR == state) {
                final ConfigPath.ConfigPathSegment segment = new ConfigPath.ConfigPathSegment(sb.toString());
                segments.add(segment);
                sb = new StringBuilder();
            }
        }
        switch (state) {
            case STAR_1:
            case COMMENT:
            case STAR_2:
                throw new ConfigPathException("Config path segment contains unclosed comment:" + path);
            default:
                if (sb.length() != 0) {
                    final ConfigPath.ConfigPathSegment segment = new ConfigPath.ConfigPathSegment(sb.toString());
                    segments.add(segment);
                }
                break;
        }

        return segments;
    }
}
