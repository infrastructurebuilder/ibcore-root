/**
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
 */
package org.infrastructurebuilder;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IBConstants {
  public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
  public final static String TEXT_PLAIN = "text/plain";
  public static final String AVRO_BINARY = ".avro";
  public static final String APPLICATION_PDF = "application/pdf";
  public static final String TEXT_CSV = "text/csv";
  public static final String APPLICATION_ZIP = "application/zip";
  public static final String APPLICATION_XML = "application/xml";

  public static final String _SHA512 = "sha512";
  public static final String ASC_EXT = ".asc";

  public static final JSONArray CHECKSUM_TYPES_DEFAULT = new JSONArray(Arrays.asList(_SHA512));

  public static final Optional<String> CHECKSUM_TYPES_SHA512 = Optional.of(_SHA512);

  public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
      .withLocale(Locale.US).withZone(ZoneId.of("Z"));

  public static final String DIGEST_TYPE = "SHA-512";
  public static final String DIRECTORY_PERMISSIONS = "directoryPermissions";
  public static final String EXPORTED = "ExportedInsecureTestKeyrings";
  public static final String FACTORY_NAME = "org.infrastructurebuilder.core.config.CoreCryptoProviderFactory";
  public static final String FILE_PERMISSIONS = "filePermissions";
  public static final String FILESYSTEM_CRYPTO_CONFIGURATION = "Filesystem-Crypto-Configuration";
  public static final String GITHUB = "github";
  public static final String AZUREDEVOPS = "azuredevops";
  public static final String HEX_IDENTIFIER = "HEXID:";
  public static final String ID = "id";
  public static final String JSON_EXT = ".json";
  public static final String KEYSERVER_DEFAULT_HOST = "pgp.mit.edu";
  public static final String KEYSERVER_HOST_ENV = "SPECIFIC_KEYSERVER_HOST";
  public static final String MAVEN_MIRRORS = "MAVEN_MIRRORS";
  public static final String NAME = "name";
  public static final String NO_OP = "no-op";
  public static final String NULL_PASSPHRASE = "*NULL*";
  public static final String OF = "of";
  public static final String PASSPHRASE = "passphrase";
  public static final String PASSPHRASE_ENV = "SPECIFIC_PASSPHRASE";
  public static final String PASSWORD = "password";
  public static final String PASSWORD_TYPE = "PASSWORD";
  public static final String PRIVATE_KEY = "privateKey";
  public static final String RANDOM = "*RANDOM*";

  public static final String ROOTPATH = "com/infrastructurebuilder/test/keys";
  public static final String SHA512 = _SHA512;
  public static final String SOURCE_LEVEL = "sourceLevel";
  public static final String SYMMETRIC_PREFIX = "*#*";
  public static final String TXT_EXT = ".txt";
  public static final String UNENCRYPTED = "_unencrypted_";
  public static final String URL = "url";
  public static final String USERNAME = "username";

  public static final Charset UTF8 = Charset.forName("UTF-8");
  public static final String VERSION = "version";

  public final static String HTTP_PREFIX = "http://";
  public final static String HTTPS_PREFIX = "https://";
  public final static String FILE_PREFIX = "file:";
  public final static String ZIP_PREFIX = "zip:";

}
