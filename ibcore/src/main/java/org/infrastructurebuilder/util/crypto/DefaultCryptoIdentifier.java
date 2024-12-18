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
package org.infrastructurebuilder.util.crypto;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;

import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.JSONBuilder;
import org.json.JSONObject;

public class DefaultCryptoIdentifier implements CryptoIdentifier {

  private static final String IDENTIFIERS = "identifiers";
  private static final String VALIDATION_IDENTIFIERS = "validation-identifiers";
  private static final String TYPE = "type";
  private final String type;
  private final String validationIdentifier;
  private final String id;
  private final SortedSet<String> identifiers;
  private final ChecksumBuilder checksumBuilder = ChecksumBuilderFactory.newInstance();

  public DefaultCryptoIdentifier(String type, String id, SortedSet<String> identifiers,
      Optional<String> validationIdentifier)
  {
    this.type = requireNonNull(type);
    this.identifiers = requireNonNull(identifiers);
    this.validationIdentifier = requireNonNull(validationIdentifier).orElse(null);
    this.id = Optional.ofNullable(id).orElse(UUID.randomUUID().toString());
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance().addString("id", getId())
        // type
        .addString(TYPE, getType())
        // identifiers
        .addString(VALIDATION_IDENTIFIERS, getValidationIdentifier())
        //
        .addSetString(IDENTIFIERS, getIdentifiers())
        //
        .asJSON();
  }

//  @Override
//  public Checksum asChecksum() {
//    return ChecksumBuilderImpl.newInstance()
//        // ID
//        .addString(getId())
//        // Type
//        .addString(getType())
//        //
//        .addSetString(getIdentifiers())
//        // opt
//        .addString(getValidationIdentifier())
//        //
//        .asChecksum();
//  }
//
  @Override
  public SortedSet<String> getIdentifiers() {
    return this.identifiers;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getType() {
    return this.type;
  }

  @Override
  public Optional<String> getValidationIdentifier() {
    return Optional.ofNullable(this.validationIdentifier);
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return this.checksumBuilder //
        .addString(getType()) //
        .addString(getValidationIdentifier()) //
        .addSetString(getIdentifiers());
  }

}
