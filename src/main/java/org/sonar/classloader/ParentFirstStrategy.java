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

import java.net.URL;
import java.util.Collection;

class ParentFirstStrategy implements Strategy {
  static final Strategy INSTANCE = new ParentFirstStrategy();

  private ParentFirstStrategy() {
  }

  @Override
  public Class loadClass(StrategyContext context, String name) throws ClassNotFoundException {
    Class clazz = context.loadClassFromSiblings(name);
    if (clazz == null) {
      clazz = context.loadClassFromParent(name);
      if (clazz == null) {
        clazz = context.loadClassFromSelf(name);
        if (clazz == null) {
          throw new ClassNotFoundException(name);
        }
      }
    }
    return clazz;
  }

  @Override
  public URL getResource(StrategyContext context, String name) {
    URL url = context.loadResourceFromSiblings(name);
    if (url == null) {
      url = context.loadResourceFromParent(name);
      if (url == null) {
        url = context.loadResourceFromSelf(name);
      }
    }
    return url;
  }

  @Override
  public void getResources(StrategyContext context, String name, Collection<URL> appendTo) {
    context.loadResourcesFromSiblings(name, appendTo);
    context.loadResourcesFromParent(name, appendTo);
    context.loadResourcesFromSelf(name, appendTo);
  }
}
