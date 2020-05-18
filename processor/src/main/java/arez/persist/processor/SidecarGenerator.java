package arez.persist.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class SidecarGenerator
{
  @Nonnull
  private static final ClassName AREZ_CLASSNAME = ClassName.get( "arez", "Arez" );

  private SidecarGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final TypeDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();
    final TypeSpec.Builder builder =
      TypeSpec
        .classBuilder( GeneratorUtil.getGeneratedClassName( element, "", "_PersistSidecar" ) )
        .addModifiers( Modifier.ABSTRACT );
    if ( element.getModifiers().contains( Modifier.PUBLIC ) )
    {
      builder.addModifiers( Modifier.ABSTRACT );
    }

    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.copyWhitelistedAnnotations( element, builder );

    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, ArezPersistProcessor.class.getName() );
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                        builder,
                                                        Collections.emptyList(),
                                                        Collections.singletonList( element.asType() ) );

    // Create a nested keys type to eliminate any possibility GWT will
    // attempt to create a <clinit> for sidecar type and the deopt that brings  .
    builder.addType( buildKeysType( descriptor ) );

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildKeysType( @Nonnull final TypeDescriptor descriptor )
  {
    final TypeSpec.Builder keys = TypeSpec.classBuilder( "Keys" );
    keys.addModifiers( Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL );
    keys.addField( FieldSpec
                     .builder( String.class, "TYPE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
                     .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                     .initializer( "$T.areNamesEnabled() ? $S : $T.class.getName()",
                                   AREZ_CLASSNAME,
                                   descriptor.getName(),
                                   descriptor.getElement() )
                     .build() );

    int propertyIndex = 0;
    for ( final PropertyDescriptor property : descriptor.getProperties() )
    {
      keys.addField( FieldSpec
                       .builder( String.class,
                                 property.getConstantName(),
                                 Modifier.PRIVATE,
                                 Modifier.STATIC,
                                 Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                       .initializer( "$T.areNamesEnabled() ? $S : $S",
                                     AREZ_CLASSNAME,
                                     property.getName(),
                                     String.valueOf( (char) ( 'a' + propertyIndex ) ) )
                       .build() );
      propertyIndex++;
    }
    return keys.build();
  }
}
