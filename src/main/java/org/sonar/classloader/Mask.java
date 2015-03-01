/*
 * Sonar Classloader
 * Copyright (C) 2015 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.classloader;

import java.util.ArrayList;
import java.util.List;

/**
 * A mask restricts access of a classloader to resources through inclusion and exclusion patterns.
 * By default all resources/classes are visible.
 * <p/>
 * Format of inclusion/exclusion patterns is the file path separated by slashes, for example
 * "org/foo/Bar.class" or "org/foo/config.xml". Wildcard patterns are not supported. Directories must end with
 * slash, for example "org/foo/" for excluding package org.foo and its sub-packages. Add the
 * exclusion "/" to exclude everything.
 *
 * @since 0.1
 */
public class Mask {

  private static final String ROOT = "/";
  private final List<String> inclusions = new ArrayList<>();
  private final List<String> exclusions = new ArrayList<>();

  List<String> getInclusions() {
    return inclusions;
  }

  List<String> getExclusions() {
    return exclusions;
  }

  public Mask addInclusion(String s) {
    inclusions.add(s);
    return this;
  }

  public Mask addExclusion(String s) {
    exclusions.add(s);
    return this;
  }

  boolean acceptClass(String classname) {
    if (inclusions.isEmpty() && exclusions.isEmpty()) {
      return true;
    }
    return acceptResource(classToResource(classname));
  }

  boolean acceptResource(String name) {
    boolean ok = true;
    if (!inclusions.isEmpty()) {
      ok = false;
      for (String include : inclusions) {
        if (include.equals(ROOT) || (include.endsWith("/") && name.startsWith(include)) || include.equals(name)) {
          ok = true;
          break;
        }
      }
    }
    if (ok) {
      for (String exclude : exclusions) {
        if (exclude.equals(ROOT) || (exclude.endsWith("/") && name.startsWith(exclude)) || exclude.equals(name)) {
          ok = false;
          break;
        }
      }
    }
    return ok;
  }

  void merge(Mask with) {
    List<String> lowestIncludes = new ArrayList<>();

    if (inclusions.isEmpty()) {
      lowestIncludes.addAll(with.inclusions);
    } else if (with.inclusions.isEmpty()) {
      lowestIncludes.addAll(inclusions);
    } else {
      for (String include : inclusions) {
        for (String fromInclude : with.inclusions) {
          if (fromInclude.startsWith(include)) {
            lowestIncludes.add(fromInclude);
          }
        }
      }
      for (String fromInclude : with.inclusions) {
        for (String include : inclusions) {
          if (include.startsWith(fromInclude)) {
            lowestIncludes.add(include);
          }
        }
      }
    }
    inclusions.clear();
    inclusions.addAll(lowestIncludes);
    exclusions.addAll(with.exclusions);
  }

  private String classToResource(String classname) {
    return classname.replace('.', '/') + ".class";
  }
}
