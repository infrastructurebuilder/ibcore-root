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
package org.infrastructurebuilder.util.mavendownloadplugin;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.slf4j.Logger;

public interface WGetBuilder {

  Optional<WGetResult> wget();

  /** Should be set once **/
  WGetBuilder withLogger(Logger log);

  /** Must be set once **/
  WGetBuilder withArchiverManager(ArchiverManager archiverManager);

  /** The following params can be reset after calls to {@link WGetBuilder#build()} **/
  WGetBuilder withUri(URI uri);

  WGetBuilder withOverwrite(boolean overwrite);

  WGetBuilder withOutputFileName(String outputFileName);

  WGetBuilder withOutputDirectory(Path outputDirectory);

  WGetBuilder withMd5(String md5);

  WGetBuilder withSha1(String sha1);

  WGetBuilder withSha256(String sha256);

  WGetBuilder withSha512(String sha512);

  WGetBuilder withUnpack(boolean unpack);

  WGetBuilder withUsername(String username);

  WGetBuilder withPassword(String password);

  WGetBuilder withNtlmDomain(String ntlmDomain);

  WGetBuilder withNtlmHost(String ntlmHost);

  WGetBuilder withProxyHost(String proxyHost);

  WGetBuilder withProxyPort(Integer proxyPort);

  WGetBuilder withRetries(int retries);

  WGetBuilder withReadTimeOut(int readTimeOut);

  WGetBuilder withCacheDirectory(Path cacheDirectory);

  WGetBuilder withFailOnError(boolean failOnError);

  WGetBuilder withAlwaysVerifyChecksum(boolean alwaysVerifyChecksum);

  WGetBuilder withFollowRedirects(boolean followRedirects);

  WGetBuilder withSkipCache(boolean skipCache);

  WGetBuilder withHeaders(Map<String, String> headers);

  WGetBuilder withHeaders(Properties p);

  WGetBuilder withMaxLockWaitTime(long maxLockWaitTime);

  WGetBuilder withFileMappers(FileMapper[] fileMappers);

  WGetBuilder withPreemptiveAuth(boolean preemptiveAuth);

  WGetBuilder withInsecure(boolean insecure);

  WGetBuilder withProgressReport(ProgressReport progressReport);

}
