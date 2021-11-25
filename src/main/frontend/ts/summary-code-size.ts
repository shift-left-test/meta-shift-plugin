/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('summary-code-size')
/**
 * summary code size.
 */
export class SummaryCodeSize extends LitElement {
  @property() codeSize
  @property() testedRecipes

  /**
   * constructor.
   */
  constructor() {
    super();

    this.codeSize = '{}';
  }

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    const codeSizeJson = JSON.parse(this.codeSize);
    const testedRecipesJson = JSON.parse(this.testedRecipes);

    return html`
    <b>Recipes</b>: ${codeSizeJson.recipes.toLocaleString()}  \
    (<b>Tested</b>: ${testedRecipesJson.numerator.toLocaleString()}) &nbsp;
    <b>Lines</b>: ${codeSizeJson.lines.toLocaleString()}  &nbsp;
    <b>Functions</b>: ${codeSizeJson.functions.toLocaleString()}  &nbsp;
    <b>Classes</b>: ${codeSizeJson.classes.toLocaleString()}  &nbsp;
    <b>Files</b>: ${codeSizeJson.files.toLocaleString()}  &nbsp;
    `;
  }
}
