import {elementUpdated, fixture, html} from '@open-wc/testing';
import {CommentFiles} from './comment-files';

import {assert} from 'chai';

suite('comment-files', () => {
  test('is defined', () => {
    const el = document.createElement('comment-files');
    assert.instanceOf(el, CommentFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <comment-files></comment-files>`
    )) as CommentFiles;

    assert.isNotNull(el.querySelector('.files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
     <comment-files></comment-files>
    `)) as CommentFiles;

    const model = {
      responseJSON: [
        {
          file: 'test',
          ratio: 0.1,
          lines: 20,
          commentLines: 2,
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
        'div.tabulator-cell[tabulator-field="ratio"]');
    assert.equal(elChild.textContent, '10%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="lines"]');
    assert.equal(elChild.textContent, '20',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="commentLines"]');
    assert.equal(elChild.textContent, '2',
        elChild.outerHTML);
  });
});
