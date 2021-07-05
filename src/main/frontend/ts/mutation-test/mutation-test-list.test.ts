import {elementUpdated, fixture, html} from '@open-wc/testing';
import {MutationTestList} from './mutation-test-list';

import {assert} from 'chai';

suite('mutation-test-list', () => {
  test('is defined', () => {
    const el = document.createElement('mutation-test-list');
    assert.instanceOf(el, MutationTestList);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <mutation-test-list></mutation-test-list>
    `)) as MutationTestList;

    assert.isNotNull(el.querySelector('.files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <mutation-test-list></mutation-test-list>
    `)) as MutationTestList;

    const model = {
      responseJSON: [
        {
          file: 'test',
          killed: 3,
          survived: 2,
          skipped: 1,
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
        'div.tabulator-cell[tabulator-field="killed"]');
    assert.equal(elChild.textContent, '3',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="survived"]');
    assert.equal(elChild.textContent, '2',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="skipped"]');
    assert.equal(elChild.textContent, '1',
        elChild.outerHTML);
  });
});
