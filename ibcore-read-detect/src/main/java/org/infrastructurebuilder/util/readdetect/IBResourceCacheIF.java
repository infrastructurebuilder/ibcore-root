package org.infrastructurebuilder.util.readdetect;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public interface IBResourceCacheIF {

  /**
   * Declares to which version of descriptor this configuration conforms. This value is the api version (major.minor)
   * for transportable schemas and conforms to semantic versioning (Required)
   *
   */
  String getModelVersion();

  /**
   * A URL-like for the RelativeRoot of the cache. (Required)
   *
   */
  String getRoot();

  /**
   * Queryable name for the cache. (Required)
   *
   */
  String getName();

  /**
   * Description for the cache.
   *
   */
  Optional<String> getDescription();

  Optional<List<IBResourceModel>> getResources();

  Map<String, Object> getAdditionalProperties();

}
