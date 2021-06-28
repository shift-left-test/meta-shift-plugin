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

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <code-violation-files></code-violation-files>
    `)) as CodeViolationFiles;

    const model = {
      responseJSON: [
        {
          file: 'test',
          major: 1,
          minor: 2,
          info: 3,
        },
      ],
    };

    el.setAjaxFunc((callback) => {
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
