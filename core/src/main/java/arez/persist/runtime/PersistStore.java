package arez.persist.runtime;

import arez.SafeProcedure;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PersistStore
{
  /**
   * In-memory cache of configuration data.
   */
  @Nonnull
  private final Map<PersistScope, Map<String, Map<String, StorageService.Entry>>> _config = new HashMap<>();
  /**
   * Has the config data been committed to the backend storage?
   */
  private boolean _committed = true;
  @Nonnull
  private final StorageService _storageService;
  @Nonnull
  private final SafeProcedure _commitTriggerAction = this::commit;

  PersistStore( @Nonnull final StorageService storageService )
  {
    _storageService = Objects.requireNonNull( storageService );
  }

  void restore()
  {
    _config.clear();
    _storageService.restore( _config );
  }

  void releaseScope( @Nonnull final PersistScope scope )
  {
    scope.getNestedScopes().forEach( this::releaseScope );
    if ( null != _config.remove( scope ) )
    {
      scheduleCommit();
    }
  }

  public void save( @Nonnull final PersistScope scope,
                    @Nonnull final String type,
                    @Nonnull final String id,
                    @Nonnull final Map<String, Object> state )
  {
    if ( state.isEmpty() )
    {
      remove( scope, type, id );
    }
    else
    {
      // Initial experiments converted state in a separate idle callback but the overhead of
      // asynchronous queuing and callback when the encoded types are primitive and not rich types
      // requiring converters did not seem worth it
      _config
        .computeIfAbsent( scope, t -> new HashMap<>() )
        .computeIfAbsent( type, t -> new HashMap<>() )
        .put( id, new StorageService.Entry( state, _storageService.encodeState( state ) ) );
      scheduleCommit();
    }
  }

  public void remove( @Nonnull final PersistScope scope, @Nonnull final String type, @Nonnull final String id )
  {
    final Map<String, Map<String, StorageService.Entry>> scopeMap = _config.get( scope );
    final Map<String, StorageService.Entry> typeMap = null != scopeMap ? scopeMap.get( type ) : null;
    if ( null != typeMap && null != typeMap.remove( id ) )
    {
      scheduleCommit();
    }
  }

  @Nullable
  public Map<String, Object> get( @Nonnull final PersistScope scope,
                                  @Nonnull final String type,
                                  @Nonnull final String id )
  {
    final Map<String, Map<String, StorageService.Entry>> scopeMap = _config.get( scope );
    final Map<String, StorageService.Entry> typeMap = null != scopeMap ? scopeMap.get( type ) : null;
    if ( null != typeMap )
    {
      final StorageService.Entry entry = typeMap.get( id );
      return null == entry ? null : entry.getData();
    }
    else
    {
      return null;
    }
  }

  void dispose()
  {
    _storageService.dispose();
  }

  private void scheduleCommit()
  {
    if ( _committed )
    {
      _storageService.scheduleCommit( _commitTriggerAction );
      _committed = false;
    }
  }

  private void commit()
  {
    if ( !_committed )
    {
      _storageService.commit( _config );
      _committed = true;
    }
  }
}
