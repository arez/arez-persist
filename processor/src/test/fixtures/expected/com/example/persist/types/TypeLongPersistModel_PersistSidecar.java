package com.example.persist.types;

import arez.Arez;
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
    requireId = Feature.DISABLE,
    observable = Feature.DISABLE
)
@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class TypeLongPersistModel_PersistSidecar {
  private static int c_nextTaskId;

  @Nonnull
  private final Scope _scope;

  @ComponentDependency
  @Nonnull
  final TypeLongPersistModel _peer;

  @Nonnull
  private final Store _appStore;

  TypeLongPersistModel_PersistSidecar(@Nonnull final Scope scope,
      @Nonnull final TypeLongPersistModel peer, @Nonnull final Store appStore) {
    _scope = Objects.requireNonNull( scope );
    _peer = Objects.requireNonNull( peer );
    _appStore = Objects.requireNonNull( appStore );
  }

  @Nonnull
  static TypeLongPersistModel_PersistSidecar attach(@Nonnull final Scope scope,
      @Nonnull final TypeLongPersistModel peer) {
    final Store appStore = ArezPersist.getStore( "app" );
    return new Arez_TypeLongPersistModel_PersistSidecar( scope, peer, appStore );
  }

  @Nonnull
  static void scheduleAttach(@Nonnull final Scope scope, @Nonnull final TypeLongPersistModel peer) {
    Arez.context().task( Arez.areNamesEnabled() ? "TypeLongPersistModel_PersistSidecar.attach." + ( ++c_nextTaskId ) : null, () -> attach( scope, peer ) );
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
    if ( !_appStore.isDisposed() ) {
      final Map<String, Object> state = _appStore.get( _scope, Keys.TYPE, $ap$_id, Converters.TYPE_CONVERTER );
      if ( null != state ) {
        final Long $prop$_value = (Long) state.get( Keys.PROPERTY_value );
        if ( null != $prop$_value ) {
          _peer.setValue( $prop$_value );
        }
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
      final long $prop$_value = _peer.getValue();
      if ( 0L != $prop$_value ) {
        state.put( Keys.PROPERTY_value, $prop$_value );
      }
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state, Converters.TYPE_CONVERTER );
    }
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "TypeLongPersistModel" : TypeLongPersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_value = Arez.areNamesEnabled() ? "value" : "a";
  }

  @SuppressWarnings({
      "unchecked",
      "rawtypes"
  })
  private static final class Converters {
    @Nonnull
    private static final Converter CONVERTER_long = ArezPersist.getConverter( long.class );

    @Nonnull
    private static final TypeConverter TYPE_CONVERTER = createTypeConverter();

    @Nonnull
    private static TypeConverter createTypeConverter() {
      final Map<String, Converter> converters = new HashMap<>();
      converters.put( "value", CONVERTER_long );
      return new TypeConverter( converters );
    }
  }
}
