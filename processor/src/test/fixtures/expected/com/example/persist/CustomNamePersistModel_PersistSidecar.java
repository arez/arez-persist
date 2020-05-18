package com.example.persist;

import arez.Arez;
import arez.ArezContext;
import arez.annotations.ComponentDependency;
import arez.annotations.ContextRef;
import arez.component.Identifiable;
import arez.persist.runtime.Scope;
import arez.persist.runtime.Store;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class CustomNamePersistModel_PersistSidecar {
  @Nonnull
  private final Scope _scope;

  @ComponentDependency
  @Nonnull
  final CustomNamePersistModel _peer;

  @Nonnull
  private final Store _appStore;

  CustomNamePersistModel_PersistSidecar(@Nonnull final Scope scope,
      @Nonnull final CustomNamePersistModel peer, @Nonnull final Store appStore) {
    _scope = Objects.requireNonNull( scope );
    _peer = Objects.requireNonNull( peer );
    _appStore = Objects.requireNonNull( appStore );
  }

  @ContextRef
  abstract ArezContext context();

  @Nonnull
  private String getComponentId() {
    return String.valueOf( Objects.requireNonNull( Identifiable.getArezId( _peer ) ) );
  }

  private void persistState() {
    if ( !_appStore.isDisposed() ) {
      final Map<String, Object> state = new HashMap<>();
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state );
    }
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "CustomNamePersistModel" : CustomNamePersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_v = Arez.areNamesEnabled() ? "v" : "a";
  }
}
