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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

class DefaultClassloaderRef implements ClassloaderRef {
  private final Mask mask;
  private final ClassLoader classloader;

  DefaultClassloaderRef(ClassLoader classloader, Mask mask) {
    this.classloader = classloader;
    this.mask = mask;
  }

  @Override
  public Class loadClassIfPresent(String classname) {
    if (mask.acceptClass(classname)) {
      try {
        return classloader.loadClass(classname);
      } catch (ClassNotFoundException ignored) {
        // excepted behavior. Return null if class does not exist in this classloader
      }
    }
    return null;
  }

  @Override
  public URL loadResourceIfPresent(String name) {
    if (mask.acceptResource(name)) {
      return classloader.getResource(name);
    }
    return null;
  }

  @Override
  public void loadResources(String name, Collection<URL> appendTo) {
    if (mask.acceptResource(name)) {
      try {
        Enumeration<URL> resources = classloader.getResources(name);
        while (resources.hasMoreElements()) {
          appendTo.add(resources.nextElement());
        }
      } catch (IOException e) {
        throw new IllegalStateException(String.format("Fail to load resources named '%s'", name), e);
      }
    }
  }
}
