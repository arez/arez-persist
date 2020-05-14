package arez.persist.runtime;

import arez.SafeProcedure;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.webstorage.Storage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

final class WebStorageService
  implements StorageService
{
  /**
   * A reference to the "beforeunload" listener so that the listener can be removed on disposed.
   */
  @Nonnull
  private final EventListener _beforeUnloadListener = e -> maybeCommit();
  /**
   * The browsers storage api targeted by the service.
   */
  @Nonnull
  private final Storage _storage;
  /**
   * The key used to store/access state in storage api.
   */
  @Nonnull
  private final String _address;
  /**
   * A cached copy of last trigger action supplied. Used to try and trigger save before app unloads.
   */
  @Nullable
  private SafeProcedure _commitTriggerAction;
  private int _idleCallbackId;

  WebStorageService( @Nonnull final Storage storage, @Nonnull final String address )
  {
    _storage = Objects.requireNonNull( storage );
    _address = Objects.requireNonNull( address );
    // It should be noted that we don't
    DomGlobal.window.addEventListener( "beforeunload", _beforeUnloadListener );
  }

  @Override
  public void dispose()
  {
    if ( 0 != _idleCallbackId )
    {
      DomGlobal.cancelIdleCallback( _idleCallbackId );
      _idleCallbackId = 0;
    }
    DomGlobal.window.removeEventListener( "beforeunload", _beforeUnloadListener );
  }

  @Override
  public void scheduleCommit( @Nonnull final SafeProcedure commitTriggerAction )
  {
    _commitTriggerAction = commitTriggerAction;
    // An alternative strategy is to send a message to a WebWorker containing the
    // state to save and performing the save in the other thread but we have yet
    // to see a scenario where performance requirements would warrant the extra complexity
    _idleCallbackId = DomGlobal.requestIdleCallback( t -> commitTriggerAction.call() );
  }

  @Override
  public void commit( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state )
  {
    _idleCallbackId = 0;
    final JsPropertyMap<Object> data = JsPropertyMap.of();
    for ( final Map.Entry<PersistScope, Map<String, Map<String, Entry>>> scopeEntry : state.entrySet() )
    {
      final JsPropertyMap<Object> scope = JsPropertyMap.of();
      for ( final Map.Entry<String, Map<String, Entry>> entry : scopeEntry.getValue().entrySet() )
      {
        final JsPropertyMap<Object> type = JsPropertyMap.of();
        for ( final Map.Entry<String, Entry> instance : entry.getValue().entrySet() )
        {
          type.set( instance.getKey(), instance.getValue().getEncoded() );
        }
        scope.set( entry.getKey(), type );
      }
      data.set( scopeEntry.getKey().getQualifiedName(), scope );
    }
    if ( 0 == JsObject.keys( data ).length )
    {
      _storage.removeItem( _address );
    }
    else
    {
      _storage.setItem( _address, Global.JSON.stringify( data ) );
    }
  }

  @Nonnull
  @Override
  public Object encodeState( @Nonnull final Map<String, Object> state )
  {
    final JsPropertyMap<Object> encoded = JsPropertyMap.of();
    for ( final Map.Entry<String, Object> entry : state.entrySet() )
    {
      encoded.set( entry.getKey(), entry.getValue() );
    }
    return encoded;
  }

  @Override
  public void restore( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state )
  {
    final String item = _storage.getItem( _address );
    if ( null != item )
    {
      final JsPropertyMap<Object> scopes = Js.uncheckedCast( Global.JSON.parse( item ) );
      final JsArray<String> scopeNames = JsObject.keys( scopes );
      final int scopeCount = scopeNames.length;
      for ( int s = 0; s < scopeCount; s++ )
      {
        final String scopeName = scopeNames.getAt( s );
        restoreScope( state, scopeName, scopes.getAsAny( scopeName ).asPropertyMap() );
      }
    }
  }

  private void restoreScope( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state,
                             @Nonnull final String scopeName,
                             @Nonnull final JsPropertyMap<Object> types )
  {
    final JsArray<String> typeNames = JsObject.keys( types );
    final int typeCount = typeNames.length;
    for ( int i = 0; i < typeCount; i++ )
    {
      final String typeName = typeNames.getAt( i );
      restoreType( state, scopeName, typeName, types.getAsAny( typeName ).asPropertyMap() );
    }
  }

  private void restoreType( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state,
                            @Nonnull final String scopeName,
                            @Nonnull final String typeName,
                            @Nonnull final JsPropertyMap<Object> idMap )
  {
    final PersistScope scope = ArezPersist.findOrCreateScope( scopeName );
    final Map<String, Entry> entryMap = new HashMap<>();
    final JsArray<String> ids = JsObject.keys( idMap );
    final int idCount = ids.length;
    for ( int j = 0; j < idCount; j++ )
    {
      final String id = ids.getAt( j );
      final JsPropertyMap<Object> encoded = Js.uncheckedCast( idMap.get( id ) );
      entryMap.put( id, new StorageService.Entry( decodeState( encoded ), encoded ) );
    }
    state.computeIfAbsent( scope, s -> new HashMap<>() ).put( typeName, entryMap );
  }

  private void maybeCommit()
  {
    // If we have been supplied an action before, try to trigger a commit in case changes are in progress
    if ( null != _commitTriggerAction )
    {
      _commitTriggerAction.call();
    }
  }

  @Nonnull
  private Map<String, Object> decodeState( @Nonnull final JsPropertyMap<Object> encoded )
  {
    final Map<String, Object> data = new HashMap<>();
    final JsArray<String> keys = JsObject.keys( encoded );
    final int keyCount = keys.length;
    for ( int i = 0; i < keyCount; i++ )
    {
      final String key = keys.getAt( i );
      data.put( key, encoded.get( key ) );
    }
    return data;
  }
}
