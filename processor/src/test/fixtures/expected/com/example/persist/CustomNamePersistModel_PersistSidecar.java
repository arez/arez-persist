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
import arez.annotations.Priority;
import arez.component.Identifiable;
import arez.persist.runtime.ArezPersist;
import arez.persist.runtime.Converter;
import arez.persist.runtime.Scope;
import arez.persist.runtime.Store;
import arez.persist.runtime.TypeConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@ArezComponent(
    disposeNotifier = Feature.DISABLE,
    requireId = Feature.DISABLE
)
@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class CustomNamePersistModel_PersistSidecar {
  private static int c_nextTaskId;

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

  @Nonnull
  static CustomNamePersistModel_PersistSidecar attach(@Nonnull final Scope scope,
      @Nonnull final CustomNamePersistModel peer) {
    final Store appStore = ArezPersist.getStore( "app" );
    return new Arez_CustomNamePersistModel_PersistSidecar( scope, peer, appStore );
  }

  private static void maybeAttach(@Nonnull final Scope scope,
      @Nonnull final CustomNamePersistModel peer) {
    if ( Disposable.isNotDisposed( scope ) && Disposable.isNotDisposed( peer ) )  {
      attach( scope, peer );
    }
  }

  @Nonnull
  static void scheduleAttach(@Nonnull final Scope scope,
      @Nonnull final CustomNamePersistModel peer) {
    Arez.context().task( Arez.areNamesEnabled() ? "CustomNamePersistModel_PersistSidecar.attach." + ( ++c_nextTaskId ) : null, () -> maybeAttach( scope, peer ) );
  }

  @Nonnull
  private String getComponentId() {
    return String.valueOf( Objects.<Object>requireNonNull( Identifiable.getArezId( _peer ) ) );
  }

  @Observe(
      priority = Priority.LOWEST,
      nestedActionsAllowed = true,
      depType = DepType.AREZ_OR_NONE
  )
  void savePersistentProperties() {
    persistState();
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
    if ( !_appStore.isDisposed() && !_scope.isDisposed() ) {
      final Map<String, Object> state = _appStore.get( _scope, Keys.TYPE, $ap$_id, Converters.TYPE_CONVERTER );
      if ( null != state ) {
        final Integer $prop$_v = (Integer) state.get( Keys.PROPERTY_v );
        if ( null != $prop$_v ) {
          _peer.setV( $prop$_v );
        }
      }
    }
  }

  @Action(
      mutation = false,
      verifyRequired = false
  )
  void persistState() {
    if ( !_appStore.isDisposed() && !_scope.isDisposed() ) {
      final Map<String, Object> state = new HashMap<>();
      final int $prop$_v = _peer.getValue();
      if ( 0 != $prop$_v ) {
        state.put( Keys.PROPERTY_v, $prop$_v );
      }
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state, Converters.TYPE_CONVERTER );
    }
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "CustomNamePersistModel" : CustomNamePersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_v = Arez.areNamesEnabled() ? "v" : "a";
  }

  @SuppressWarnings({
      "unchecked",
      "rawtypes"
  })
  private static final class Converters {
    @Nonnull
    private static final Converter CONVERTER_int = ArezPersist.getConverter( int.class );

    @Nonnull
    private static final TypeConverter TYPE_CONVERTER = createTypeConverter();

    @Nonnull
    private static TypeConverter createTypeConverter() {
      final Map<String, Converter> converters = new HashMap<>();
      converters.put( "v", CONVERTER_int );
      return new TypeConverter( converters );
    }
  }
}
