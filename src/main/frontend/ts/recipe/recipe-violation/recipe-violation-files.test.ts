import {elementUpdated, fixture, html} from '@open-wc/testing';
import {RecipeViolationFiles} from './recipe-violation-files';

import {assert} from 'chai';

suite('recipe-violation-files', () => {
  test('is defined', () => {
    const el = document.createElement('recipe-violation-files');
    assert.instanceOf(el, RecipeViolationFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <recipe-violation-files></recipe-violation-files>
    `)) as RecipeViolationFiles;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <recipe-violation-files></recipe-violation-files>
    `)) as RecipeViolationFiles;

    const model = {
      responseJSON: [
        {
          name: 'test',
          linesOfCode: 100,
          total: 10,
          first: 1,
          second: 2,
          third: 3,
          ratio: 0.5,
          qualified: true,
        },
      ],
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
        'div.tabulator-cell[tabulator-field="first"]');
    assert.include(elChild.textContent, '1',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="second"]');
    assert.include(elChild.textContent, '2',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="third"]');
    assert.include(elChild.textContent, '3',
        elChild.outerHTML);
  });
});
