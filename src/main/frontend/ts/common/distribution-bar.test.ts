import {fixture, html} from '@open-wc/testing';
import {DistributionBar} from './distribution-bar';

import {assert} from 'chai';

suite('distribution-bar', () => {
  test('is defined', () => {
    const el = document.createElement('distribution-bar');
    assert.instanceOf(el, DistributionBar);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <distribution-bar></distribution-bar>
    `)) as DistributionBar;

    assert.isNotNull(el.querySelector('.test-stats'),
        el.outerHTML);
  });

  test('itemlist', async () => {
    const distribution = {
      first: {ratio: 0.3, count: 100},
      second: {ratio: 0.7, count: 600},
    };
    const labels = [
      {clazz: 'valid-good', name: 'test'},
      {clazz: 'valid-bad', name: 'test2'},
    ];

    const el = (await fixture(html`
      <distribution-bar distribution="${JSON.stringify(distribution)}"
      labels="${JSON.stringify(labels)}"><distribution-bar>
    `)) as DistributionBar;

    let elChild = el.querySelector('div.label.valid-good');
    assert.include(elChild.textContent, 'test 30% (100)',
        elChild.outerHTML);

    elChild = el.querySelector('div.label.valid-bad');
    assert.include(elChild.textContent, 'test2 70% (600)',
        elChild.outerHTML);
  });
});
