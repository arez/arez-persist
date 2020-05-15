package arez.persist.runtime;

import grim.annotations.OmitType;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Location of all compile time configuration settings for framework.
 */
@SuppressWarnings( "FieldMayBeFinal" )
@OmitType
final class Config
{
  @Nonnull
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_ENVIRONMENT = PROVIDER.isProductionEnvironment();
  private static boolean ENABLE_APPLICATION_STORE = PROVIDER.enableApplicationStore();
  private static boolean CHECK_API_INVARIANTS = PROVIDER.checkApiInvariants();
  @Nonnull
  private static final String LOGGER_TYPE = PROVIDER.loggerType();

  private Config()
  {
  }

  static boolean isDevelopmentEnvironment()
  {
    return !isProductionEnvironment();
  }

  static boolean isProductionEnvironment()
  {
    return PRODUCTION_ENVIRONMENT;
  }

  static boolean shouldCheckApiInvariants()
  {
    return BrainCheckConfig.checkApiInvariants() && CHECK_API_INVARIANTS;
  }

  static boolean isApplicationScopedPersistenceEnabled()
  {
    return ENABLE_APPLICATION_STORE;
  }

  @Nonnull
  static String loggerType()
  {
    return LOGGER_TYPE;
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    boolean isProductionEnvironment()
    {
      return "production".equals( System.getProperty( "arez.persist.environment", "production" ) );
    }

    @GwtIncompatible
    @Override
    boolean enableApplicationStore()
    {
      return "true".equals( System.getProperty( "arez.persist.enable_application_store", "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean checkApiInvariants()
    {
      return "true".equals( System.getProperty( "arez.persist.check_api_invariants",
                                                isProductionEnvironment() ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    @Nonnull
    String loggerType()
    {
      return System.getProperty( "arez.persist.logger", isProductionEnvironment() ? "basic" : "proxy" );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionEnvironment()
    {
      return "production" == System.getProperty( "arez.persist.environment" );
    }

    boolean enableApplicationStore()
    {
      return "true" == System.getProperty( "arez.persist.enable_application_store" );
    }

    boolean checkApiInvariants()
    {
      return "true" == System.getProperty( "arez.persist.check_api_invariants" );
    }

    @Nonnull
    String loggerType()
    {
      /*
       * Valid values are: "none", "console" and "proxy" (for testing)
       */
      return System.getProperty( "arez.logger" );
    }
  }
}
