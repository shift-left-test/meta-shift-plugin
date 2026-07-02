/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {UnitTestList}
  from './recipe/unit-test/unit-test-list';
import {StatementCoverageFileView}
  from './recipe/statement-coverage/statement-coverage-file-view';
import {BranchCoverageFileView}
  from './recipe/branch-coverage/branch-coverage-file-view';
import {MutationTestFileView}
  from './recipe/mutation-test/mutation-test-file-view';

import 'tabulator-tables/src/scss/tabulator.scss';
import '../scss/main.scss';

import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/js/all';

export {
  UnitTestList,
  StatementCoverageFileView,
  BranchCoverageFileView,
  MutationTestFileView,

};
