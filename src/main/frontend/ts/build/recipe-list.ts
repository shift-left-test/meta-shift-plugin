import {html, LitElement} from 'lit';
import {customElement, query} from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';

@customElement('recipe-list')
/**
 * Recipe list element.
 */
export class RecipeList extends LitElement {
  @query('#recipes-table') recipesTable;

  private requestFilesFunc;
  private requestFileDetailFunc;
  private tabulatorTable: Tabulator;
  protected columns;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'Recipes', field: 'name', widthGrow: 1},
      {title: 'Lines of Code', field: 'lines',
        formatter: this.lineOfCodeFormatter.bind(this)},
      {title: 'Premirror Cache', field: 'premirrorCache', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Shared State Cache', field: 'sharedStateCache', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Recipe Violations', field: 'recipeViolations', hozAlign: 'left',
        formatter: this.qualifierCellformatter.bind(this), widthGrow: 1},
      {title: 'Comment', field: 'comments', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Code Violations', field: 'codeViolations', hozAlign: 'left',
        formatter: this.qualifierCellformatter.bind(this), widthGrow: 1},
      {title: 'Complexity', field: 'complexity', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Duplications', field: 'duplications', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Unit Tests', field: 'test', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Coverage', field: 'coverage', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
      {title: 'Mutation Tests', field: 'mutationTest', hozAlign: 'left',
        formatter: this.qualifierPercentCellformatter.bind(this), widthGrow: 1},
    ];
  }

  /**
   * create render root.
   * @return {LitElement}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    return html`<div id="recipes-table"></div>`;
  }

  /**
   * called after updated.
   */
  updated() : void {
    this.tabulatorTable = new Tabulator(
        this.recipesTable, {
          rowClick: this._handleRecipeClicked.bind(this),
          pagination: 'remote',
          paginationSize: 10,
          ajaxRequestFunc: this._handleAjaxRequest.bind(this),
          ajaxSorting: true,
          layout: 'fitColumns', // fit columns to width of table (optional)
          columns: this.columns,
        }
    );
  }

  /**
   * line of code formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  private lineOfCodeFormatter(cell) {
    return `<div class="progress-bar-legend">
      ${cell.getValue().toLocaleString()}</div>`;
  }

  /**
   * qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  private qualifierCellformatter(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio;
    }

    if (available) {
      return `
      <div class="progress-bar-legend">
          ${ratio.toFixed(2)}
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  private qualifierPercentCellformatter(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio * 100;
    }

    if (available) {
      return `
      <div class="progress">
        <div class="progress-bar"
          role="progressbar" style="width: ${ratio}%">
        </div>
        <div class="progress-bar-legend">${Math.floor(ratio)}% </div>
      </div>
      `;
    } else {
      return `<div>N/A</div>`;
    }
  }

  /**
   * ajax request function.
   * @param {unknown} url
   * @param {unknown} config
   * @param {unknown} params
   * @return {Promise}
   */
  private _handleAjaxRequest(url, config, params) {
    const that = this;
    return new Promise(function(resolve, ) {
      that.requestFilesFunc(params.page, params.size, params.sorters,
          function(model) {
            console.log(model.responseJSON);
            resolve(model.responseJSON);
          }
      );
    });
  }

  /**
   * recipe click event handler.
   * @param {unknown} e
   * @param {unknown} row
   */
  private _handleRecipeClicked(e, row) {
    console.log(row.getData().name);
    window.location.href = row.getData().name;
  }

  /**
   * set ajax func.
   * @param {unknown} requestFilesFunc
   * @param {unknown} requestFileDetailFunc
   */
  setAjaxFunc(requestFilesFunc: (page, size, sorters, callback) => void,
      requestFileDetailFunc = undefined) : void {
    this.requestFilesFunc = requestFilesFunc;
    this.requestFileDetailFunc = requestFileDetailFunc;

    // just triggering ajaxRequestFunc.
    // url('meta-shift') has no meaning, because we replace ajaxRequestFunc.
    this.tabulatorTable.setData('meta-shift');
  }
}
