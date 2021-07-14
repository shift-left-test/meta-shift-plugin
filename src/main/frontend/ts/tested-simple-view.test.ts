import {fixture, html} from '@open-wc/testing';
import {TestedSimpleView} from './tested-simple-view';

import {assert} from 'chai';

suite('tested-simple-view', () => {
  test('is defined', () => {
    const el = document.createElement('tested-simple-view');
    assert.instanceOf(el, TestedSimpleView);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <tested-simple-view></tested-simple-view>`
    )) as TestedSimpleView;

    assert.isNotNull(el.querySelector('div.board'),
        el.outerHTML);
  });

  test('property', async () => {
    const el = (await fixture(html`
        <tested-simple-view title="test"
          tested='3'
          recipes='10'
          delta='0.101010'
        ></tested-simple-view>`
    )) as TestedSimpleView;

    let elChild = el.querySelector('div.title');
    assert.include(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-number');
    assert.include(elChild.textContent, '30%',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-diff');
    assert.include(elChild.textContent, '10%',
        elChild.outerHTML);
  });

  test('property-no-recipes', async () => {
    const el = (await fixture(html`
        <tested-simple-view title="test"
          tested='0'
          recipes='0'
          delta='0'
        ></tested-simple-view>`
    )) as TestedSimpleView;

    let elChild = el.querySelector('div.title');
    assert.include(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-number');
    assert.include(elChild.textContent, '0%',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-diff');
    assert.include(elChild.textContent, '0%',
        elChild.outerHTML);
  });
});
