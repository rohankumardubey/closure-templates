/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy.jssrc.internal;

import com.google.common.collect.ImmutableSet;
import com.google.template.soy.base.internal.BaseUtils;

/**
 * Shared utilities specific to the JS Src backend.
 *
 * <p>Important: Do not use outside of Soy code (treat as superpackage-private).
 *
 */
public final class JsSrcUtils {


  private JsSrcUtils() {}


  /**
   * Builds a version of the given string that has literal Unicode Format characters (Unicode
   * category "Cf") changed to valid JavaScript Unicode escapes (i.e. &92;u####). If the provided
   * string doesn't have any Unicode Format characters, then the same string is returned.
   *
   * @param str The string to escape.
   * @return A version of the given string that has literal Unicode Format characters (Unicode
   * category "Cf") changed to valid JavaScript Unicode escapes (i.e. &92;u####).
   */
  static String escapeUnicodeFormatChars(String str) {

    int len = str.length();

    // Do a quick check first, because most strings do not contain Unicode format characters.
    boolean hasFormatChar = false;
    for (int i = 0; i < len; i++) {
      if (Character.getType(str.charAt(i)) == Character.FORMAT) {
        hasFormatChar = true;
        break;
      }
    }
    if (!hasFormatChar) {
      return str;
    }

    // Now we actually need to build a new string.
    StringBuilder out = new StringBuilder(len * 4 / 3);
    int codePoint;
    for (int i = 0; i < len; i += Character.charCount(codePoint)) {
      codePoint = str.codePointAt(i);
      if (Character.getType(codePoint) == Character.FORMAT) {
        BaseUtils.appendHexEscape(out, codePoint);
      } else {
        out.appendCodePoint(codePoint);
      }
    }
    return out.toString();
  }


  static final ImmutableSet<String> JS_LITERALS =
      ImmutableSet.of("null", "true", "false", "NaN", "Infinity", "undefined");

  static final ImmutableSet<String> JS_RESERVED_WORDS =
      ImmutableSet.of(
          "break",
          "case",
          "catch",
          "continue",
          "debugger",
          "default",
          "delete",
          "do",
          "else",
          "finally",
          "for",
          "function",
          "if",
          "in",
          "instanceof",
          "new",
          "return",
          "switch",
          "this",
          "throw",
          "try",
          "typeof",
          "var",
          "void",
          "while",
          "with",
          "class",
          "const",
          "enum",
          "export",
          "extends",
          "import",
          "super",
          "implements",
          "interface",
          "let",
          "package",
          "private",
          "protected",
          "public",
          "static",
          "yield",
          /* future reserved words */
          "async",
          "await");

  /**
   * Standard global objects and functions
   *
   * <p>https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects
   */
  static final ImmutableSet<String> JS_GLOBALS =
      ImmutableSet.of(
          "eval",
          "uneval",
          "isFinite",
          "isNaN",
          "parseFloat",
          "parseInt",
          "decodeURI",
          "decodeURIComponent",
          "encodeURI",
          "encodeURIComponent",
          "escape",
          "unescape",
          "Object",
          "Function",
          "Boolean",
          "Symbol",
          "Error",
          "EvalError",
          "InternalError",
          "RangeError",
          "ReferenceError",
          "SyntaxError",
          "TypeError",
          "URIError",
          "Number",
          "Math",
          "Date",
          "String",
          "RegExp",
          "Array",
          "Int8Array",
          "Uint8Array",
          "Uint8ClampedArray",
          "Int16Array",
          "Uint16Array",
          "Int32Array",
          "Uint32Array",
          "Float32Array",
          "Float64Array",
          "Map",
          "Set",
          "WeakMap",
          "WeakSet",
          "SIMD",
          "ArrayBuffer",
          "SharedArrayBuffer",
          "Atomics",
          "DataView",
          "JSON",
          "Promise",
          "Generator",
          "GeneratorFunction",
          "AsyncFunction",
          "Reflect",
          "Proxy",
          "Intl",
          "WebAssembly",
          "Iterator",
          "ParallelArray",
          "StopIteration",
          "arguments");
}
