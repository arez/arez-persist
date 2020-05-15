package arez.persist.runtime.browser;

import arez.persist.StoreTypes;
import arez.persist.runtime.ArezPersist;
import javax.annotation.Nonnull;

public final class ArezPersistBrowserUtil
{
  private ArezPersistBrowserUtil()
  {
  }

  /**
   * Register a store under the {@link StoreTypes#SESSION} name that saves state in a browsers session storage.
   *
   * @param persistenceKey the key under which the state is stored.
   */
  public static void registerSessionScopedStore( @Nonnull final String persistenceKey )
  {
    ArezPersist.registerStore( StoreTypes.SESSION, WebStorageService.createSessionStorageService( persistenceKey ) );
  }

  /**
   * Register a store under the {@link StoreTypes#LOCAL} name that saves state in a browsers local storage.
   *
   * @param persistenceKey the key under which the state is stored.
   */
  public static void registerLocalScopedStore( @Nonnull final String persistenceKey )
  {
    ArezPersist.registerStore( StoreTypes.LOCAL, WebStorageService.createLocalStorageService( persistenceKey ) );
  }
}
