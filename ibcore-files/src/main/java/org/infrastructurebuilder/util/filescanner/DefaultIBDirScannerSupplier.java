/*
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
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
package org.infrastructurebuilder.util.filescanner;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

import javax.inject.Named;

import org.infrastructurebuilder.util.core.AbstractBaseIBDirScan;
import org.infrastructurebuilder.util.core.IBDirScan;
import org.infrastructurebuilder.util.core.IBDirScanner;
import org.infrastructurebuilder.util.core.IBDirScannerSupplier;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.StringListSupplier;

@Named
public class DefaultIBDirScannerSupplier implements IBDirScannerSupplier {
  private static final String DEEP_TREE_MATCH = "**";

  // Copied from Ant DirectoryScanner (and should be updated regularly) to
  // preserve parity between the two
  protected static final List<String> DEFAULTEXCLUDES = List.of( // NOSONAR
      // Miscellaneous typical temporary files
      DEEP_TREE_MATCH + "/*~", DEEP_TREE_MATCH + "/#*#", DEEP_TREE_MATCH + "/.#*", DEEP_TREE_MATCH + "/%*%",
      DEEP_TREE_MATCH + "/._*",
      // CVS
      DEEP_TREE_MATCH + "/CVS", DEEP_TREE_MATCH + "/CVS/" + DEEP_TREE_MATCH, DEEP_TREE_MATCH + "/.cvsignore",

      // SCCS
      DEEP_TREE_MATCH + "/SCCS", DEEP_TREE_MATCH + "/SCCS/" + DEEP_TREE_MATCH,

      // Visual SourceSafe
      DEEP_TREE_MATCH + "/vssver.scc",

      // Subversion
      DEEP_TREE_MATCH + "/.svn", DEEP_TREE_MATCH + "/.svn/" + DEEP_TREE_MATCH,

      // Git
      DEEP_TREE_MATCH + "/.git", DEEP_TREE_MATCH + "/.git/" + DEEP_TREE_MATCH, DEEP_TREE_MATCH + "/.gitattributes",
      DEEP_TREE_MATCH + "/.gitignore", DEEP_TREE_MATCH + "/.gitmodules",

      // Mercurial
      DEEP_TREE_MATCH + "/.hg", DEEP_TREE_MATCH + "/.hg/" + DEEP_TREE_MATCH, DEEP_TREE_MATCH + "/.hgignore",
      DEEP_TREE_MATCH + "/.hgsub", DEEP_TREE_MATCH + "/.hgsubstate", DEEP_TREE_MATCH + "/.hgtags",

      // Bazaar
      DEEP_TREE_MATCH + "/.bzr", DEEP_TREE_MATCH + "/.bzr/" + DEEP_TREE_MATCH, DEEP_TREE_MATCH + "/.bzrignore",

      // Mac
      DEEP_TREE_MATCH + "/.DS_Store"
      );

  private final Path root;
  private List<String> includes;
  private List<String> excludes;
  private boolean excludeDirectories;
  private boolean excludeHidden;
  private boolean excludeDotFiles;

  private Boolean excludeFiles;

  public DefaultIBDirScannerSupplier(PathSupplier root,
      // Includes
      StringListSupplier includes,
      // Excludes
      StringListSupplier excludes,
      // Exclude dirs
      BooleanSupplier excludeDirectories,
      // Exclude dirs
      BooleanSupplier excludeFiles,
      // Exclude hidden
      BooleanSupplier excludeHidden,
      // DOT files aren't PRECISELY the same as hidden sometimes
      BooleanSupplier excludeDotFiles,
      // Ignore symlinks (this should almost always be true
      BooleanSupplier ignoreSymlinks,
      // Add the Ant default excludes list
      BooleanSupplier includeDefaultExcludes) {
    this.root = requireNonNull(root.get());
    this.includes = new ArrayList<>(requireNonNull(includes.get()));
    this.excludes = new ArrayList<>(requireNonNull(excludes.get()));
    this.excludeDirectories = requireNonNull(excludeDirectories.getAsBoolean());
    this.excludeFiles = requireNonNull(excludeFiles.getAsBoolean());
    this.excludeHidden = requireNonNull(excludeHidden.getAsBoolean());
    this.excludeDotFiles = requireNonNull(excludeDotFiles.getAsBoolean());
    if (includeDefaultExcludes.getAsBoolean())
      this.excludes.addAll(DEFAULTEXCLUDES);

  }

  @Override
  public IBDirScanner get() {
    return new DefaultIBDirScanner(root, metafilter);
  }

  private BiFunction<Path, BasicFileAttributes, Boolean> metafilter = (path, attr) -> {
    if (attr.isSymbolicLink()) {
      // TODO I think we should ignore all symlinks but...
      return false;
    } else if (attr.isRegularFile()) {
      if (excludeFiles)
        return false;
    } else if (attr.isDirectory()) {
      if (excludeDirectories)
        return false;
    } else {
      // Not a symlink, regular file, or dir.
      // TODO Decide what to do here
    }
    if (excludeHidden && path.toFile().isHidden())
      return false;
    if (excludeDotFiles && path.getFileName().startsWith("."))
      return false;
    var localFS = path.getFileSystem();
    boolean include = false;
    if (includes.size() > 0) {
      for (String s : includes) {
        if (localFS.getPathMatcher("glob:" + s).matches(path)) {
          include = true;
          break;
        }
      }
    }
    if (excludes.size() > 0) {
      for (String s: excludes) {
        if (localFS.getPathMatcher("glob:" + s).matches(path)) {
          include = false;
          break;
        }
      }
    }
    return include;
  };

  public static class DefaultIBDirScanner implements IBDirScanner {
//    public final static Function<List<String>, String[]> mapToArray = (i) -> {
//      return i.toArray(new String[i.size()]);
//    };
    private final Path srcDir;
    private BiFunction<Path, BasicFileAttributes, Boolean> exclusionFunction;

    public DefaultIBDirScanner(Path p, BiFunction<Path, BasicFileAttributes, Boolean> exclusionFunction) {
      this.srcDir = requireNonNull(p);
      this.exclusionFunction = exclusionFunction;
    }

    public IBDirScan scan() throws IOException {
      AbstractBaseIBDirScan dirscan = new AbstractBaseIBDirScan() {
        @Override
        public BiFunction<Path, BasicFileAttributes, Boolean> getExclusionFunction() {
          return exclusionFunction;
        }
      };
      Files.walkFileTree(this.srcDir,dirscan);

      return dirscan;
    }
  }

}