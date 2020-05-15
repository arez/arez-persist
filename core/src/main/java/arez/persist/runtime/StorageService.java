package arez.persist.runtime;

import arez.Arez;
import arez.SafeProcedure;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Interface implemented to provide the state storage and retrieval services.
 */
public interface StorageService
{
  /**
   * Dispose the service.
   * The service may release any resources no longer required after dispose has been
   * called. It is expected that no other methods will be invoked after this method
   * has been invoked.
   */
  void dispose();

  /**
   * Method invoked when state has changed.
   * The storage service is expected to invoke the {@code commitTriggerAction.call()}
   * method when service wants to commit changes to the service. This is likely to be
   * invoked many times in quick succession and it is up to the storage service to decide
   * how to batch up the changes and thus when to schedule the commit.
   *
   * @param commitTriggerAction the action to invoke when a commit should be triggered.
   */
  void scheduleCommit( @Nonnull SafeProcedure commitTriggerAction );

  /**
   * Invoked by the store to save the state to the storage service.
   * The map contains scopes, mapped to component types, mapped to component ids, map
   * to instance data for that component.
   *
   * @param state the state to store.
   */
  void commit( @Nonnull final Map<Scope, Map<String, Map<String, Entry>>> state );

  /**
   * Restore state from the service into the specified state parameter.
   *
   * @param state the map in which to store data retrieved from the service.
   */
  void restore( @Nonnull final Map<Scope, Map<String, Map<String, Entry>>> state );

  /**
   * Encode state in a way that makes storage to the backend easier.
   * This is invoked when the change occurs and thus should be relatively fast.
   *
   * @param state the component state.
   * @return the encoded form for this service.
   */
  @Nonnull
  Object encodeState( @Nonnull Map<String, Object> state );

  /**
   * An entry containing the state for a particular component.
   */
  final class Entry
  {
    @Nonnull
    private final Map<String, Object> _data;
    @Nonnull
    private final Object _encoded;

    public Entry( @Nonnull final Map<String, Object> data, @Nonnull final Object encoded )
    {
      _data = Objects.requireNonNull( data );
      _encoded = Objects.requireNonNull( encoded );
    }

    /**
     * Return the decoded data.
     *
     * @return the decoded data.
     */
    @Nonnull
    public Map<String, Object> getData()
    {
      return _data;
    }

    /**
     * Return the encoded data.
     *
     * @return the encoded data.
     */
    @Nonnull
    public Object getEncoded()
    {
      return _encoded;
    }

    @Override
    public String toString()
    {
      if ( Arez.areNamesEnabled() )
      {
        return String.valueOf( _data );
      }
      else
      {
        return super.toString();
      }
    }
  }
}
