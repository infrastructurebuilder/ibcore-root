package org.infrastructurebuilder.data.line;

import java.util.function.Supplier;

public interface IBDataLineTransformerSupplier<I, O> extends Supplier<IBDataLineTransformer<I,O>> {

}
