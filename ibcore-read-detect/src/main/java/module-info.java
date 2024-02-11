/*
 * @formatter:off
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
 * @formatter:on
 */
module org.infrastructurebuilder.util.readdetect {
  exports org.infrastructurebuilder.util.readdetect;
  requires transitive org.infrastructurebuilder.util.core;
  requires transitive org.infrastructurebuilder.util.readdetect.model;
  requires org.apache.commons.codec;
  requires javax.inject;
  requires org.apache.httpcomponents.httpcore;
  requires org.apache.httpcomponents.httpclient;
  requires org.apache.httpcomponents.httpclient.cache;
  requires plexus.utils;
  requires wagon.provider.api;
  requires org.apache.maven.resolver;
//  requires plexus.io;
  requires org.apache.maven.resolver.util;
  requires s3fs.nio;
  requires transitive org.codehaus.plexus.archiver.usurped;
  requires transitive org.apache.tika.core;
  requires transitive com.fasterxml.jackson.databind;
  requires transitive com.fasterxml.jackson.datatype.jsr310;
  requires org.infrastructurebuilder.util.config;
  requires io.avaje.jsr305x;
  requires transitive plexus.io;
  requires org.slf4j;
  requires com.fasterxml.jackson.datatype.jdk8;
}