/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {elementUpdated, fixture, html} from '@open-wc/testing';
import {RecipeList} from './recipe-list';

import {assert} from 'chai';

suite('recipe-list', () => {
  test('is defined', () => {
    const el = document.createElement('recipe-list');
    assert.instanceOf(el, RecipeList);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <recipe-list></recipe-list>`
    )) as RecipeList;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <recipe-list></recipe-list>
    `)) as RecipeList;

    const model = {
      responseJSON: [{
        name: 'test',
        unitTests: {available: true, ratio: 0.9,
          numerator: 10, denominator: 100},
        statementCoverage: {available: true, ratio: 1.0,
          numerator: 10, denominator: 100},
        branchCoverage: {available: true, ratio: 0.99,
          numerator: 10, denominator: 100},
        mutationTests: {available: true, ratio: 0.11,
          numerator: 10, denominator: 100},
      }],
    };

    el.setAjaxFunc((callback) => {
      callback(model);
    });

    await elementUpdated(el);

    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="name"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="unitTests"]');
    assert.include(elChild.textContent, '90%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="statementCoverage"]');
    assert.include(elChild.textContent, '100%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="branchCoverage"]');
    assert.include(elChild.textContent, '99%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="mutationTests"]');
    assert.include(elChild.textContent, '11%',
        elChild.outerHTML);
  });

  test('setAjaxFunc-n/a', async () => {
    const el = (await fixture(html`
      <recipe-list></recipe-list>
    `)) as RecipeList;

    const model = {
      responseJSON: [{
        name: 'test',
        unitTests: {available: false, ratio: 0.9,
          numerator: 10, denominator: 100},
        statementCoverage: {available: true, ratio: 1.0,
          numerator: 10, denominator: 100},
        branchCoverage: {available: true, ratio: 0.99,
          numerator: 10, denominator: 100},
        mutationTests: {available: false, ratio: 0.11,
          numerator: 10, denominator: 100},
      }],
    };

    el.setAjaxFunc((callback) => {
      callback(model);
    });

    await elementUpdated(el);

    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="name"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="unitTests"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="statementCoverage"]');
    assert.include(elChild.textContent, '100%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="branchCoverage"]');
    assert.include(elChild.textContent, '99%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="mutationTests"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);
  });
});
