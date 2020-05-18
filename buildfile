require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'
require 'buildr/jacoco'
require 'buildr/top_level_generate_dir'

ELEMENTAL2_DEPS =
  [
    :elemental2_core,
    :elemental2_dom,
    :elemental2_promise,
    :elemental2_webstorage,
    :jsinterop_base
  ]

# JDK options passed to test environment. Essentially turns assertions on.
TEST_OPTIONS =
  {
    'braincheck.environment' => 'development',
    'arez.environment' => 'development',
    'arez.persist.environment' => 'development'
  }

desc 'Arez-Persist: Arez extension for persisting observable properties'
define 'arez-persist' do
  project.group = 'org.realityforge.arez.persist'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all,-processing,-serial'
  project.compile.options.warnings = true
  project.compile.options.other = %w(-Werror -Xmaxerrs 10000 -Xmaxwarns 10000)

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('arez/arez-persist')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'The Core Library'
  define 'core' do
    pom.include_transitive_dependencies << artifact(:javax_annotation)
    pom.include_transitive_dependencies << artifact(:jsinterop_annotations)
    pom.include_transitive_dependencies << artifact(:braincheck)
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' }

    compile.with :javax_annotation,
                 :grim_annotations,
                 :braincheck,
                 :arez_core,
                 :jetbrains_annotations,
                 ELEMENTAL2_DEPS,
                 :jsinterop_annotations

    project.processorpath << artifacts(:grim_processor, :javax_json)
    project.processorpath << artifacts(:arez_processor)

    test.options[:properties] =
      TEST_OPTIONS.merge('arez.persist.core.compile_target' => compile.target.to_s)
    test.options[:java_args] = ['-ea']

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with :guiceyloops, :jdepend, :arez_testng, :mockito
  end

  desc 'The Annotation processor'
  define 'processor' do
    pom.dependency_filter = Proc.new { |_| false }

    compile.with :javax_annotation,
                 :proton_core,
                 :javapoet

    test.with :compile_testing,
              Java.tools_jar,
              :proton_qa,
              :truth,
              :junit,
              :guava,
              :guava_failureaccess,
              :hamcrest_core,
              :arez_processor,
              project('core').package(:jar),
              project('core').compile.dependencies

    package(:jar)
    package(:sources)
    package(:javadoc)

    package(:jar).enhance do |jar|
      jar.merge(artifact(:javapoet))
      jar.merge(artifact(:proton_core))
      jar.enhance do |f|
        shaded_jar = (f.to_s + '-shaded')
        Buildr.ant 'shade_jar' do |ant|
          artifact = Buildr.artifact(:shade_task)
          artifact.invoke
          ant.taskdef :name => 'shade', :classname => 'org.realityforge.ant.shade.Shade', :classpath => artifact.to_s
          ant.shade :jar => f.to_s, :uberJar => shaded_jar do
            ant.relocation :pattern => 'com.squareup.javapoet', :shadedPattern => 'arez.persist.processor.vendor.javapoet'
            ant.relocation :pattern => 'org.realityforge.proton', :shadedPattern => 'arez.persist.processor.vendor.proton'
          end
        end
        FileUtils.mv shaded_jar, f.to_s
      end
    end

    test.using :testng
    test.options[:properties] = { 'arez.persist.processor_fixture_dir' => _('src/test/fixtures') }
    test.compile.with :guiceyloops

    iml.test_source_directories << _('src/test/fixtures/input')
    iml.test_source_directories << _('src/test/fixtures/expected')
    iml.test_source_directories << _('src/test/fixtures/bad_input')
  end

  desc 'Arez Integration Tests'
  define 'integration-tests' do
    project.enable_annotation_processor = true

    test.options[:properties] = TEST_OPTIONS.merge('arez.persist.integration_fixture_dir' => _('src/test/resources'))
    test.options[:java_args] = ['-ea']

    test.using :testng
    test.compile.with :guiceyloops,
                      ELEMENTAL2_DEPS,
                      :arez_testng,
                      :javax_json,
                      project('core').package(:jar),
                      project('core').compile.dependencies,
                      project('processor').package(:jar),
                      project('processor').compile.dependencies

    # The generators are configured to generate to here.
    iml.test_source_directories << _('generated/processors/test/java')
  end

  doc.from(projects(%w(core processor))).
    using(:javadoc,
          :windowtitle => 'Arez-Persist API Documentation',
          :linksource => true,
          :timestamp => false,
          :link => %w(https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/ https://arez.github.io/api)
    )

  cleanup_javadocs(project, 'arez/persist')

  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => "-ea -Dbraincheck.environment=development -Darez.environment=development -Darez.persist.environment=development -Darez.persist.output_fixture_data=false -Darez.persist.processor_fixture_dir=processor/src/test/resources -Darez.persist.integration_fixture_dir=integration-tests/src/test/resources -Darez.persist.core.compile_target=target/arez-persist_core/idea/classes")

  ipr.add_testng_configuration('core',
                               :module => 'core',
                               :jvm_args => '-ea -Dbraincheck.environment=development -Darez.environment=development -Darez.persist.environment=development -Darez.persist.output_fixture_data=false -Darez.persist.core.compile_target=../target/arez-persist_core/idea/classes')
  ipr.add_testng_configuration('processor',
                               :module => 'processor',
                               :jvm_args => '-ea -Darez.persist.output_fixture_data=true -Darez.persist.fixture_dir=src/test/fixtures')
  ipr.add_testng_configuration('integration-tests',
                               :module => 'integration-tests',
                               :jvm_args => '-ea -Dbraincheck.environment=development -Darez.environment=development -Darez.persist.output_fixture_data=true -Darez.persist.integration_fixture_dir=src/test/resources')

  ipr.add_component_from_artifact(:idea_codestyle)

  ipr.nonnull_assertions = false

  ipr.add_component('JavacSettings') do |xml|
    xml.option(:name => 'ADDITIONAL_OPTIONS_STRING', :value => '-Xlint:all,-processing,-serial -Werror -Xmaxerrs 10000 -Xmaxwarns 10000')
  end

  ipr.add_component('JavaProjectCodeInsightSettings') do |xml|
    xml.tag!('excluded-names') do
      xml << '<name>com.sun.istack.internal.NotNull</name>'
      xml << '<name>com.sun.istack.internal.Nullable</name>'
      xml << '<name>org.jetbrains.annotations.Nullable</name>'
      xml << '<name>org.jetbrains.annotations.NotNull</name>'
      xml << '<name>org.testng.AssertJUnit</name>'
    end
  end
  ipr.add_component('NullableNotNullManager') do |component|
    component.option :name => 'myDefaultNullable', :value => 'javax.annotation.Nullable'
    component.option :name => 'myDefaultNotNull', :value => 'javax.annotation.Nonnull'
    component.option :name => 'myNullables' do |option|
      option.value do |value|
        value.list :size => '2' do |list|
          list.item :index => '0', :class => 'java.lang.String', :itemvalue => 'org.jetbrains.annotations.Nullable'
          list.item :index => '1', :class => 'java.lang.String', :itemvalue => 'javax.annotation.Nullable'
        end
      end
    end
    component.option :name => 'myNotNulls' do |option|
      option.value do |value|
        value.list :size => '2' do |list|
          list.item :index => '0', :class => 'java.lang.String', :itemvalue => 'org.jetbrains.annotations.NotNull'
          list.item :index => '1', :class => 'java.lang.String', :itemvalue => 'javax.annotation.Nonnull'
        end
      end
    end
  end
end
Buildr.projects.each do |project|
  project.doc.options.merge!('Xdoclint:all' => true)
end
