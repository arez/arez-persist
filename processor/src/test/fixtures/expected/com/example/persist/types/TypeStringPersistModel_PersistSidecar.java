package com.example.persist.types;

import arez.Arez;
import arez.annotations.ComponentDependency;
import arez.persist.runtime.Scope;
import arez.persist.runtime.Store;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class TypeStringPersistModel_PersistSidecar {
  @Nonnull
  private final Scope _scope;

  @ComponentDependency
  @Nonnull
  final TypeStringPersistModel _peer;

  @Nonnull
  private final Store _appStore;

  TypeStringPersistModel_PersistSidecar(@Nonnull final Scope scope,
      @Nonnull final TypeStringPersistModel peer, @Nonnull final Store appStore) {
    _scope = Objects.requireNonNull( scope );
    _peer = Objects.requireNonNull( peer );
    _appStore = Objects.requireNonNull( appStore );
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "TypeStringPersistModel" : TypeStringPersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_getValue = Arez.areNamesEnabled() ? "getValue" : "a";
  }
}
