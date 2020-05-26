/*
 * Copyright 2011 Google Inc.
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

package com.google.template.soy.soytree;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.CopyState;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.soytree.defn.ImportedVar;
import java.util.List;

/**
 * Node representing a 'import' statement with a value expression.
 *
 * <p>Important: Do not use outside of Soy code (treat as superpackage-private).
 *
 */
public final class ImportNode extends AbstractSoyNode {

  /** The value expression that the variable is set to. */
  private final ImmutableList<VarDefn> identifiers;

  private final StringNode path;

  private final ImportType importType;

  /** Only CSS is supported right now. */
  public enum ImportType {
    CSS,
    PROTO,
    UNKNOWN
  }

  public ImportNode(int id, SourceLocation location, StringNode path, List<VarDefn> defns) {
    super(id, location);
    this.identifiers = ImmutableList.copyOf(defns);
    this.path = path;
    this.importType = importTypeForPath(path.getValue());
  }

  /**
   * Copy constructor.
   *
   * @param orig The node to copy.
   */
  private ImportNode(ImportNode orig, CopyState copyState) {
    super(orig, copyState);
    this.identifiers =
        orig.identifiers.stream().map(c -> ((ImportedVar) c).clone()).collect(toImmutableList());
    this.path = orig.path.copy(copyState);
    this.importType = orig.importType;
  }

  @Override
  public Kind getKind() {
    return Kind.IMPORT_NODE;
  }

  @Override
  public ImportNode copy(CopyState copyState) {
    return new ImportNode(this, copyState);
  }

  public boolean isSideEffectImport() {
    return identifiers.isEmpty();
  }

  public ImportType getImportType() {
    return importType;
  }

  private static ImportType importTypeForPath(String path) {
    // TODO(tomnguyen): Throw an error if any aliases are extracted from CSS imports, as they do not
    // exist yet.
    if (path.endsWith(".gss") || path.endsWith(".scss")) {
      return ImportType.CSS;
    }
    if (path.endsWith(".proto")) {
      return ImportType.PROTO;
    }
    // TODO(tomnguyen) Write a validation pass to verify imports.
    return ImportType.UNKNOWN;
  }

  public String getPath() {
    return path.getValue();
  }

  public SourceLocation getPathSourceLocation() {
    return path.getSourceLocation();
  }

  public ImmutableList<VarDefn> getIdentifiers() {
    return identifiers;
  }

  @Override
  public String toSourceString() {
    String exprs = "";
    if (!identifiers.isEmpty()) {
      exprs =
          String.format(
              "{%s} from ", identifiers.stream().map(VarDefn::name).collect(joining(",")));
    }
    return String.format("import %s'%s'", exprs, path.getValue());
  }
}
