import {fixture, html} from '@open-wc/testing';
import {RecipeViolationFileView} from './recipe-violation-file-view';

import {assert} from 'chai';

suite('recipe-violation-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('recipe-violation-file-view');
    assert.instanceOf(el, RecipeViolationFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <recipe-violation-file-view></recipe-violation-file-view>
    `)) as RecipeViolationFileView;

    assert.isNotNull(el.querySelector('div#editor-panel'),
        el.outerHTML);
  });
});
