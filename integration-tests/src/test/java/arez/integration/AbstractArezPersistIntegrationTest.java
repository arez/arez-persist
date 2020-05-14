package arez.integration;

import arez.testng.ArezTestSupport;
import java.io.File;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import static org.testng.Assert.*;

public abstract class AbstractArezPersistIntegrationTest
  implements ArezTestSupport
{
  @Nonnull
  private Path fixtureDir()
  {
    final String fixtureDir = System.getProperty( "arez.integration_fixture_dir" );
    assertNotNull( fixtureDir,
                   "Expected System.getProperty( \"arez.integration_fixture_dir\" ) to return fixture directory if arez.output_fixture_data=true" );

    return new File( fixtureDir ).toPath();
  }

  private boolean outputFiles()
  {
    return System.getProperty( "arez.output_fixture_data", "false" ).equals( "true" );
  }
}
