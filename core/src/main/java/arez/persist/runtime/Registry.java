package arez.persist.runtime;

import arez.persist.StoreTypes;
import grim.annotations.OmitSymbol;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import static org.realityforge.braincheck.Guards.*;

/**
 * A utility class that contains references to stores and the root scope.
 * This is extracted to a separate class to eliminate the <clinit> from {@link ArezPersist} and thus
 * make it much easier for GWT to optimize out code based on build time compilation parameters.
 */
final class Registry
{
  @Nonnull
  private static final Map<String, PersistStore> c_stores = new HashMap<>();
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
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> PersistScope.DEFAULT_SCOPE_NAME.equals( scope.getName() ),
                    () -> "disposeScope() invoked with the root scope" );
    }
    releaseScope( scope );
    _disposeScope( scope );
  }

  static void releaseScope( @Nonnull final PersistScope scope )
  {
    c_stores.values().forEach( store -> store.releaseScope( scope ) );
  }

  private static void _disposeScope( @Nonnull final PersistScope scope )
  {
    scope.getNestedScopes().forEach( Registry::_disposeScope );
    scope.dispose();
  }

  static void registerPersistStore( @Nonnull final String name, @Nonnull final StorageService service )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !c_stores.containsKey( name ),
                    () -> "registerPersistStore() invoked with name '" + name +
                          "' but a store is already registered with that name" );
    }
    final PersistStore store = new PersistStore( service );
    c_stores.put( name, store );
    try
    {
      store.restore();
    }
    catch ( final Throwable t )
    {
      ArezPersistLogger.getLogger().log( "Failed to restore state for store named '" + name + "'", t );
    }
  }

  @Nonnull
  static PersistStore getPersistStore( @Nonnull final String name )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
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
    c_stores.values().forEach( PersistStore::dispose );
    c_stores.clear();
    registerIntrinsicStores();
    disposeScope( c_rootScope );
    c_rootScope = new PersistScope( null, PersistScope.DEFAULT_SCOPE_NAME );
  }

  private static void registerIntrinsicStores()
  {
    if ( ArezPersist.isApplicationScopedPersistenceEnabled() )
    {
      registerPersistStore( StoreTypes.APPLICATION, new NoopStorageService() );
    }
  }
}
