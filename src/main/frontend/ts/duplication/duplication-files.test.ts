import {elementUpdated, fixture, html} from '@open-wc/testing';
import {DuplicationFiles} from './duplication-files';

import {assert} from 'chai';

suite('duplication-files', () => {
  test('is defined', () => {
    const el = document.createElement('duplication-files');
    assert.instanceOf(el, DuplicationFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <duplication-files></duplication-files>`
    )) as DuplicationFiles;

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <duplication-files></duplication-files>
    `)) as DuplicationFiles;

    const model = {
      responseJSON: {
        last_page: 1,
        data: [
          {
            file: 'test',
            lines: 100,
            duplicatedLines: 50,
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
        'div.tabulator-cell[tabulator-field="lines"]');
    assert.equal(elChild.textContent, '100',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="duplicatedLines"]');
    assert.equal(elChild.textContent, '50',
        elChild.outerHTML);
  });
});
