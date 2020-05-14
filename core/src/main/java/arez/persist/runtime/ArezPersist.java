package arez.persist.runtime;

import arez.persist.StoreTypes;
import grim.annotations.OmitClinit;
import grim.annotations.OmitSymbol;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provide an interface to register and access stores and access the root scope as well as global configuration settings.
 */
@OmitClinit
public final class ArezPersist
{
  private ArezPersist()
  {
  }

  /**
   * Return true if apiInvariants will be checked.
   *
   * @return true if apiInvariants will be checked.
   */
  @OmitSymbol
  public static boolean shouldCheckApiInvariants()
  {
    return ArezPersistConfig.shouldCheckApiInvariants();
  }

  /**
   * Return true if the in-memory "app" persistence store will be registered by the framework.
   *
   * @return true if the in-memory "app" persistence store will be registered by the framework.
   */
  @OmitSymbol
  public static boolean isApplicationScopedPersistenceEnabled()
  {
    return ArezPersistConfig.isApplicationScopedPersistenceEnabled();
  }

  @Nonnull
  public static PersistScope getRootScope()
  {
    return Registry.getRootScope();
  }

  public static void registerPersistStore( @Nonnull final String name, @Nonnull final StorageService service )
  {
    Registry.registerPersistStore( name, service );
  }

  public static void registerSessionScopedPersistStore( @Nonnull final String persistenceKey )
  {
    registerPersistStore( StoreTypes.SESSION, WebStorageService.createSessionStorageService( persistenceKey ) );
  }

  public static void registerLocalScopedPersistStore( @Nonnull final String persistenceKey )
  {
    registerPersistStore( StoreTypes.LOCAL, WebStorageService.createLocalStorageService( persistenceKey ) );
  }

  @Nonnull
  public static PersistStore getPersistStore( @Nonnull final String name )
  {
    return Registry.getPersistStore( name );
  }

  @Nullable
  public static PersistScope findScope( @Nonnull final String qualifiedName )
  {
    PersistScope scope = Registry.getRootScope();
    if ( PersistScope.DEFAULT_SCOPE_NAME.equals( qualifiedName ) )
    {
      return scope;
    }
    int start = 0;
    int end;
    while ( -1 != ( end = qualifiedName.indexOf( start, '.' ) ) )
    {
      scope = scope.findScope( qualifiedName.substring( start, end ) );
      if ( null == scope )
      {
        return null;
      }
      else
      {
        start = end + 1;
      }
    }
    return scope.findScope( qualifiedName.substring( start ) );
  }

  @Nonnull
  public static PersistScope findOrCreateScope( @Nonnull final String qualifiedName )
  {
    PersistScope scope = Registry.getRootScope();
    if ( PersistScope.DEFAULT_SCOPE_NAME.equals( qualifiedName ) )
    {
      return scope;
    }
    int start = 0;
    int end;
    while ( -1 != ( end = qualifiedName.indexOf( '.', start ) ) )
    {
      scope = scope.findOrCreateScope( qualifiedName.substring( start, end ) );
      start = end + 1;
    }
    return scope.findOrCreateScope( qualifiedName.substring( start ) );
  }

  public static void disposeScope( @Nonnull final PersistScope scope )
  {
    Registry.disposeScope( scope );
  }

  public static void releaseScope( @Nonnull final PersistScope scope )
  {
    Registry.releaseScope( scope );
  }
}
