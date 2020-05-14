package arez.persist.runtime;

import grim.annotations.OmitSymbol;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import arez.persist.StoreTypes;
import static org.realityforge.braincheck.Guards.*;

/**
 * A utility class that contains references to stores and the root scope.
 * This is extracted to a separate class to eliminate the <clinit> from {@link arez.persist.runtime.ArezPersist} and thus
 * make it much easier for GWT to optimize out code based on build time compilation parameters.
 */
final class Registry
{
  @Nonnull
  private static final Map<String, arez.persist.runtime.PersistStore> c_stores = new HashMap<>();
  @Nonnull
  private static PersistScope c_rootScope = new PersistScope( null, PersistScope.DEFAULT_SCOPE_NAME );

  static
  {
    registerIntrinsicStores();
  }

  private Registry()
  {
  }

  @Nonnull
  static PersistScope getRootScope()
  {
    return c_rootScope;
  }

  static void disposeScope( @Nonnull final PersistScope scope )
  {
    releaseScope( scope );
    _disposeScope( scope );
  }

  static void releaseScope( @Nonnull final PersistScope scope )
  {
    c_stores.values().forEach( store -> store.releaseScope( scope ) );
  }

  private static void _disposeScope( @Nonnull final PersistScope scope )
  {
    scope.getNestedScopes().forEach( arez.persist.runtime.Registry::_disposeScope );
    scope.dispose();
  }

  static void registerPersistStore( @Nonnull final String name, @Nonnull final arez.persist.runtime.StorageService service )
  {
    if ( arez.persist.runtime.ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !c_stores.containsKey( name ),
                    () -> "registerPersistStore() invoked with name '" + name +
                          "' but a store is already registered with that name" );
    }
    final arez.persist.runtime.PersistStore store = new arez.persist.runtime.PersistStore( service );
    c_stores.put( name, store );
    store.restore();
  }

  @Nonnull
  static arez.persist.runtime.PersistStore getPersistStore( @Nonnull final String name )
  {
    if ( arez.persist.runtime.ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> c_stores.containsKey( name ),
                    () -> "getPersistStore() invoked with name " + name +
                          " but no such store exists" );
    }
    return Objects.requireNonNull( c_stores.get( name ) );
  }

  /**
   * cleanup service.
   * This is dangerous as it may leave dangling references and should only be done in tests.
   */
  @OmitSymbol
  static void reset()
  {
    c_stores.values().forEach( arez.persist.runtime.PersistStore::dispose );
    c_stores.clear();
    registerIntrinsicStores();
    disposeScope( c_rootScope );
    c_rootScope = new PersistScope( null, PersistScope.DEFAULT_SCOPE_NAME );
  }

  private static void registerIntrinsicStores()
  {
    if ( arez.persist.runtime.ArezPersist.isApplicationScopedPersistenceEnabled() )
    {
      registerPersistStore( StoreTypes.APPLICATION, new arez.persist.runtime.NoopStorageService() );
    }
  }
}
