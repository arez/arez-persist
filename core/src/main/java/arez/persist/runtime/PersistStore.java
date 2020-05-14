package arez.persist.runtime;

import arez.SafeProcedure;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

/**
 * The store that contains a cached copy of any state persisted.
 * The store is also responsible for committing state to a storage service when the state has changed.
 * The commit is typically an asynchronous process that occurs in another process so as not to block the
 * ui thread.
 */
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
  /**
   * The service that stores the state.
   */
  @Nonnull
  private final StorageService _storageService;
  /**
   * Cache for action that performs commit.
   */
  @Nonnull
  private final SafeProcedure _commitTriggerAction = this::commit;

  /**
   * Create the store.
   *
   * @param storageService the underlying storage service that manages the state.
   */
  PersistStore( @Nonnull final StorageService storageService )
  {
    _storageService = Objects.requireNonNull( storageService );
  }

  /**
   * Clear the state in store and reload it from the backend service.
   */
  void restore()
  {
    _config.clear();
    _storageService.restore( _config );
  }

  /**
   * Release all state stored under scope and any nested scope.
   * If any state is actually removed, then a commit is scheduled.
   *
   * @param scope the scope.
   */
  void releaseScope( @Nonnull final PersistScope scope )
  {
    scope.getNestedScopes().forEach( this::releaseScope );
    if ( null != _config.remove( scope ) )
    {
      scheduleCommit();
    }
  }

  /**
   * Save the state for a single component to the store.
   * If the state value is empty this operation is effectively a remove. If any changes are made to
   * the store as a result of this operation then a commit is scheduled.
   *
   * @param scope the scope in which the state is saved.
   * @param type  the string that identifies the type of component.
   * @param id    a string representation of the component id.
   * @param state the state to store.
   */
  public void save( @Nonnull final PersistScope scope,
                    @Nonnull final String type,
                    @Nonnull final String id,
                    @Nonnull final Map<String, Object> state )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !scope.isDisposed(),
                    () -> "PersistStore.save() passed a disposed scope named '" + scope.getName() + "'" );
    }

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

  /**
   * Remove the state for a single component from the store.
   * If the state does not exist then this is a noop, otherwise a commit is scheduled.
   *
   * @param scope the scope in which the state is saved.
   * @param type  the string that identifies the type of component.
   * @param id    a string representation of the component id.
   */
  public void remove( @Nonnull final PersistScope scope, @Nonnull final String type, @Nonnull final String id )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !scope.isDisposed(),
                    () -> "PersistStore.remove() passed a disposed scope named '" + scope.getName() + "'" );
    }
    final Map<String, Map<String, StorageService.Entry>> scopeMap = _config.get( scope );
    final Map<String, StorageService.Entry> typeMap = null != scopeMap ? scopeMap.get( type ) : null;
    if ( null != typeMap && null != typeMap.remove( id ) )
    {
      scheduleCommit();
    }
  }

  /**
   * Retrieve the state for a single component from the store.
   * If the state does not exist then a null is returned.
   *
   * @param scope the scope in which the state is saved.
   * @param type  the string that identifies the type of component.
   * @param id    a string representation of the component id.
   * @return the component state if it exists, else null.
   */
  @Nullable
  public Map<String, Object> get( @Nonnull final PersistScope scope,
                                  @Nonnull final String type,
                                  @Nonnull final String id )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !scope.isDisposed(),
                    () -> "PersistStore.get() passed a disposed scope named '" + scope.getName() + "'" );
    }
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

  /**
   * Schedule a commit if one is not already pending.
   */
  private void scheduleCommit()
  {
    if ( _committed )
    {
      _storageService.scheduleCommit( _commitTriggerAction );
      _committed = false;
    }
  }

  /**
   * Commit state to storage service.
   */
  private void commit()
  {
    if ( !_committed )
    {
      _storageService.commit( _config );
      _committed = true;
    }
  }
}
