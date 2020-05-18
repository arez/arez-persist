package arez.persist;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation applied to arez components that triggers the generation of persistence code.
 */
@Documented
@Target( ElementType.TYPE )
public @interface PersistType
{
  /**
   * Return the name used to persist the type.
   * If not specified, then the simple name of the class will be used. The name should comply with the
   * requirements for a java identifier.
   *
   * <p>It should be noted that production mode persistent properties that are not persisted across
   * reloads will use synthetic keys as an optimization strategy.</p>
   *
   * @return the name used to persist the property.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * The key identifying the default store where the observable data is stored.
   * Individual properties annotated with the {@link Persist} annotation can still override the store used.
   *
   * @return the key identifying the default store where the observable data is stored.
   */
  @Nonnull
  String defaultStore() default StoreTypes.APPLICATION;
}
