package arez.persist.processor;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.DeferredElementSet;

/**
 * Annotation processor that analyzes Arez annotated source and generates models from the annotations.
 */
@SupportedAnnotationTypes( Constants.PERSIST_TYPE_CLASSNAME )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "arez.persist.defer.unresolved", "arez.persist.defer.errors", "arez.persist.debug" } )
public final class ArezPersistProcessor
  extends AbstractStandardProcessor
{
  @Nonnull
  private final DeferredElementSet _deferredTypes = new DeferredElementSet();

  @Override
  @Nonnull
  protected String getIssueTrackerURL()
  {
    return "https://github.com/arez/arez-persist/issues";
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "arez.persist";
  }

  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    processTypeElements( annotations, env, Constants.PERSIST_TYPE_CLASSNAME, _deferredTypes, this::process );

    errorIfProcessingOverAndInvalidTypesDetected( env );
    return true;
  }

  private void process( @Nonnull final TypeElement element )
    throws Exception
  {
  }
}
