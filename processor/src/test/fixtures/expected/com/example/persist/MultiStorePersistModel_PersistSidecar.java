package com.example.persist;

import arez.Arez;
import arez.Disposable;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.DepType;
import arez.annotations.Feature;
import arez.annotations.Observe;
import arez.annotations.PreDispose;
import arez.annotations.Priority;
import arez.component.Identifiable;
import arez.persist.runtime.ArezPersist;
import arez.persist.runtime.Scope;
import arez.persist.runtime.Store;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@ArezComponent(
    disposeNotifier = Feature.DISABLE,
    requireId = Feature.DISABLE,
    requireEquals = Feature.DISABLE,
    observable = Feature.DISABLE
)
@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class MultiStorePersistModel_PersistSidecar {
  @Nonnull
  private final Scope _scope;

  @ComponentDependency
  @Nonnull
  final MultiStorePersistModel _peer;

  @Nonnull
  private final Store _aStore;

  @Nonnull
  private final Store _appStore;

  @Nonnull
  private final Store _bStore;

  MultiStorePersistModel_PersistSidecar(@Nonnull final Scope scope,
      @Nonnull final MultiStorePersistModel peer, @Nonnull final Store aStore,
      @Nonnull final Store appStore, @Nonnull final Store bStore) {
    _scope = Objects.requireNonNull( scope );
    _peer = Objects.requireNonNull( peer );
    _aStore = Objects.requireNonNull( aStore );
    _appStore = Objects.requireNonNull( appStore );
    _bStore = Objects.requireNonNull( bStore );
  }

  @Nonnull
  static MultiStorePersistModel_PersistSidecar attach(@Nonnull final Scope scope,
      @Nonnull final MultiStorePersistModel peer) {
    final Store aStore = ArezPersist.getStore( "a" );
    final Store appStore = ArezPersist.getStore( "app" );
    final Store bStore = ArezPersist.getStore( "b" );
    return new Arez_MultiStorePersistModel_PersistSidecar( scope, peer, aStore, appStore, bStore );
  }

  @Nonnull
  private String getComponentId() {
    return String.valueOf( Objects.requireNonNull( Identifiable.getArezId( _peer ) ) );
  }

  @Observe(
      priority = Priority.LOWEST,
      nestedActionsAllowed = true,
      depType = DepType.AREZ_OR_NONE
  )
  void savePersistentProperties() {
    persistState();
  }

  @PreDispose
  void preDispose() {
    if ( Disposable.isNotDisposed( _peer ) ) {
      persistState();
    }
  }

  @Action(
      mutation = false,
      verifyRequired = false
  )
  void persistState() {
    if ( !_aStore.isDisposed() ) {
      final Map<String, Object> state = new HashMap<>();
      _aStore.save( _scope, Keys.TYPE, getComponentId(), state );
    }
    if ( !_appStore.isDisposed() ) {
      final Map<String, Object> state = new HashMap<>();
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state );
    }
    if ( !_bStore.isDisposed() ) {
      final Map<String, Object> state = new HashMap<>();
      _bStore.save( _scope, Keys.TYPE, getComponentId(), state );
    }
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "MultiStorePersistModel" : MultiStorePersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_getValue = Arez.areNamesEnabled() ? "getValue" : "a";

    @Nonnull
    private static final String PROPERTY_getValue3 = Arez.areNamesEnabled() ? "getValue3" : "b";

    @Nonnull
    private static final String PROPERTY_getValue4 = Arez.areNamesEnabled() ? "getValue4" : "c";

    @Nonnull
    private static final String PROPERTY_getValue2 = Arez.areNamesEnabled() ? "getValue2" : "d";
  }
}
