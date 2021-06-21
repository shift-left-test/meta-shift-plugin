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

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <recipe-violation-files></recipe-violation-files>
    `)) as RecipeViolationFiles;

    const model = {
      responseJSON: {
        last_page: 1,
        data: [
          {
            file: 'test',
            major: 1,
            minor: 2,
            info: 3,
          },
        ],
      },
    };

    el.setAjaxFunc((page, size, sorters, callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="file"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="major"]');
    assert.equal(elChild.textContent, '1',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="minor"]');
    assert.equal(elChild.textContent, '2',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="info"]');
    assert.equal(elChild.textContent, '3',
        elChild.outerHTML);
  });
});
