/*
 * Sonar Classloader
 * Copyright (C) 2015-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.classloader;

import javax.annotation.CheckForNull;

import java.net.URL;
import java.util.Collection;

interface ClassloaderRef {

  /**
   * Does not throw {@link java.lang.ClassNotFoundException} but returns null
   * when class is not found
   * @param name name of class, for example "org.foo.Bar"
   */
  @CheckForNull
  Class loadClassIfPresent(String name);

  /**
   * Searches for a resource. Returns null if not found.
   * @param name name of resource, for example "org/foo/Bar.class" or "org/foo/config.xml"
   */
  @CheckForNull
  URL loadResourceIfPresent(String name);

  /**
   * Searches for all the occurrences of a resource from hierarchy of classloaders.
   * Results are appended to the parameter "appendTo". Order of resources is given by the
   * hierarchy order of classloaders.
   * @see #loadResourceIfPresent(String) for the format of resource name
   */
  void loadResources(String name, Collection<URL> appendTo);
}
