/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {RecipeList} from './build/recipe-list';

import {UnitTestList}
  from './recipe/unit-test/unit-test-list';
import {StatementCoverageFiles}
  from './recipe/statement-coverage/statement-coverage-files';
import {StatementCoverageFileView}
  from './recipe/statement-coverage/statement-coverage-file-view';
import {BranchCoverageFiles}
  from './recipe/branch-coverage/branch-coverage-files';
import {BranchCoverageFileView}
  from './recipe/branch-coverage/branch-coverage-file-view';
import {MutationTestList}
  from './recipe/mutation-test/mutation-test-list';
import {MutationTestFileView}
  from './recipe/mutation-test/mutation-test-file-view';

import {BranchCoverageRecipes}
  from './build/branch-coverage/branch-coverage-recipes';
import {MutationTestRecipes}
  from './build/mutation-test/mutation-test-recipes';
import {StatementCoverageRecipes}
  from './build/statement-coverage/statement-coverage-recipes';
import {UnitTestRecipes}
  from './build/unit-test/unit-test-recipes';
import 'tabulator-tables/src/scss/tabulator.scss';
import '../scss/main.scss';

import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/js/all';

export {
  RecipeList,
  UnitTestList,
  StatementCoverageFiles,
  StatementCoverageFileView,
  BranchCoverageFiles,
  BranchCoverageFileView,
  MutationTestList,
  MutationTestFileView,

  BranchCoverageRecipes,
  MutationTestRecipes,
  StatementCoverageRecipes,
  UnitTestRecipes,

};
