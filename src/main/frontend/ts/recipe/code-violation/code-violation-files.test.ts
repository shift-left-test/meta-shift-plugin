import {elementUpdated, fixture, html} from '@open-wc/testing';
import {CodeViolationFiles} from './code-violation-files';

import {assert} from 'chai';

suite('code-violation-files', () => {
  test('is defined', () => {
    const el = document.createElement('code-violation-files');
    assert.instanceOf(el, CodeViolationFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <code-violation-files></code-violation-files>
    `)) as CodeViolationFiles;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <code-violation-files></code-violation-files>
    `)) as CodeViolationFiles;

    const model = {
      responseJSON: [
        {
          name: 'test',
          first: 1,
          second: 2,
          third: 3,
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
