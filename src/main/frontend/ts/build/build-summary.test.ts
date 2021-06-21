import {fixture, html} from '@open-wc/testing';
import {BuildSummary} from './build-summary';

import {assert} from 'chai';

suite('build-summary', () => {
  test('is defined', () => {
    const el = document.createElement('build-summary');
    assert.instanceOf(el, BuildSummary);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <build-summary title="test"></build-summary>
    `)) as BuildSummary;

    assert.equal(el.querySelector('div.title').textContent, 'test');
  });

  test('metricsValue', async () => {
    const el = (await fixture(html`
      <build-summary title="test"
      metricsValue='{
        "available": true, 
        "qualified": true,
        "ratio": 0.70332030
      }'></build-summary>
    `)) as BuildSummary;

    assert.equal(el.querySelector('div.ratio').textContent, '0.70');
    assert.isNull(el.querySelector('div.progress'));
  });

  test('metricsValue-percent', async () => {
    const el = (await fixture(html`
      <build-summary class="percent" title="test"
      metricsValue='{
        "available": true, 
        "qualified": true,
        "ratio": 0.70332030
      }'></build-summary>
    `)) as BuildSummary;

    assert.equal(el.querySelector('div.ratio').textContent, '70%');
    assert.isNotNull(el.querySelector('div.progress'));
  });

  test('metricsValue-na', async () => {
    const el = (await fixture(html`
      <build-summary class="percent" title="test"
      metricsValue='{
        "available": false, 
        "qualified": true,
        "ratio": 0.70332030
      }'></build-summary>
    `)) as BuildSummary;

    assert.equal(el.querySelector('div.ratio').textContent, 'N/A');
    assert.isNull(el.querySelector('div.progress'));
  });
});
