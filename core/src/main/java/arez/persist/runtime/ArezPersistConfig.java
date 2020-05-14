package arez.persist.runtime;

import grim.annotations.OmitType;
import org.realityforge.braincheck.BrainCheckConfig;

@OmitType
final class ArezPersistConfig
{
  static boolean shouldCheckApiInvariants()
  {
    return BrainCheckConfig.checkApiInvariants();
  }

  static boolean isApplicationScopedPersistenceEnabled()
  {
    return true;
  }
}
