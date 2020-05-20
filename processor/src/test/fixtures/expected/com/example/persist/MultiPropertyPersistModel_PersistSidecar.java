package com.example.persist;

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
import arez.persist.runtime.Converter;
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

  @Nonnull
  static MultiPropertyPersistModel_PersistSidecar attach(@Nonnull final Scope scope,
      @Nonnull final MultiPropertyPersistModel peer) {
    final Store appStore = ArezPersist.getStore( "app" );
    return new Arez_MultiPropertyPersistModel_PersistSidecar( scope, peer, appStore );
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
      final int $prop$_getValue = _peer.getValue();
      if ( 0 != $prop$_getValue ) {
        state.put( Keys.PROPERTY_getValue, $prop$_getValue );
      }
      final Double $prop$_getValue3 = _peer.getValue3();
      if ( null != $prop$_getValue3 ) {
        state.put( Keys.PROPERTY_getValue3, $prop$_getValue3 );
      }
      final String $prop$_getValue2 = _peer.getValue2();
      if ( null != $prop$_getValue2 ) {
        state.put( Keys.PROPERTY_getValue2, $prop$_getValue2 );
      }
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state );
    }
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

  @SuppressWarnings({
      "unchecked",
      "rawtypes"
  })
  private static final class Converters {
    @Nonnull
    private static final Converter CONVERTER_int = ArezPersist.getConverter( int.class );

    @Nonnull
    private static final Converter CONVERTER_java__lang__Double = ArezPersist.getConverter( Double.class );

    @Nonnull
    private static final Converter CONVERTER_java__lang__String = ArezPersist.getConverter( String.class );
  }
}
