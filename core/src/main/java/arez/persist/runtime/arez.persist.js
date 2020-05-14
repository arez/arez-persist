/**
 * This file provides the @defines for arez configuration options.
 * See ArezConfig.java for details.
 */
goog.provide('arez.persist');

/** @define {string} */
arez.environment = goog.define('arez.persist.environment', 'production');

/** @define {string} */
arez.check_api_invariants = goog.define('arez.persist.check_api_invariants', 'false');

/** @define {string} */
arez.logger = goog.define('arez.persist.logger', 'none');
