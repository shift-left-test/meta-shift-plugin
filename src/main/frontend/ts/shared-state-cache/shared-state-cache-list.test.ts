import {elementUpdated, fixture, html} from '@open-wc/testing';
import {SharedStateCacheList} from './shared-state-cache-list';

import {assert} from 'chai';

suite('shared-state-cache-list', () => {
  test('is defined', () => {
    const el = document.createElement('shared-state-cache-list');
    assert.instanceOf(el, SharedStateCacheList);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <shared-state-cache-list></shared-state-cache-list>
    `)) as SharedStateCacheList;

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <shared-state-cache-list></shared-state-cache-list>
    `)) as SharedStateCacheList;

    const model = {
      responseJSON: [
        {
          signature: 'test',
          available: true,
        },
      ],
    };

    el.setAjaxFunc((callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="signature"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="available"]');
    assert.equal(elChild.textContent, 'true',
        elChild.outerHTML);
  });
});
