package arez.persist.runtime;

import arez.SafeProcedure;
import java.util.Map;
import javax.annotation.Nonnull;

final class NoopStorageService
  implements StorageService
{
  @Override
  public void restore( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state )
  {
  }

  @Override
  public void dispose()
  {
  }

  @Override
  public void scheduleCommit( @Nonnull final SafeProcedure commitTriggerAction )
  {
    commitTriggerAction.call();
  }

  @Override
  public void commit( @Nonnull final Map<PersistScope, Map<String, Map<String, Entry>>> state )
  {
    //no-op
  }

  @Nonnull
  @Override
  public Object encodeState( @Nonnull final Map<String, Object> state )
  {
    // No serialization required as never stored to backend
    return state;
  }
}
