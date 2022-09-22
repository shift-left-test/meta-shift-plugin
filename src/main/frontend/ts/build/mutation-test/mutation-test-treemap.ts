/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';
import {RecipeTreemap} from '../../common/recipe-treemap';

@customElement('mutation-test-treemap')
/**
 * build recipe treemap.
 */
export class MutationTestTreemap extends RecipeTreemap {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.recipeRoot = '../';
  }

  /**
   * tooltip generator.
   * @param {unknown} info
   * @return {unknown}
   */
  generateTooltip(info) {
    if (this.tooltipInfos !== undefined) {
      const tooltipInfo = this.tooltipInfos.find(
          (o) => o.name == info.data.name);

      if (tooltipInfo !== undefined) {
        return `<div class="tooltip-title">
          ${info.name}</div>
          <div class="tooltip-body">
            <div>
              <div class="tooltip-qualified-key">Lines of Code:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.linesOfCode.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Tests:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.total.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Killed:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.first.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Survived:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.second.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Skipped:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.third.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Ratio:</div>
              <div class="tooltip-qualified-value">
                ${Math.floor(tooltipInfo.ratio * 100)}%
              </div>
            </div>
          <div>`;
      }
    }
    return '';
  }
}
