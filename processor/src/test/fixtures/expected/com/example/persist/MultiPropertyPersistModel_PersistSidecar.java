package com.example.persist;

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
abstract class MultiPropertyPersistModel_PersistSidecar {
  private static int c_nextTaskId;

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
  static void scheduleAttach(@Nonnull final Scope scope,
      @Nonnull final MultiPropertyPersistModel peer) {
    Arez.context().task( Arez.areNamesEnabled() ? "MultiPropertyPersistModel_PersistSidecar.attach." + ( ++c_nextTaskId ) : null, () -> attach( scope, peer ) );
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
        final String $prop$_value2 = (String) state.get( Keys.PROPERTY_value2 );
        if ( null != $prop$_value2 ) {
          _peer.setValue2( $prop$_value2 );
        }
        final Double $prop$_value3 = (Double) state.get( Keys.PROPERTY_value3 );
        if ( null != $prop$_value3 ) {
          _peer.setValue3( $prop$_value3 );
        }
        final Integer $prop$_value = (Integer) state.get( Keys.PROPERTY_value );
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
      final String $prop$_value2 = _peer.getValue2();
      if ( null != $prop$_value2 ) {
        state.put( Keys.PROPERTY_value2, $prop$_value2 );
      }
      final Double $prop$_value3 = _peer.getValue3();
      if ( null != $prop$_value3 ) {
        state.put( Keys.PROPERTY_value3, $prop$_value3 );
      }
      final int $prop$_value = _peer.getValue();
      if ( 0 != $prop$_value ) {
        state.put( Keys.PROPERTY_value, $prop$_value );
      }
      _appStore.save( _scope, Keys.TYPE, getComponentId(), state, Converters.TYPE_CONVERTER );
    }
  }

  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "MultiPropertyPersistModel" : MultiPropertyPersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_value2 = Arez.areNamesEnabled() ? "value2" : "a";

    @Nonnull
    private static final String PROPERTY_value3 = Arez.areNamesEnabled() ? "value3" : "b";

    @Nonnull
    private static final String PROPERTY_value = Arez.areNamesEnabled() ? "value" : "c";
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

    @Nonnull
    private static final TypeConverter TYPE_CONVERTER = createTypeConverter();

    @Nonnull
    private static TypeConverter createTypeConverter() {
      final Map<String, Converter> converters = new HashMap<>();
      converters.put( "value2", CONVERTER_java__lang__String );
      converters.put( "value3", CONVERTER_java__lang__Double );
      converters.put( "value", CONVERTER_int );
      return new TypeConverter( converters );
    }
  }
}
