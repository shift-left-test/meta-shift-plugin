/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';
import {RecipeTreemap} from '../../common/recipe-treemap';

@customElement('statement-coverage-treemap')
/**
 * build recipe treemap.
 */
export class StatementCoverageTreemap extends RecipeTreemap {
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
              <div class="tooltip-qualified-key">Statements:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.total.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Covered:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.first.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Uncovered:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.second.toLocaleString()}
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
