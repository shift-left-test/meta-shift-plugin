import {customElement} from 'lit/decorators.js';
import {RecipeTreemap} from '../../common/recipe-treemap';

@customElement('unit-test-treemap')
/**
 * build recipe treemap.
 */
export class UnitTestTreemap extends RecipeTreemap {
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
              <div class="tooltip-qualified-key">Tests:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.total.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Passed:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.first.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Failed:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.second.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Error:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.third.toLocaleString()}
              </div>
            </div>
            <div>
              <div class="tooltip-qualified-key">Skipped:</div>
              <div class="tooltip-qualified-value">
                ${tooltipInfo.fourth.toLocaleString()}
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
