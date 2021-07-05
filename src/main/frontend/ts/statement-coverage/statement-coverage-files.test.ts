import {elementUpdated, fixture, html} from '@open-wc/testing';
import {StatementCoverageFiles} from './statement-coverage-files';

import {assert} from 'chai';

suite('coverage-files', () => {
  test('is defined', () => {
    const el = document.createElement('statement-coverage-files');
    assert.instanceOf(el, StatementCoverageFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <statement-coverage-files></statement-coverage-files>
    `)) as StatementCoverageFiles;

    assert.isNotNull(el.querySelector('.files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <statement-coverage-files></statement-coverage-files>
    `)) as StatementCoverageFiles;

    const model = {
      responseJSON: [
        {
          file: 'test',
          coverage: 0.6,
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
        'div.tabulator-cell[tabulator-field="coverage"]');
    assert.equal(elChild.textContent, '60%',
        elChild.outerHTML);
  });
});
