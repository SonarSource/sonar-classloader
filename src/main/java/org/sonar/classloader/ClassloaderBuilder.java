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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassloaderBuilder {

  public static enum LoadingOrder {
    PARENT_FIRST(ParentFirstStrategy.INSTANCE),
    SELF_FIRST(SelfFirstStrategy.INSTANCE);

    private final Strategy strategy;

    private LoadingOrder(Strategy strategy) {
      this.strategy = strategy;
    }
  }

  /**
   * Wrapper of {@link ClassRealm} as long as associations are not fully
   * defined
   */
  private static class NewRealm {
    private final ClassRealm realm;

    private Mask exportMask;

    // key of the optional parent classloader
    private String parentKey;

    private final List<String> siblingKeys = new ArrayList<>();
    private final Map<String, Mask> associatedMasks = new HashMap<>();

    private NewRealm(ClassRealm realm) {
      this.realm = realm;
    }
  }

  private final Map<String, NewRealm> newRealmsByKey = new HashMap<>();

  public ClassloaderBuilder newClassloader(String key) {
    return newClassloader(key, getSystemClassloader());
  }

  public ClassloaderBuilder newClassloader(String key, ClassLoader baseClassloader) {
    if (newRealmsByKey.containsKey(key)) {
      throw new IllegalStateException(String.format("The classloader '%s' already exists. Can not create it twice.", key));
    }
    newRealmsByKey.put(key, new NewRealm(new ClassRealm(key, baseClassloader)));
    return this;
  }

  public ClassloaderBuilder setParent(String key, String parentKey, Mask mask) {
    NewRealm newRealm = getOrFail(key);
    newRealm.parentKey = parentKey;
    newRealm.associatedMasks.put(parentKey, mask);
    return this;
  }

  public ClassloaderBuilder setParent(String key, ClassLoader parent, Mask mask) {
    NewRealm newRealm = getOrFail(key);
    newRealm.realm.setParent(new DefaultClassloaderRef(parent, mask));
    return this;
  }

  public ClassloaderBuilder addSibling(String key, String siblingKey, Mask mask) {
    NewRealm newRealm = getOrFail(key);
    newRealm.siblingKeys.add(siblingKey);
    newRealm.associatedMasks.put(siblingKey, mask);
    return this;
  }

  public ClassloaderBuilder addSibling(String key, ClassLoader sibling, Mask mask) {
    NewRealm newRealm = getOrFail(key);
    newRealm.realm.addSibling(new DefaultClassloaderRef(sibling, mask));
    return this;
  }

  public ClassloaderBuilder addURL(String key, URL url) {
    getOrFail(key).realm.addConstituent(url);
    return this;
  }

  public ClassloaderBuilder setMask(String key, Mask mask) {
    getOrFail(key).realm.setMask(mask);
    return this;
  }

  public ClassloaderBuilder setExportMask(String key, Mask mask) {
    getOrFail(key).exportMask = mask;
    return this;
  }

  public ClassloaderBuilder setLoadingOrder(String key, LoadingOrder order) {
    getOrFail(key).realm.setStrategy(order.strategy);
    return this;
  }

  /**
   * Returns the new classloaders, grouped by keys. The parent and sibling classloaders
   * that are already existed (see {@link #setParent(String, ClassLoader, Mask)}
   * and {@link #addSibling(String, ClassLoader, Mask)} are not included into result.
   */
  public Map<String, ClassLoader> build() {
    Map<String, ClassLoader> result = new HashMap<>();

    // all the classloaders are registered and created. Associations can be resolved.
    for (Map.Entry<String, NewRealm> entry : newRealmsByKey.entrySet()) {
      NewRealm newRealm = entry.getValue();
      if (newRealm.parentKey != null) {
        NewRealm parent = getOrFail(newRealm.parentKey);
        Mask parentMask = newRealm.associatedMasks.get(newRealm.parentKey);
        mergeWithExportMask(parentMask, newRealm.parentKey);
        newRealm.realm.setParent(new DefaultClassloaderRef(parent.realm, parentMask));
      }
      for (String siblingKey : newRealm.siblingKeys) {
        NewRealm sibling = getOrFail(siblingKey);
        Mask siblingMask = newRealm.associatedMasks.get(siblingKey);
        mergeWithExportMask(siblingMask, siblingKey);
        newRealm.realm.addSibling(new DefaultClassloaderRef(sibling.realm, siblingMask));
      }
      result.put(newRealm.realm.getKey(), newRealm.realm);
    }
    return result;
  }

  private void mergeWithExportMask(Mask mask, String exportKey) {
    NewRealm newRealm = newRealmsByKey.get(exportKey);
    if (newRealm != null && newRealm.exportMask != null) {
      mask.merge(newRealm.exportMask);
    }
  }

  private NewRealm getOrFail(String key) {
    NewRealm newRealm = newRealmsByKey.get(key);
    if (newRealm == null) {
      throw new IllegalStateException(String.format("The classloader '%s' does not exist", key));
    }
    return newRealm;
  }

  private ClassLoader getSystemClassloader() {
    // on Oracle JVM :
    // - ClassLoader.getSystemClassLoader() is sun.misc.Launcher$AppClassLoader. It contains app classpath.
    // - ClassLoader.getSystemClassLoader().getParent() is sun.misc.Launcher$ExtClassLoader. It contains core JVM
    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    ClassLoader systemParent = systemClassLoader.getParent();
    if (systemParent != null) {
      systemClassLoader = systemParent;
    }
    return systemClassLoader;
  }
}
