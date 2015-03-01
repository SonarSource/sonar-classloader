# Toolbox for Java Classloaders

Sonar Classloader is a toolbox for creating Java 7+ classloaders. It's inspired from projects [Codehaus Classworlds][classworlds] and
[Plexus Classworlds][plexus].

This library is available under GNU LGPLv3.

## Maven Dependency

    <dependency>
      <groupId>org.codehaus.sonar-plugins</groupId>
      <artifactId>sonar-classloader</artifactId>
      <version>${classloader.version}</version>
    </dependency>

## Usage

#### Build classloader

Create a classloader based on system classloader.

```java
ClassloaderBuilder builder = new ClassloaderBuilder();
Map<String, ClassLoader> classloaders = builder
  .newClassloader("the-cl")
  .addURL("the-cl", jarFile)
  .addURL("the-cl", directory)
  .build();

// this classloader can load only JRE and the resources contained in jarFile and directory. 
ClassLoader c = classloaders.get("the-cl");
```

It's also possible to create a classloader based on another one:

```java
ClassloaderBuilder builder = new ClassloaderBuilder();
Map<String, ClassLoader> classloaders = builder
  .newClassloader("the-cl", otherClassloader)
  .addURL("the-cl", jarFile)
  .build();

// this classloader can load the resources of JRE, jarFile and otherClassloader. 
ClassLoader cl1 = classloaders.get("cl1");
```

#### Hierarchy of classloaders

```java
ClassloaderBuilder builder = new ClassloaderBuilder();
Map<String, ClassLoader> classloaders = builder
  .newClassloader("the-parent")
  .addURL("the-parent", parentJar)
  
  .newClassloader("the-child")
  .addURL("the-child", childJar)
  .setParent("the-child", "the-parent", new Mask())
  
  .newClassloader("the-grand-child")
  .setParent("the-grand-child", "the-child", new Mask())
  // can be parent-first or self-first ordering strategy. Default is parent-first.
  .setLoadingOrder("the-grand-child", LoadingOrder.SELF_FIRST)
  
  .build();
ClassLoader parent = classloaders.get("the-parent");
ClassLoader child = classloaders.get("the-child");
ClassLoader grandChild = classloaders.get("the-grand-child");
```

## License

    Copyright (C) 2015 SonarSource
    
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02

[classworlds]: http://classworlds.codehaus.org
[plexus]: https://github.com/sonatype/plexus-classworlds
