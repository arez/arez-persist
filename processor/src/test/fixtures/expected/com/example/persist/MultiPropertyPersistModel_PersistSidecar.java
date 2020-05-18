package com.example.persist;

import arez.Arez;
import arez.annotations.ComponentDependency;
import arez.persist.runtime.Scope;
import arez.persist.runtime.Store;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class MultiPropertyPersistModel_PersistSidecar {
  @Nonnull
  private final Scope _scope;

  @ComponentDependency
  @Nonnull
  final MultiPropertyPersistModel _peer;

  @Nonnull
  private final Store _appStore;

  MultiPropertyPersistModel_PersistSidecar(@Nonnull final Scope scope,
      @Nonnull final MultiPropertyPersistModel peer, @Nonnull final Store appStore) {
    _scope = Objects.requireNonNull( scope );
    _peer = Objects.requireNonNull( peer );
    _appStore = Objects.requireNonNull( appStore );
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "MultiPropertyPersistModel" : MultiPropertyPersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_getValue = Arez.areNamesEnabled() ? "getValue" : "a";

    @Nonnull
    private static final String PROPERTY_getValue3 = Arez.areNamesEnabled() ? "getValue3" : "b";

    @Nonnull
    private static final String PROPERTY_getValue2 = Arez.areNamesEnabled() ? "getValue2" : "c";
  }
}
