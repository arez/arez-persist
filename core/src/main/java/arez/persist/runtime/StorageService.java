package arez.persist.runtime;

import arez.Arez;
import arez.SafeProcedure;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public interface StorageService
{
  void dispose();

  void scheduleCommit( @Nonnull SafeProcedure commitTriggerAction );

  void commit( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state );

  void restore( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state );

  @Nonnull
  Object encodeState( @Nonnull Map<String, Object> state );

  final class Entry
  {
    @Nonnull
    private final Map<String, Object> _data;
    @Nonnull
    private final Object _encoded;

    Entry( @Nonnull final Map<String, Object> data, @Nonnull final Object encoded )
    {
      _data = Objects.requireNonNull( data );
      _encoded = Objects.requireNonNull( encoded );
    }

    @Nonnull
    public Map<String, Object> getData()
    {
      return _data;
    }

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
