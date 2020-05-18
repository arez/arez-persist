package arez.persist.processor;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

final class TypeDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final TypeElement _element;
  @Nonnull
  private final String _defaultStore;
  @Nonnull
  private final List<PropertyDescriptor> _properties;

  TypeDescriptor( @Nonnull final String name,
                  @Nonnull final TypeElement element,
                  @Nonnull final String defaultStore,
                  @Nonnull final List<PropertyDescriptor> properties )
  {
    _name = Objects.requireNonNull( name );
    _element = Objects.requireNonNull( element );
    _defaultStore = Objects.requireNonNull( defaultStore );
    _properties = Objects.requireNonNull( properties );
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  String getDefaultStore()
  {
    return _defaultStore;
  }

  @Nonnull
  List<PropertyDescriptor> getProperties()
  {
    return _properties;
  }
}
