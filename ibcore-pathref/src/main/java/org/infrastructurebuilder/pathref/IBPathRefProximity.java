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
package org.infrastructurebuilder.pathref;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

/**
 * The proximity of a PathRef, in terms of how "fast" or "hard" it is to acquire the data that it points to. It is
 * advisory-only, with the distinction between what is DIRECT and what is LOCAL being somewhat arbitrary. Just because
 * something is NETWORKATTACHED doesn't mean that it will be materially slower to <b>your process</b>. That is a more
 * complex determination to make.
 *
 * Note that proximity manages live use of the data, not the single cost of retrieval. So something that is
 * NETWORKATTACHED but cached in-memory makes its proximity set to INMEMORY, even though it could take
 * significant time to initially retrieve.  Those values are managed by expected access speed and
 * initial retrieval speed in the PathRef.
 *
 * The scale is to allow for changes between the various speeds to remain manageable. If something comes along that fits
 * between LOCAL and NETWORKATTACHED it can be inserted into the enum and given a mid-point scale.
 *
 * Values are persisted at scale and retrieved using fromScale()
 *
 *
 * @see
 */
public enum IBPathRefProximity {
  /*
   * We have no idea about where this data is or comes from
   */
  UNKNOWN(-1),
  /*
   * In-memory pathrefs are abstractions that are effectively memory-local, such as an in-memory cache.
   */
  INMEMORY(0),
  /*
   * Direct means that the PathRef is as close to being "local-disk" as possible. It can also mean a faster disk-access
   * method than LOCAL
   */
  DIRECT(10),
  /*
   * LOCAL is considered the <i>slowest local access method</i>. This could mean the difference between SSD and spinning
   * disc hard drives, for instance.
   */
  LOCAL(100),
  /**
   * Network attached differentiates between a locally accessible disk and one that is on a remote filesystem, such as
   * NFS.
   */
  NETWORKATTACHED(1000),
  /**
   * This is data that requires connections to access, but can be done in a reasonable amount of time. For instance,
   * blob storage in a cloud provider when accessed directly from within teh provider.
   *
   * It is possible for REMOTE to be essentially as fast as NETWORKATTACHED.
   *
   * By convention, a FUSE-mounted S3 (or other blobstore) filesystem is considered NETWORKATTACHED, even though it is
   * more accurate to call it REMOTE
   */
  REMOTE(10000),
  /*
   * The data is difficult to retrieve, will probably have to be cached (at which point it may return a different
   * IBPathRefProximity, and use of it should be performed judiciously.
   */
  VERYREMOTE(100000),
  /*
   * LIMITED data will be hard to access and frequently inaccessible. If a source reports that it has limited proximity,
   * a user should have no expectation of being able to reliably retrieve it.
   */
  LIMITED(Integer.MAX_VALUE);

  public final static Comparator<IBPathRefProximity> comparator = new IBPathRefProximityComparator();

  public final static IBPathRefProximity fromScale(int scale) {
    var v = IBPathRefProximity.values();
    for (int i = v.length; i >= 0; i--) {
      if (scale >= v[i].getScale())
        return v[i];
    }
    return UNKNOWN;

  }

  private final int scale;

  IBPathRefProximity(int i) {
    this.scale = i;
  }

  public int getScale() {
    return scale;
  }

  private final static class IBPathRefProximityComparator implements Comparator<IBPathRefProximity> {

    @Override
    public int compare(IBPathRefProximity o1, IBPathRefProximity o2) {
      return requireNonNull(o1.getScale()).compareTo(requireNonNull(o2.getScale()));
    }

  }
}
