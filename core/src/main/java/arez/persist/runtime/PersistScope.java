package arez.persist.runtime;

import arez.Arez;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

public final class PersistScope
{
  @Nonnull
  public static final String DEFAULT_SCOPE_NAME = "<>";
  @Nullable
  private final PersistScope _parent;
  @Nonnull
  private final String _name;
  @Nonnull
  private final Map<String, PersistScope> _nestedScopes = new HashMap<>();
  private boolean _disposed;

  PersistScope( @Nullable final PersistScope parent, @Nonnull final String name )
  {
    _parent = parent;
    _name = Objects.requireNonNull( name );
  }

  @Nonnull
  public String getName()
  {
    return _name;
  }

  @Nonnull
  public String getQualifiedName()
  {
    return null == _parent || null == _parent._parent ? _name : _parent.getQualifiedName() + "." + _name;
  }

  @Nonnull
  public PersistScope findOrCreateScope( @Nonnull final String name )
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
    final PersistScope existing = findScope( name );
    if ( null != existing )
    {
      return existing;
    }
    else
    {
      final PersistScope scope = new PersistScope( this, name );
      _nestedScopes.put( name, scope );
      return scope;
    }
  }

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
  PersistScope findScope( @Nonnull final String name )
  {
    return _nestedScopes.get( name );
  }

  @Nonnull
  Collection<PersistScope> getNestedScopes()
  {
    return _nestedScopes.values();
  }

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
   * Return true if the name is a valid java identifier.
   *
   * @param value the value to check.
   * @return true if the name is a valid java identifier.
   */
  private boolean isValidName( @Nonnull final String value )
  {
    if ( 0 == value.length() )
    {
      return false;
    }
    else
    {
      final int length = value.length();
      for ( int i = 0; i < length; i++ )
      {
        final char ch = value.charAt( i );
        if ( !Character.isLetterOrDigit( ch ) && '_' != ch && '-' != ch )
        {
          return false;
        }
      }
      return true;
    }
  }
}
