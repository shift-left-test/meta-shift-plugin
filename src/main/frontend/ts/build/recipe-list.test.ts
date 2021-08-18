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
        linesOfCode: 10,
        premirrorCache: {available: true, ratio: 0.20,
          numerator: 10, denominator: 100},
        sharedStateCache: {available: true, ratio: 0.3011,
          numerator: 10, denominator: 100},
        recipeViolations: {available: true, ratio: 0.4000,
          numerator: 10, denominator: 100},
        comments: {available: true, ratio: 0.509,
          numerator: 10, denominator: 100},
        codeViolations: {available: true, ratio: 0.60455,
          numerator: 10, denominator: 100},
        complexity: {available: true, ratio: 0.700001,
          numerator: 10, denominator: 100},
        duplications: {available: true, ratio: 0.80001,
          numerator: 10, denominator: 100},
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
        'div.tabulator-cell[tabulator-field="linesOfCode"]');
    assert.include(elChild.textContent, '10',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="premirrorCache"]');
    assert.include(elChild.textContent, '20%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="sharedStateCache"]');
    assert.include(elChild.textContent, '30%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="recipeViolations"]');
    assert.include(elChild.textContent, '0.40',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="comments"]');
    assert.include(elChild.textContent, '50%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="codeViolations"]');
    assert.include(elChild.textContent, '0.60',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="complexity"]');
    assert.include(elChild.textContent, '70%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="duplications"]');
    assert.include(elChild.textContent, '80%',
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
        linesOfCode: 10,
        premirrorCache: {available: false, ratio: 0.20,
          numerator: 10, denominator: 100},
        sharedStateCache: {available: false, ratio: 0.3011,
          numerator: 10, denominator: 100},
        recipeViolations: {available: false, ratio: 0.4000,
          numerator: 10, denominator: 100},
        comments: {available: false, ratio: 0.509,
          numerator: 10, denominator: 100},
        codeViolations: {available: false, ratio: 0.60455,
          numerator: 10, denominator: 100},
        complexity: {available: false, ratio: 0.700001,
          numerator: 10, denominator: 100},
        duplications: {available: false, ratio: 0.80001,
          numerator: 10, denominator: 100},
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
        'div.tabulator-cell[tabulator-field="linesOfCode"]');
    assert.include(elChild.textContent, '10',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="premirrorCache"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="sharedStateCache"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="recipeViolations"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="comments"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="codeViolations"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="complexity"]');
    assert.include(elChild.textContent, 'N/A',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="duplications"]');
    assert.include(elChild.textContent, 'N/A',
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
