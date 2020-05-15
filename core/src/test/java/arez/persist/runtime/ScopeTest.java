package arez.persist.runtime;

import arez.ArezTestUtil;
import arez.persist.AbstractTest;
import org.realityforge.guiceyloops.shared.ValueUtil;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public final class ScopeTest
  extends AbstractTest
{
  @Test
  public void basicOperation()
  {
    final Scope parent = ArezPersist.getRootScope();
    final String name = ValueUtil.randomString();

    assertEquals( parent.getNestedScopes().size(), 0 );

    assertNull( parent.findScope( name ) );

    assertEquals( parent.getNestedScopes().size(), 0 );

    final Scope scope = parent.findOrCreateScope( name );

    assertEquals( parent.getNestedScopes().size(), 1 );
    assertTrue( parent.getNestedScopes().contains( scope ) );

    assertEquals( parent.findOrCreateScope( name ), scope );

    assertEquals( parent.getNestedScopes().size(), 1 );
    assertTrue( parent.getNestedScopes().contains( scope ) );

    assertEquals( scope.getName(), name );
    assertEquals( scope.getQualifiedName(), name );
    assertEquals( scope.toString(), name );
    assertFalse( scope.isDisposed() );
    assertEquals( scope.getNestedScopes().size(), 0 );

    scope.dispose();

    assertEquals( parent.getNestedScopes().size(), 0 );
    assertTrue( scope.isDisposed() );

    scope.dispose();

    assertEquals( parent.getNestedScopes().size(), 0 );
    assertTrue( scope.isDisposed() );
  }

  @Test
  public void toString_output()
  {
    final Scope parent = ArezPersist.getRootScope();
    final String name = ValueUtil.randomString();

    final Scope scope = parent.findOrCreateScope( name );
    assertEquals( scope.toString(), name );

    ArezTestUtil.disableNames();

    assertDefaultToString( scope );
  }

  @Test
  public void findOrCreateScope_on_disposed()
  {
    final Scope parent = ArezPersist.getRootScope();
    final String name1 = ValueUtil.randomString();
    final String name2 = ValueUtil.randomString();

    final Scope scope = parent.findOrCreateScope( name1 );

    scope.dispose();

    assertEquals( parent.getNestedScopes().size(), 0 );
    assertTrue( scope.isDisposed() );

    assertInvariantFailure( () -> scope.findOrCreateScope( name2 ),
                            "findOrCreateScope() invoked on disposed scope named '" + name1 + "'" );
  }

  @Test
  public void findOrCreateScope_emptyName()
  {
    final Scope parent = ArezPersist.getRootScope();

    assertInvariantFailure( () -> parent.findOrCreateScope( "" ),
                            "findOrCreateScope() invoked with name '' but the name has invalid characters. Names must contain alphanumeric characters, '-' or '_'" );
  }
  @Test
  public void findOrCreateScope_invalidName()
  {
    final Scope parent = ArezPersist.getRootScope();

    assertInvariantFailure( () -> parent.findOrCreateScope( " * -jhsagdjhg2" ),
                            "findOrCreateScope() invoked with name ' * -jhsagdjhg2' but the name has invalid characters. Names must contain alphanumeric characters, '-' or '_'" );
  }
}
