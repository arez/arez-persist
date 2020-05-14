package arez.persist.runtime;

import grim.annotations.OmitType;
import javax.annotation.Nonnull;
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

  @Nonnull
  static String loggerType()
  {
    return "none";
  }
}
