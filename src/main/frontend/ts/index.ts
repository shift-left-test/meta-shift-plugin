/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {BuildTreemap} from './build/build-treemap';
import {RecipeList} from './build/recipe-list';
import {BuildTrendChart} from './build/build-trend-chart';

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
import {BranchCoverageTreemap}
  from './build/branch-coverage/branch-coverage-treemap';
import {MutationTestRecipes}
  from './build/mutation-test/mutation-test-recipes';
import {MutationTestTreemap}
  from './build/mutation-test/mutation-test-treemap';
import {StatementCoverageRecipes}
  from './build/statement-coverage/statement-coverage-recipes';
import {StatementCoverageTreemap}
  from './build/statement-coverage/statement-coverage-treemap';
import {UnitTestRecipes}
  from './build/unit-test/unit-test-recipes';
import {UnitTestTreemap}
  from './build/unit-test/unit-test-treemap';
import 'tabulator-tables/src/scss/tabulator.scss';
import '../scss/main.scss';

import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/js/all';

export {
  BuildTreemap,
  RecipeList,
  BuildTrendChart,
  UnitTestList,
  StatementCoverageFiles,
  StatementCoverageFileView,
  BranchCoverageFiles,
  BranchCoverageFileView,
  MutationTestList,
  MutationTestFileView,

  BranchCoverageRecipes,
  BranchCoverageTreemap,
  MutationTestRecipes,
  MutationTestTreemap,
  StatementCoverageRecipes,
  StatementCoverageTreemap,
  UnitTestRecipes,
  UnitTestTreemap,

};
