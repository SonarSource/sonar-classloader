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

interface StrategyContext {

  @CheckForNull
  Class loadClassFromSiblings(String name);

  @CheckForNull
  Class loadClassFromSelf(String name);

  @CheckForNull
  Class loadClassFromParent(String name);

  @CheckForNull
  URL loadResourceFromSiblings(String name);

  @CheckForNull
  URL loadResourceFromSelf(String name);

  @CheckForNull
  URL loadResourceFromParent(String name);

  void loadResourcesFromSiblings(String name, Collection<URL> appendTo);

  void loadResourcesFromSelf(String name, Collection<URL> appendTo);

  void loadResourcesFromParent(String name, Collection<URL> appendTo);

}
