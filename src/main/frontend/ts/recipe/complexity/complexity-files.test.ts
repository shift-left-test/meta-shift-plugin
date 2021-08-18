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

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <complexity-files></complexity-files>
    `)) as ComplexityFiles;

    const model = {
      responseJSON: [
        {
          name: 'test',
          second: 10,
          first: 20,
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
        'div.tabulator-cell[tabulator-field="second"]');
    assert.include(elChild.textContent, '10',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="first"]');
    assert.include(elChild.textContent, '20',
        elChild.outerHTML);
  });
});
