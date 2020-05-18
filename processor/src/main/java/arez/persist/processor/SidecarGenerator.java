package arez.persist.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
  private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get( "arez", "Disposable" );
  @Nonnull
  private static final ClassName IDENTIFIABLE_CLASSNAME = ClassName.get( "arez.component", "Identifiable" );
  @Nonnull
  private static final ClassName COMPONENT_DEPENDENCY_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentDependency" );
  @Nonnull
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  @Nonnull
  private static final ClassName OBSERVE_CLASSNAME = ClassName.get( "arez.annotations", "Observe" );
  @Nonnull
  private static final ClassName PRE_DISPOSE_CLASSNAME = ClassName.get( "arez.annotations", "PreDispose" );
  @Nonnull
  private static final ClassName PRIORITY_CLASSNAME = ClassName.get( "arez.annotations", "Priority" );
  @Nonnull
  private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get( "arez.annotations", "DepType" );
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
        .classBuilder( getSidecarName( element ) )
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

    // Add observer that actually persists state on change
    builder.addMethod( MethodSpec.methodBuilder( "savePersistentProperties" )
                         .addAnnotation( AnnotationSpec.builder( OBSERVE_CLASSNAME )
                                           .addMember( "priority", "$T.LOWEST", PRIORITY_CLASSNAME )
                                           .addMember( "nestedActionsAllowed", "true" )
                                           .addMember( "depType", "$T.AREZ_OR_NONE", DEP_TYPE_CLASSNAME )
                                           .build() )
                         .addStatement( "persistState()" )
                         .build() );

    // Add hook so that sidecar will save state unless the peer was disposed first
    // TODO: We should consider making this optional as it adds code-size when rarely required
    builder.addMethod( MethodSpec.methodBuilder( "preDispose" )
                         .addAnnotation( PRE_DISPOSE_CLASSNAME )
                         .addCode( CodeBlock.builder()
                                     .beginControlFlow( "if ( $T.isNotDisposed( _peer ) )", DISPOSABLE_CLASSNAME )
                                     .addStatement( "persistState()" )
                                     .endControlFlow()
                                     .build() )
                         .build() );

    builder.addMethod( buildPersistStateMethod( descriptor ) );
    return builder.build();
  }

  @Nonnull
  private static ClassName getSidecarName( @Nonnull final TypeElement element )
  {
    return GeneratorUtil.getGeneratedClassName( element, "", "_PersistSidecar" );
  }

  @Nonnull
  private static MethodSpec buildPersistStateMethod( @Nonnull final TypeDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "persistState" )
        .addAnnotation( AnnotationSpec.builder( ACTION_CLASSNAME )
                          .addMember( "mutation", "false" )
                          .addMember( "verifyRequired", "false" )
                          .build() );
    for ( final String storeName : descriptor.getStoreNames() )
    {
      final String fieldName = "_" + storeVar( storeName );
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( !$N.isDisposed() )", fieldName );
      block.addStatement( "final $T state = new $T<>()",
                          ParameterizedTypeName.get( Map.class, String.class, Object.class ),
                          HashMap.class );
      block.addStatement( "$N.save( _scope, $T.TYPE, getComponentId(), state )",
                          fieldName,
                          ClassName.bestGuess( "Keys" ) );
      block.endControlFlow();
      method.addCode( block.build() );
    }

    return method.build();
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
