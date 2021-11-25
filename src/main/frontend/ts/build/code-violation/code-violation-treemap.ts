/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';
import {RecipeTreemap} from '../../common/recipe-treemap';
import {Utils} from '../../common/utils';

@customElement('code-violation-treemap')
/**
 * build recipe treemap.
 */
export class CodeViolationTreemap extends RecipeTreemap {
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
              <div class="tooltip-qualified-key">Issues:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.total.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Major:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.first.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Minor:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.second.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Info:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.third.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Density:</div>
              <div class="tooltip-qualified-value">
                ${Utils.toFixedFloor(tooltipInfo.ratio)}
              </div>
            </div>
          <div>`;
      }
    }
    return '';
  }
}
