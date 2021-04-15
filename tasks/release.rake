require 'buildr/release_tool.rb')

Buildr::ReleaseTool.define_release_task do |t|
  t.extract_version_from_changelog
  t.zapwhite
  t.ensure_git_clean
  t.verify_no_todo
  t.build
  t.patch_changelog('arez/arez-persist')
  t.patch_maven_version_in_readme
  t.tag_project
  t.cleanup_staging
  t.stage_release(:release_to => { :url => 'https://stocksoftware.jfrog.io/stocksoftware/staging', :username => ENV['STAGING_USERNAME'], :password => ENV['STAGING_PASSWORD'] })
  t.maven_central_publish(:additional_tasks => 'site:publish_if_tagged')
  t.patch_changelog_post_release
  t.push_changes
  t.github_release('arez/arez-persist')
end
