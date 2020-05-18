package arez.persist.runtime;

import arez.SafeProcedure;
import grim.annotations.OmitSymbol;
import java.util.ArrayList;
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
  /**
   * The map of registered stores.
   * There is no support for removing registrations at this time.
   */
  @Nonnull
  private static final Map<String, Store> c_stores = new HashMap<>();
  /**
   * The root scope.
   */
  @Nonnull
  private static Scope c_rootScope = new Scope( null, Scope.ROOT_SCOPE_NAME );

  static
  {
    // register "app" store if enabled.
    ArezPersist.registerApplicationStoreIfEnabled();
  }

  private Registry()
  {
  }

  /**
   * Return the root scope under which all other scopes are nested.
   *
   * @return the root scope under which all other scopes are nested.
   */
  @Nonnull
  static Scope getRootScope()
  {
    return c_rootScope;
  }

  /**
   * Dispose the specified scope.
   * A dispose operation first performs a {@link #releaseScope(Scope)} on the scope, then attempts to
   * dispose all nested scopes and finally disposes the specified scope. A disposed scope should no longer be
   * used to store state. It is an error to attempt to dispose the root scope.
   *
   * @param scope the scope to dispose.
   */
  static void disposeScope( @Nonnull final Scope scope )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !Scope.ROOT_SCOPE_NAME.equals( scope.getName() ),
                    () -> "disposeScope() invoked with the root scope" );
      apiInvariant( () -> !scope.isDisposed(),
                    () -> "disposeScope() passed a disposed scope named '" + scope.getName() + "'" );
    }
    releaseScope( scope );
    _disposeScope( scope );
  }

  /**
   * Release the specified scope.
   * A release operation removes any state associated with the scope and any nested scope.
   *
   * @param scope the scope to release.
   */
  static void releaseScope( @Nonnull final Scope scope )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !scope.isDisposed(),
                    () -> "releaseScope() passed a disposed scope named '" + scope.getName() + "'" );
    }
    c_stores.values().forEach( store -> store.releaseScope( scope ) );
  }

  private static void _disposeScope( @Nonnull final Scope scope )
  {
    new ArrayList<>( scope.getNestedScopes() ).forEach( Registry::_disposeScope );
    scope.dispose();
  }

  /**
   * Register a store with specified name and storage service.
   * It is an error to register multiple stores with the same name.
   *
   * <p>As part of the register operation, the store will attempt to restore state from the storage service.
   * If an error occurs during the restore, then the error will be logged and registration will complete.</p>
   *
   * @param name    the name of the store.
   * @param service the associated StorageService.
   * @return the action to invoke to deregister service.
   */
  static SafeProcedure registerStore( @Nonnull final String name, @Nonnull final StorageService service )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !c_stores.containsKey( name ),
                    () -> "registerStore() invoked with name '" + name +
                          "' but a store is already registered with that name" );
    }
    final Store store = new Store( service );
    c_stores.put( name, store );
    try
    {
      store.restore();
    }
    catch ( final Throwable t )
    {
      LogUtil.getLogger().log( "Failed to restore state for store named '" + name + "'", t );
    }
    return () -> deregisterStore( name );
  }

  /**
   * Deregister a store with the specified name.
   *
   * @param name the name of the store.
   */
  private static void deregisterStore( @Nonnull final String name )
  {
    final Store store = c_stores.remove( name );
    if ( null != store )
    {
      assert !store.isDisposed();
      try
      {
        store.dispose();
      }
      catch ( final Throwable t )
      {
        LogUtil.getLogger().log( "Failed to dispose store named '" + name + "'", t );
      }
    }
  }

  /**
   * Return the store that is registered with the specified name.
   * It is an error to invoke this method without registering a store under this name.
   *
   * @param name the name of the store.
   * @return the store.
   */
  @Nonnull
  static Store getStore( @Nonnull final String name )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> c_stores.containsKey( name ),
                    () -> "getStore() invoked with name " + name + " but no such store exists" );
    }
    return Objects.requireNonNull( c_stores.get( name ) );
  }

  @Nonnull
  static Map<String, Store> getStores()
  {
    return c_stores;
  }

  /**
   * cleanup service.
   * This is dangerous as it may leave dangling references and should only be done in tests.
   */
  @OmitSymbol
  static void reset()
  {
    c_stores.values().forEach( Store::dispose );
    c_stores.clear();
    ArezPersist.registerApplicationStoreIfEnabled();
    releaseScope( c_rootScope );
    c_rootScope = new Scope( null, Scope.ROOT_SCOPE_NAME );
  }
}
