package arez.persist.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
  @Nonnull
  private static final ClassName AREZ_CONTEXT_CLASSNAME = ClassName.get( "arez", "ArezContext" );
  @Nonnull
  private static final ClassName IDENTIFIABLE_CLASSNAME = ClassName.get( "arez.component", "Identifiable" );
  @Nonnull
  private static final ClassName COMPONENT_DEPENDENCY_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentDependency" );
  @Nonnull
  private static final ClassName CONTEXT_REF_CLASSNAME = ClassName.get( "arez.annotations", "ContextRef" );
  @Nonnull
  private static final ClassName SCOPE_CLASSNAME = ClassName.get( "arez.persist.runtime", "Scope" );
  @Nonnull
  private static final ClassName STORE_CLASSNAME = ClassName.get( "arez.persist.runtime", "Store" );

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

    buildFieldAndConstructor( descriptor, builder );

    // build template method to get arez context
    builder.addMethod( MethodSpec.methodBuilder( "context" )
                         .returns( AREZ_CONTEXT_CLASSNAME )
                         .addModifiers( Modifier.ABSTRACT )
                         .addAnnotation( CONTEXT_REF_CLASSNAME )
                         .build() );

    // build method to get component id as string from peer
    builder.addMethod( MethodSpec.methodBuilder( "getComponentId" )
                         .returns( String.class )
                         .addModifiers( Modifier.PRIVATE )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                         .addStatement( "return $T.valueOf( $T.requireNonNull( $T.getArezId( _peer ) ) )",
                                        String.class,
                                        Objects.class,
                                        IDENTIFIABLE_CLASSNAME )
                         .build() );
    return builder.build();
  }

  private static void buildFieldAndConstructor( @Nonnull final TypeDescriptor descriptor,
                                                @Nonnull final TypeSpec.Builder builder )
  {
    final TypeElement element = descriptor.getElement();
    final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
    builder.addField( FieldSpec
                        .builder( SCOPE_CLASSNAME, "_scope", Modifier.PRIVATE, Modifier.FINAL )
                        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                        .build() );
    ctor.addParameter( ParameterSpec
                         .builder( SCOPE_CLASSNAME, "scope", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                         .build() );
    ctor.addStatement( "$N = $T.requireNonNull( $N )", "_scope", Objects.class, "scope" );

    final ClassName peerType = ClassName.get( element );
    builder.addField( FieldSpec.builder( peerType, "_peer", Modifier.FINAL )
                        .addAnnotation( COMPONENT_DEPENDENCY_CLASSNAME )
                        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                        .build() );
    ctor.addParameter( ParameterSpec
                         .builder( peerType, "peer", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                         .build() );
    ctor.addStatement( "$N = $T.requireNonNull( $N )", "_peer", Objects.class, "peer" );

    for ( final String storeName : descriptor.getStoreNames() )
    {
      final String varName = storeVar( storeName );
      final String fieldName = "_" + varName;
      builder.addField( FieldSpec.builder( STORE_CLASSNAME, fieldName, Modifier.PRIVATE, Modifier.FINAL )
                          .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                          .build() );
      ctor.addParameter( ParameterSpec
                           .builder( STORE_CLASSNAME, varName, Modifier.FINAL )
                           .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                           .build() );
      ctor.addStatement( "$N = $T.requireNonNull( $N )", fieldName, Objects.class, varName );
    }

    builder.addMethod( ctor.build() );
  }

  @Nonnull
  private static String storeVar( @Nonnull final String storeName )
  {
    return storeName + "Store";
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
