package arez.persist.runtime;

import arez.Arez;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

/**
 * A scope is used to control the lifecycle of state storage.
 * State containing within a scope can be removed from the store using a single call
 * to {@link ArezPersist#releaseScope(Scope)}. Scopes can be nested within other scopes
 * and releasing a scope, releases all child scopes. There is a single root scope in which
 * all other scopes are nested and it can be retrieved using the {@link ArezPersist#getRootScope()}
 * method.
 *
 * <p>A scope may be disposed. It is no longer valid to create nested scopes in disposed scopes or</p>
 */
public final class Scope
{
  /**
   * The name of the root scope.
   */
  @Nonnull
  public static final String ROOT_SCOPE_NAME = "<>";
  /**
   * The parent scope. Every scope but the root scope must contain a parent.
   */
  @Nullable
  private final Scope _parent;
  /**
   * The name of the scope. Scopes have alphanumeric names and may also include '-' or '_' characters.
   */
  @Nonnull
  private final String _name;
  /**
   * Nested scopes if any.
   */
  @Nonnull
  private final Map<String, Scope> _nestedScopes = new HashMap<>();
  private boolean _disposed;

  Scope( @Nullable final Scope parent, @Nonnull final String name )
  {
    assert ( null == parent && ROOT_SCOPE_NAME.equals( name ) ) ||
           ( null != parent && !ROOT_SCOPE_NAME.equals( name ) );
    _parent = parent;
    _name = Objects.requireNonNull( name );
  }

  /**
   * Return the simple name of the scope.
   * The simple name is the name that was used to create the scope.
   *
   * @return the simple name of the scope.
   */
  @Nonnull
  public String getName()
  {
    return _name;
  }

  /**
   * Return the qualified name of the scope.
   * The qualified name includes the parent scopes name followed by a "." character unless
   * the parent scope is the root scope.
   *
   * @return the qualified name of the scope.
   */
  @Nonnull
  public String getQualifiedName()
  {
    return null == _parent || null == _parent._parent ? _name : _parent.getQualifiedName() + "." + _name;
  }

  /**
   * Find or create a scope directly nested under the current scope.
   * This must not be invoked on a disposed scope.
   *
   * @param name the name of the nested scope.
   * @return the scope.
   */
  @Nonnull
  public Scope findOrCreateScope( @Nonnull final String name )
  {
    if ( ArezPersist.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !isDisposed(),
                    () -> "findOrCreateScope() invoked on disposed scope named '" + _name + "'" );
      apiInvariant( () -> isValidName( name ),
                    () -> "findOrCreateScope() invoked with name '" + name +
                          "' but the name has invalid characters. Names must contain alphanumeric " +
                          "characters, '-' or '_'" );
    }
    final Scope existing = findScope( name );
    if ( null != existing )
    {
      return existing;
    }
    else
    {
      final Scope scope = new Scope( this, name );
      _nestedScopes.put( name, scope );
      return scope;
    }
  }

  /**
   * Return true if this scope has been disposed.
   * A scope is disposed by invoking {@link ArezPersist#disposeScope(Scope)} on the scope or any parent scope.
   *
   * @return true if this scope has been disposed, false otherwise.
   */
  public boolean isDisposed()
  {
    return _disposed;
  }

  @Override
  public String toString()
  {
    if ( Arez.areNamesEnabled() )
    {
      return getQualifiedName();
    }
    else
    {
      return super.toString();
    }
  }

  @Nullable
  Scope findScope( @Nonnull final String name )
  {
    return _nestedScopes.get( name );
  }

  @Nonnull
  Collection<Scope> getNestedScopes()
  {
    return _nestedScopes.values();
  }

  /**
   * Dispose the scope unless it has already been disposed.
   * This method assumes that nested scopes have already been disposed.
   * This method will removed the scope from the list of scopes in the parent scope.
   */
  void dispose()
  {
    if ( !_disposed )
    {
      // The assumption is that by the time we get here all nested scopes have also been disposed
      assert _nestedScopes.isEmpty();
      if ( null != _parent )
      {
        _parent._nestedScopes.remove( _name );
      }
      _disposed = true;
    }
  }

  /**
   * Return true if the name is valid.
   *
   * @param name the name to check.
   * @return true if the name is valid.
   */
  private boolean isValidName( @Nonnull final String name )
  {
    if ( 0 == name.length() )
    {
      return false;
    }
    else
    {
      final int length = name.length();
      for ( int i = 0; i < length; i++ )
      {
        final char ch = name.charAt( i );
        if ( !Character.isLetterOrDigit( ch ) && '_' != ch && '-' != ch )
        {
          return false;
        }
      }
      return true;
    }
  }
}
