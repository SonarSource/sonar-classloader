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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaskTest {

  Mask sut = new Mask();

  @Test
  public void no_filtering() throws Exception {
    assertThat(sut.acceptClass("org.sonar.Bar")).isTrue();
    assertThat(sut.acceptClass("Bar")).isTrue();
  }

  @Test
  public void class_inclusion() throws Exception {
    sut.addInclusion("org/sonar/Bar.class");
    assertThat(sut.acceptClass("org.sonar.Bar")).isTrue();
    assertThat(sut.acceptClass("org.sonar.qube.Bar")).isFalse();
    assertThat(sut.acceptClass("org.sonar.Foo")).isFalse();
    assertThat(sut.acceptClass("Bar")).isFalse();
  }

  @Test
  public void resource_inclusion() throws Exception {
    sut.addInclusion("org/sonar/Bar.class");
    assertThat(sut.acceptResource("org/sonar/Bar.class")).isTrue();
    assertThat(sut.acceptResource("org/sonar/qube/Bar.class")).isFalse();
    assertThat(sut.acceptResource("org/sonar/Foo.class")).isFalse();
    assertThat(sut.acceptResource("Bar.class")).isFalse();
  }

  @Test
  public void package_inclusion() throws Exception {
    sut.addInclusion("org/sonar/");
    assertThat(sut.acceptClass("Foo")).isFalse();
    assertThat(sut.acceptClass("org.sonar.Bar")).isTrue();
    assertThat(sut.acceptClass("org.sonarqube.Foo")).isFalse();
    assertThat(sut.acceptClass("org.sonar.qube.Foo")).isTrue();
    assertThat(sut.acceptClass("Bar")).isFalse();
  }

  @Test
  public void class_exclusion() throws Exception {
    sut.addExclusion("org/sonar/Bar.class");
    assertThat(sut.acceptClass("org.sonar.Bar")).isFalse();
    assertThat(sut.acceptClass("org.sonar.qube.Bar")).isTrue();
    assertThat(sut.acceptClass("org.sonar.Foo")).isTrue();
    assertThat(sut.acceptClass("Bar")).isTrue();
  }

  @Test
  public void package_exclusion() throws Exception {
    sut.addExclusion("org/sonar/");
    assertThat(sut.acceptClass("Foo")).isTrue();
    assertThat(sut.acceptClass("org.sonar.Bar")).isFalse();
    assertThat(sut.acceptClass("org.sonarqube.Foo")).isTrue();
    assertThat(sut.acceptClass("org.sonar.qube.Foo")).isFalse();
    assertThat(sut.acceptClass("Bar")).isTrue();
  }

  @Test
  public void exclusion_is_subset_of_inclusion() throws Exception {
    sut.addInclusion("org/sonar/");
    sut.addExclusion("org/sonar/qube/");
    assertThat(sut.acceptClass("org.sonar.Foo")).isTrue();
    assertThat(sut.acceptClass("org.sonar.Qube")).isTrue();
    assertThat(sut.acceptClass("org.sonar.qube.Foo")).isFalse();
  }

  @Test
  public void inclusion_is_subset_of_exclusion() throws Exception {
    sut.addExclusion("org/sonar/");
    sut.addInclusion("org/sonar/qube/");
    assertThat(sut.acceptClass("org.sonar.Foo")).isFalse();
    assertThat(sut.acceptClass("org.sonar.Qube")).isFalse();
    assertThat(sut.acceptClass("org.sonar.qube.Foo")).isFalse();
  }

  @Test
  public void merge_with_none() throws Exception {
    sut.addInclusion("org/foo/");
    sut.addExclusion("org/bar/");
    sut.merge(new Mask());

    assertThat(sut.getInclusions()).containsOnly("org/foo/");
    assertThat(sut.getExclusions()).containsOnly("org/bar/");
  }

  @Test
  public void merge_exclusions() throws Exception {
    sut.addExclusion("org/foo/");
    sut.merge(new Mask().addExclusion("bar/"));

    assertThat(sut.getExclusions()).containsOnly("org/foo/", "bar/");
  }

  @Test
  public void merge_disjoined_inclusions() throws Exception {
    sut.addInclusion("org/foo/");
    sut.merge(new Mask().addInclusion("org/bar/"));

    assertThat(sut.getInclusions()).isEmpty();
  }

  @Test
  public void merge_inclusions() throws Exception {
    sut.addInclusion("org/foo/");
    sut.addInclusion("org/bar/sub/");
    sut.merge(new Mask().addInclusion("org/foo/sub/").addInclusion("org/bar/"));

    assertThat(sut.getInclusions()).containsOnly("org/foo/sub/", "org/bar/sub/");
  }
}
