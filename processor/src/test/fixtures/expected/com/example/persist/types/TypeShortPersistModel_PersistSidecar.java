package com.example.persist.types;

import arez.Arez;
import arez.Disposable;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.DepType;
import arez.annotations.Feature;
import arez.annotations.Observe;
import arez.annotations.PostConstruct;
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
abstract class TypeShortPersistModel_PersistSidecar {
  @Nonnull
  private final Scope _scope;

  @ComponentDependency
  @Nonnull
  final TypeShortPersistModel _peer;

  @Nonnull
  private final Store _appStore;

  TypeShortPersistModel_PersistSidecar(@Nonnull final Scope scope,
      @Nonnull final TypeShortPersistModel peer, @Nonnull final Store appStore) {
    _scope = Objects.requireNonNull( scope );
    _peer = Objects.requireNonNull( peer );
    _appStore = Objects.requireNonNull( appStore );
  }

  @Nonnull
  static TypeShortPersistModel_PersistSidecar attach(@Nonnull final Scope scope,
      @Nonnull final TypeShortPersistModel peer) {
    final Store appStore = ArezPersist.getStore( "app" );
    return new Arez_TypeShortPersistModel_PersistSidecar( scope, peer, appStore );
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

  @PostConstruct
  void postConstruct() {
    restoreState();
  }

  @Action(
      verifyRequired = false
  )
  void restoreState() {
    final String $ap$_id = getComponentId();
    if ( !_appStore.isDisposed() ) {
      final Map<String, Object> state = _appStore.get( _scope, Keys.TYPE, $ap$_id );
      if ( null != state ) {
      }
    }
  }

  @Action(
      mutation = false,
      verifyRequired = false
  )
  void persistState() {
    if ( !_appStore.isDisposed() ) {
      final Map<String, Object> state = new HashMap<>();
      final short $prop$_getValue = _peer.getValue();
      if ( 0 != $prop$_getValue ) {
        state.put( Keys.PROPERTY_getValue, $prop$_getValue );
      }
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state );
    }
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "TypeShortPersistModel" : TypeShortPersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_getValue = Arez.areNamesEnabled() ? "getValue" : "a";
  }
}
