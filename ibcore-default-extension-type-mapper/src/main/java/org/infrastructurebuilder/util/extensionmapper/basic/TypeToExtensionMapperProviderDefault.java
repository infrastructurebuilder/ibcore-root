package org.infrastructurebuilder.util.extensionmapper.basic;

import java.util.Map;

import org.infrastructurebuilder.pathref.fs.TypeToExtensionMapper;
import org.infrastructurebuilder.pathref.fs.TypeToExtensionMapperProvider;

public class TypeToExtensionMapperProviderDefault implements TypeToExtensionMapperProvider {

  @Override
  public <T extends TypeToExtensionMapper> T create(String name, Map<String, ?> config) {
    return (T) new DefaultTypeToExtensionMapper();
  }

}
