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
   * Register a PersistStore under the {@link StoreTypes#SESSION} name that stores state in a browsers session storage.
   *
   * @param persistenceKey the key under which the state is stored.
   */
  public static void registerSessionScopedPersistStore( @Nonnull final String persistenceKey )
  {
    ArezPersist.registerPersistStore( StoreTypes.SESSION, WebStorageService.createSessionStorageService( persistenceKey ) );
  }

  /**
   * Register a PersistStore under the {@link StoreTypes#LOCAL} name that stores state in a browsers local storage.
   *
   * @param persistenceKey the key under which the state is stored.
   */
  public static void registerLocalScopedPersistStore( @Nonnull final String persistenceKey )
  {
    ArezPersist.registerPersistStore( StoreTypes.LOCAL, WebStorageService.createLocalStorageService( persistenceKey ) );
  }
}
