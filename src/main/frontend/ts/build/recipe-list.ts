import {html, LitElement} from 'lit';
import {customElement, query} from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';

@customElement('recipe-list')
/**
 * Recipe list element.
 */
export class RecipeList extends LitElement {
  @query('#recipes-table') recipesTable;

  private requestFileDetailFunc;
  private tabulatorTable: Tabulator;
  protected columns;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'Recipes', field: 'name',
        tooltip: true,
        widthGrow: 1},
      {title: 'Lines of Code', field: 'lines',
        formatter: this.lineOfCodeFormatter.bind(this)},
      {title: 'Premirror Cache', field: 'premirrorCache',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Shared State Cache', field: 'sharedStateCache',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Recipe Violations', field: 'recipeViolations',
        formatter: this.qualifierCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Comment', field: 'comments',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Code Violations', field: 'codeViolations',
        formatter: this.qualifierCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Complexity', field: 'complexity',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Duplications', field: 'duplications',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Unit Tests', field: 'test',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Statement Coverage', field: 'statementCoverage',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Branch Coverage', field: 'branchCoverage',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Mutation Tests', field: 'mutationTest',
        formatter: this.qualifierPercentCellformatter.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
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
   * called after first updated.
   */
  firstUpdated() : void {
    this.tabulatorTable = new Tabulator(
        this.recipesTable, {
          rowClick: this._handleRecipeClicked.bind(this),
          pagination: 'local',
          paginationSize: 10,
          layout: 'fitColumns', // fit columns to width of table (optional)
          columns: this.columns,
          tooltipsHeader: true,
        }
    );
  }

  /**
   * line of code formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  private lineOfCodeFormatter(cell) {
    return `<div class="locale-number">
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
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * sort function for qualifier.
   * @param {unknown} a
   * @param {unknown} b
   * @return {unknown}
   */
  private qualifierSorter(a, b) {
    return a.ratio - b.ratio;
  }

  /**
   * tootip for qualifier cell.
   * @param {unknown} cell
   * @return {unknown}
   */
  private qualifierTooltip(cell: any) {
    if (cell.getValue().available) {
      return cell.getValue().numerator.toLocaleString() +
        ' / ' +
        cell.getValue().denominator.toLocaleString();
    }

    return 'N/A';
  }

  /**
   * recipe click event handler.
   * @param {unknown} e
   * @param {unknown} row
   */
  private _handleRecipeClicked(e, row) {
    window.location.href = row.getData().name;
  }

  /**
   * set ajax func.
   * @param {unknown} requestFilesFunc
   * @param {unknown} requestFileDetailFunc
   */
  setAjaxFunc(requestFilesFunc: (callback) => void,
      requestFileDetailFunc = undefined) : void {
    const that = this;

    requestFilesFunc(function(model) {
      that.tabulatorTable.setData(model.responseJSON);
    });
    this.requestFileDetailFunc = requestFileDetailFunc;
  }
}
