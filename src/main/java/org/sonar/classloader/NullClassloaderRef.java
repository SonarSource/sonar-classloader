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

public class NullClassloaderRef implements ClassloaderRef {

  public static final NullClassloaderRef INSTANCE = new NullClassloaderRef();

  private NullClassloaderRef() {
  }

  @Override
  public Class loadClassIfPresent(String classname) {
    return null;
  }

  @Override
  public URL loadResourceIfPresent(String name) {
    return null;
  }

  @Override
  public void loadResources(String name, Collection<URL> appendTo) {
    // do nothing
  }
}
