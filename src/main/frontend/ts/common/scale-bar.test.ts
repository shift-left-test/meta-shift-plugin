import {fixture, html} from '@open-wc/testing';
import {ScaleBar} from './scale-bar';

import {assert} from 'chai';

suite('scale-bar', () => {
  test('is defined', () => {
    const el = document.createElement('scale-bar');
    assert.instanceOf(el, ScaleBar);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <scale-bar></scale-bar>
    `)) as ScaleBar;

    assert.isNotNull(el.querySelector('.metrics-stats'),
        el.outerHTML);
  });

  test('statistics', async () => {
    const statistics = {
      min: 0.1,
      max: 0.9,
      average: 0.5,
      percent: true,
      available: true,
      scale: 0.4,
    };

    const el = (await fixture(html`
      <scale-bar statistics='${JSON.stringify(statistics)}'></scale-bar>
    `)) as ScaleBar;

    let elChild = el.querySelectorAll('div.label')[0];
    assert.include(elChild.textContent, 'Low:');
    assert.include(elChild.textContent, '10%');

    elChild = el.querySelectorAll('div.label')[1];
    assert.include(elChild.textContent, 'Average:');
    assert.include(elChild.textContent, '50%');

    elChild = el.querySelectorAll('div.label')[2];
    assert.include(elChild.textContent, 'High:');
    assert.include(elChild.textContent, '90%');

    assert.isNotNull(el.querySelector('div.pointer.scale'));
  });

  test('statistics-not available', async () => {
    const statistics = {
      min: 0.1,
      max: 0.9,
      average: 0.5,
      percent: true,
      available: false,
      scale: 0.4,
    };

    const el = (await fixture(html`
      <scale-bar statistics='${JSON.stringify(statistics)}'></scale-bar>
    `)) as ScaleBar;

    assert.isNull(el.querySelector('div.pointer.scale'));
  });
});
