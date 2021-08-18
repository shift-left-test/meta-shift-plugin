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

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <duplication-files></duplication-files>
    `)) as DuplicationFiles;

    const model = {
      responseJSON: [
        {
          name: 'test',
          linesOfCode: 100,
          first: 50,
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
        'div.tabulator-cell[tabulator-field="linesOfCode"]');
    assert.include(elChild.textContent, '100',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="first"]');
    assert.include(elChild.textContent, '50',
        elChild.outerHTML);
  });
});
