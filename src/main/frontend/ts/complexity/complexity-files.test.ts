import {elementUpdated, fixture, html} from '@open-wc/testing';
import {ComplexityFiles} from './complexity-files';

import {assert} from 'chai';

suite('complexity-files', () => {
  test('is defined', () => {
    const el = document.createElement('complexity-files');
    assert.instanceOf(el, ComplexityFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <complexity-files></complexity-files>
    `)) as ComplexityFiles;

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <complexity-files></complexity-files>
    `)) as ComplexityFiles;

    const model = {
      responseJSON: {
        last_page: 1,
        data: [
          {
            file: 'test',
            functions: 10,
            complexFunctions: 20,
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
        'div.tabulator-cell[tabulator-field="functions"]');
    assert.equal(elChild.textContent, '10',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="complexFunctions"]');
    assert.equal(elChild.textContent, '20',
        elChild.outerHTML);
  });
});
