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
   * If unspecified and the method is named according to javabeans getter conventions then the java bean property name
   * will be used, otherwise the name of the method will be used.
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
   * The name of the store must comply with the requirements for a java identifier.
   *
   * @return the key identifying the store where the observable data is stored.
   */
  @Nonnull
  String store() default "<default>";
}
