package arez.persist.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jdepend.framework.DependencyConstraint;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public final class JDependTest
{
  @Test
  public void dependencyAnalysis()
    throws Exception
  {
    final JDepend jdepend = new JDepend( PackageFilter.all().excluding( "java.*", "javax.*" ) );
    jdepend.addDirectory( compileTargetDir() );
    jdepend.analyze();

    final DependencyConstraint constraint = new DependencyConstraint();

    final JavaPackage arez = constraint.addPackage( "arez" );
    final JavaPackage arezComponent = constraint.addPackage( "arez.component" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage persist = constraint.addPackage( "arez.persist" );
    final JavaPackage runtime = constraint.addPackage( "arez.persist.runtime" );
    final JavaPackage browserRuntime = constraint.addPackage( "arez.persist.runtime.browser" );
    final JavaPackage jsinterop = constraint.addPackage( "jsinterop.annotations" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage elemental2Core = constraint.addPackage( "elemental2.core" );
    final JavaPackage elemental2Dom = constraint.addPackage( "elemental2.dom" );
    final JavaPackage elemental2Webstorage = constraint.addPackage( "elemental2.webstorage" );

    runtime.dependsUpon( jsinterop );
    runtime.dependsUpon( persist );
    runtime.dependsUpon( braincheck );
    runtime.dependsUpon( arez );
    runtime.dependsUpon( arezComponent );

    browserRuntime.dependsUpon( arez );
    browserRuntime.dependsUpon( persist );
    browserRuntime.dependsUpon( runtime );
    browserRuntime.dependsUpon( jsinteropBase );
    browserRuntime.dependsUpon( elemental2Core );
    browserRuntime.dependsUpon( elemental2Dom );
    browserRuntime.dependsUpon( elemental2Webstorage );

    final DependencyConstraint.MatchResult result = jdepend.analyzeDependencies( constraint );

    final List<JavaPackage> undefinedPackages = result.getUndefinedPackages();
    if ( !undefinedPackages.isEmpty() )
    {
      fail( "Undefined Packages: " +
            undefinedPackages.stream().map( Object::toString ).collect( Collectors.joining( ", " ) ) );
    }

    final List<JavaPackage[]> nonMatchingPackages = result.getNonMatchingPackages();
    if ( !nonMatchingPackages.isEmpty() )
    {
      final StringBuilder sb = new StringBuilder();
      sb.append( "Discovered packages where relationships do not align.\n" );
      for ( final JavaPackage[] packages : nonMatchingPackages )
      {
        final JavaPackage expected = packages[ 0 ];
        final JavaPackage actual = packages[ 1 ];

        final List<JavaPackage> oldAfferents = new ArrayList<>( expected.getAfferents() );
        oldAfferents.removeAll( actual.getAfferents() );

        oldAfferents.forEach( p -> sb
          .append( "Package " )
          .append( p.getName() )
          .append( " no longer depends upon " )
          .append( expected.getName() )
          .append( "\n" )
        );

        final List<JavaPackage> newAfferents = new ArrayList<>( actual.getAfferents() );
        newAfferents.removeAll( expected.getAfferents() );

        newAfferents.forEach( p -> sb
          .append( "Package " )
          .append( p.getName() )
          .append( " now depends upon " )
          .append( expected.getName() )
          .append( "\n" )
        );
      }
      fail( sb.toString() );
    }
  }

  @Nonnull
  private String compileTargetDir()
  {
    final String fixtureDir = System.getProperty( "arez.persist.core.compile_target" );
    assertNotNull( fixtureDir,
                   "Expected System.getProperty( \"arez.persist.core.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
