import {fixture, html} from '@open-wc/testing';
import {RecipeTreemap} from './recipe-treemap';

import {assert} from 'chai';

suite('recipe-treemap', () => {
  test('is defined', () => {
    const el = document.createElement('recipe-treemap');
    assert.instanceOf(el, RecipeTreemap);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <recipe-treemap></recipe-treemap>
    `)) as RecipeTreemap;

    assert.isNotNull(el.querySelector('#treemap-chart'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <recipe-treemap></recipe-treemap>
    `)) as RecipeTreemap;

    const seriesData = [
      {name: 'test', link: 'test', path: 'test', value: [10, 10]},
    ];

    el.setAjaxFunc((callback) => {
      callback({
        responseJSON: {
          series: seriesData,
        },
      });
    });

    assert.deepInclude(el.getChartOption()['series'][0]['data'][0],
        seriesData[0], JSON.stringify(el.getChartOption()['series'][0]));
  });
});
