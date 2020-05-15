package arez.persist;

import arez.persist.runtime.ArezPersistTestUtil;
import arez.testng.ArezTestSupport;
import javax.annotation.Nonnull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public abstract class AbstractTest
  implements ArezTestSupport
{
  @Nonnull
  private final TestLogger _logger = new TestLogger();

  @BeforeMethod
  public void preTest()
    throws Exception
  {
    ArezTestSupport.super.preTest();
    ArezPersistTestUtil.resetConfig( false );
    _logger.getEntries().clear();
    ArezPersistTestUtil.setLogger( _logger );
  }

  @AfterMethod
  public void postTest()
  {
    ArezPersistTestUtil.resetConfig( true );
    ArezTestSupport.super.postTest();
  }

  @Nonnull
  protected final TestLogger getTestLogger()
  {
    return _logger;
  }

  protected final void assertDefaultToString( @Nonnull final Object object )
  {
    assertEquals( object.toString(), object.getClass().getName() + "@" + System.identityHashCode( object ) );
  }
}
