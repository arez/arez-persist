package arez.persist;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation applied to observable properties that direct Arez to persist the property.
 */
@Documented
@Target( ElementType.METHOD )
public @interface Persist
{
  /**
   * Return the name used to persist the property.
   * If not specified, then the name of the observable property will be used.
   *
   * <p>It should be noted that production mode persistent properties that are not persisted across
   * reloads will use synthetic keys as an optimization strategy.</p>
   *
   * @return the name used to persist the property.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * The key identifying the store where the observable data is stored.
   *
   * @return the key identifying the store where the observable data is stored.
   */
  @Nonnull
  String store() default "<default>";
}
