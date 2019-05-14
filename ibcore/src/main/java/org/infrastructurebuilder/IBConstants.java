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
  String _ENCRYPTION_TYPE_NONE = "org.infrastructurebuilder.util.noop.NoopCryptoStreamConfig";
  String _NOOP_CRYPTO_TYPE = _ENCRYPTION_TYPE_NONE;
  String _SHA512 = "sha512";
  String ASC_EXT = ".asc";

  JSONArray CHECKSUM_TYPES_DEFAULT = new JSONArray(Arrays.asList(_SHA512));
  Optional<String> CHECKSUM_TYPES_SHA512 = Optional.of(_SHA512);
  String CRYPTO_API_VERSION = "Crypto-API-Version";
  String CRYPTO_CIPHER = "Cryto-Cipher";
  String CRYPTO_DEFAULT = "Crypto-Defaults";
  String CRYPTO_ENCODED_KEY = "Crypto-Encoded-Key";
  String CRYPTO_ENCRYPT_WITH_ALL_ENCRYPTION_KEYS = "Crypto-Encrypt-With-All-Encryption-Keys";
  String CRYPTO_ENCRYPTION_IDENTIFIER = "Crypto-Encryption-Identifier";
  String CRYPTO_ENCRYPTION_IDENTIFIERS = "Crypto-Encryption-Identifiers";
  String CRYPTO_EXTERNAL_KEYRINGS = "Crypto-External-Keyrings";
  String CRYPTO_ID = "Crypto-Id";
  String CRYPTO_KEY_IDS_TO_PASSWORDS = "Crypto-Key-Ids-To-Passwords";
  String CRYPTO_KEY_RING = "Crypto-KeyRing";
  String CRYPTO_KEY_RINGS = "Crypto-KeyRings";
  String CRYPTO_KEYIDS_FOR_ENCRYPTION = "Crypto-Key-Ids-For-Encryption";
  String CRYPTO_KEYPAIRS_FOR_DECRYPTION = "Crypto-Key-Pairs-For-Decryption";
  String CRYPTO_PAIRS = "Crypto-Pairs";
  String CRYPTO_PASSPHRASE = "Crypto-Passphrase";
  String CRYPTO_PRINCIPAL = "Crypto-Principal";
  String CRYPTO_PRIVATE_KEY = "Crypto-PrivateKey";

  String CRYPTO_PRIVATE_KEYS = "Crypto-Private-Keys";
  String CRYPTO_PUBLIC_KEY = "Crypto-PublicKey";

  String CRYPTO_PUBLIC_KEYS = "Crypto-Public-Keys";

  String CRYPTO_SECRET = "Crypto-Secret";

  String CRYPTO_SECRET_KEYPAIRS = "Crypto-Secret-Keypairs";
  String CRYPTO_SERVER_IDS = "Crypto-Server-Ids";

  String CRYPTO_TYPE = "Crypto-Type";
  String CRYPTO_TYPES = "Crypto-Types";
  String CRYTPO_ASYMMETRIC_KEYPAIR = "Crypto-Asymmetric-KeyPair";
  String D99C2A73 = "D99C2A73";

  DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").withLocale(Locale.US)
      .withZone(ZoneId.of("Z"));
  String DEFAULT_CRYPTO_CONFIGURATION_KEY = "defaultCryptoConfig";

  String DEFAULT_NOOP_CRYPTO_CONFIG = new JSONObject().put(CRYPTO_KEY_IDS_TO_PASSWORDS, new JSONObject())

      .put(CRYPTO_TYPES, new JSONArray().put(_NOOP_CRYPTO_TYPE))

      .put(_NOOP_CRYPTO_TYPE, new JSONObject())

      .toString(2);
  String DIGEST_TYPE = "SHA-512";
  String DIRECTORY_PERMISSIONS = "directoryPermissions";
  String ENCRYPTION_TYPE_NONE = _ENCRYPTION_TYPE_NONE;
  String EXPORTED = "ExportedInsecureTestKeyrings";
  String FACTORY_NAME = "org.infrastructurebuilder.core.config.CoreCryptoProviderFactory";
  String FILE_PERMISSIONS = "filePermissions";
  String FILESYSTEM_CRYPTO_CONFIGURATION = "Filesystem-Crypto-Configuration";
  String HEX_IDENTIFIER = "HEXID:";
  String ID = "id";
  String JSON_EXT = ".json";
  String KEYSERVER_DEFAULT_HOST = "pgp.mit.edu";
  String KEYSERVER_HOST_ENV = "SPECIFIC_KEYSERVER_HOST";
  String MAVEN_MIRRORS = "MAVEN_MIRRORS";
  String NAME = "name";
  String NO_OP = "no-op";
  String NOOP_CRYPTO_TYPE = _NOOP_CRYPTO_TYPE;
  String NULL_PASSPHRASE = "*NULL*";
  String OF = "of";
  String PASSPHRASE = "passphrase";
  String PASSPHRASE_ENV = "SPECIFIC_PASSPHRASE";
  String PASSWORD = "password";
  String PASSWORD_ENCRYPTION_TYPE = "org.infrastructurebuilder.util.crypto.symkey.PasswordCryptoStreamSubconfig";
  String PASSWORD_TYPE = "PASSWORD";
  String PGP_DS_TYPE = "org.infrastructurebuilder.util.pgp.PGPDSCryptoStreamSubconfig";
  String PGP_ENCRYPTION_TYPE = PGP_DS_TYPE;
  String PRIVATE_KEY = "privateKey";
  String RANDOM = "*RANDOM*";

  String ROOTPATH = "com/infrastructurebuilder/test/keys";
  String SHA512 = _SHA512;
  String SOURCE_LEVEL = "sourceLevel";
  String SYMMETRIC_PREFIX = "*#*";
  String TXT_EXT = ".txt";
  String UNENCRYPTED = "_unencrypted_";
  String URL = "url";
  String USERNAME = "username";
  Charset UTF8 = Charset.forName("UTF-8");
  String VERSION = "version";
  String X101155F2 = "101155F2";
  String X101155F2_PUBLIC_SUBKEY = "B4A231B1";
  String X400FEB21 = "400FEB21";

  String X7FD737B2 = "7FD737B2";
  String X7FD737B2_PUBLIC_SUBKEY = "E48A21FD";

}
