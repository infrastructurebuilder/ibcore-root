package org.infrastructurebuilder.util.readdetect;

import java.nio.file.Path;

import org.infrastructurebuilder.util.core.RelativeRoot;

public class DefaultIBResourceRelativeRootSupplier extends AbstractIBResourceRelativeRootSupplier {

  private final RelativeRoot rr;

  public DefaultIBResourceRelativeRootSupplier(Path p) {
    this.rr = RelativeRoot.from(p);
  }

  @Override
  public RelativeRoot get() {
    // TODO Auto-generated method stub
    return rr;
  }

}
