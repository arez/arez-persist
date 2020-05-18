package arez.persist.processor;

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

    return builder.build();
  }
}
