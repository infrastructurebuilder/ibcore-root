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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.tools.ant.DirectoryScanner;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.IBDirScanner;
import org.infrastructurebuilder.util.core.IBDirScannerSupplier;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.StringListSupplier;

@Named
public class DefaultIBDirScannerSupplier implements IBDirScannerSupplier {

  private final Path root;
  private final List<String> includes;
  private final List<String> excludes;
  private final boolean excludeDirectories;
  private final boolean excludeHidden;
  private final boolean caseSensitive;
  private final boolean excludeDotFiles;

  public DefaultIBDirScannerSupplier(PathSupplier root,
      // Includes
      StringListSupplier includes,
      // Excludes
      StringListSupplier excludes,
      // Exclude dirs
      BooleanSupplier excludeDirectories,
      // Exclude hidden
      BooleanSupplier excludeHidden,
      // DOT files aren't PRECISELY the same as hidden sometimes
      BooleanSupplier excludeDotFiles, BooleanSupplier caseSensitive) {
    this.root = requireNonNull(root.get());
    this.includes = requireNonNull(includes.get());
    this.excludes = requireNonNull(excludes.get());
    this.excludeDirectories = requireNonNull(excludeDirectories.getAsBoolean());
    this.excludeHidden = requireNonNull(excludeHidden.getAsBoolean());
    this.excludeDotFiles = requireNonNull(excludeDotFiles.getAsBoolean());
    this.caseSensitive = requireNonNull(caseSensitive.getAsBoolean());
  }

  @Override
  public IBDirScanner get() {
    return new DefaultIBDirScanner(root, includes, excludes, excludeDirectories, excludeHidden, excludeDotFiles,
        caseSensitive);
  }

  public static class DefaultIBDirScanner implements IBDirScanner {
    public final static Function<List<String>, String[]> mapToArray = (i) -> {
      return i.toArray(new String[i.size()]);
    };
    private final Path srcDir;
    private final boolean excludeDirs;
    private final List<String> excl;
    private final List<String> incl;
    private final boolean caseSens;
    private final boolean excludeHidden;
    private final boolean excludeDotFiles;

    public DefaultIBDirScanner(
        // Root path for scanning
        Path p,
        // includes
        List<String> incl,
        // excludes
        List<String> excl,
        // exclude directories
        boolean exclDir,
        // exclude hidden
        boolean excludeHidden,
        // exclude dotfiles
        boolean excludeDotFiles,
        // case sensitive
        boolean caseSens) {
      this.srcDir = requireNonNull(p);
      this.incl = requireNonNull(incl);
      this.excl = requireNonNull(excl);
      this.excludeDirs = exclDir;
      this.excludeHidden = excludeHidden;
      this.excludeDotFiles = excludeDotFiles;
      this.caseSens = caseSens;
    }

    @Override
    public Map<Boolean, List<Path>> scan() {
      final Map<Boolean, List<Path>> ret = new HashMap<>();
      final DirectoryScanner ds = new DirectoryScanner();
      ds.setBasedir(srcDir.toFile());
      ds.setExcludes(mapToArray.apply(excl));
      ds.setIncludes(mapToArray.apply(incl));
      ds.setCaseSensitive(caseSens);
      ds.setFollowSymlinks(false); // We don't do symlinks in CF
      ds.scan();

      final String[] inclFiles = ds.getIncludedFiles();
      final String[] inclDirs = ds.getIncludedDirectories();
      final String[] exclFiles = ds.getExcludedFiles();
      final String[] exclDirs = ds.getExcludedDirectories();

      for (int i = 0; i < inclDirs.length; ++i) {
        if (!inclDirs[i].endsWith(File.separator))
          inclDirs[i] = inclDirs[i] + File.separator;
      }
      final TreeSet<String> inclSet = new TreeSet<>();
      inclSet.addAll(Arrays.asList(inclFiles));
      if (!excludeDirs)
        inclSet.addAll(Arrays.asList(inclDirs));
      final TreeSet<String> exclSet = new TreeSet<>();
      exclSet.addAll(Arrays.asList(exclFiles));
      if (!excludeDirs)
        exclSet.addAll(Arrays.asList(exclDirs));

      ret.put(true, filterDotfiles.apply(filterHidden.apply(mapToList.apply(srcDir, inclSet), this.excludeHidden),
          this.excludeDotFiles));
      ret.put(false, filterDotfiles.apply(filterHidden.apply(mapToList.apply(srcDir, exclSet), this.excludeHidden),
          this.excludeDotFiles));
      return ret;
    }

    public final static BiFunction<Path, SortedSet<String>, List<Path>> mapToList = (srcDir, orderedSet) -> {
      return orderedSet.stream()
          // Resolve as rooted by sourceDirectory
          .map(f -> srcDir.resolve(f))
          // Make it a URL
//          .map((p) -> cet.withReturningTranslation(() -> p.toUri().toURL().toExternalForm()))
          // Make it absolute
          .map(Path::toAbsolutePath)
          // Collect set
//          .collect(Collectors.toSet())
//          //
//          .stream()
//          //
//          .map(Paths::get)
          //
          .collect(Collectors.toList());

    };

    //    public final static BiFunction<List<Path>, Predicate<? super Path>, List<Path>> filterList = (paths, function) -> {
    //      return paths.stream().filter(function).collect(Collectors.toList());
    //    };

    public final static BiFunction<List<Path>, Boolean, List<Path>> filterHidden = (paths, exclHidden) -> {
      return requireNonNull(paths).stream()
          // If we're not filtering then pass OR if we're not hidden then pass
          .filter(path -> (!exclHidden) || (!IBException.cet.withReturningTranslation(() -> Files.isHidden(path))))
          .collect(Collectors.toList());
    };
    public final static BiFunction<List<Path>, Boolean, List<Path>> filterDotfiles = (paths, exclHidden) -> {
      return requireNonNull(paths).stream()
          // If we're not filtering then pass OR if we're not hidden then pass
          .filter(path -> (!exclHidden) || (!path.getFileName().startsWith(".")))
          // Filter dotfiles if necessary
          .collect(Collectors.toList());
    };
  }
}