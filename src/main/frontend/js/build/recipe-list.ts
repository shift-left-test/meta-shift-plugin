import { html, css, LitElement } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';

import 'tabulator-tables/dist/css/tabulator.min.css';

@customElement('recipe-list')
export class RecipeList extends LitElement {
  @query("#recipes-table") recipesTable;

  private requestFilesFunc;
  private requestFileDetailFunc;
  private tabulatorTable;
  protected columns;

  constructor() {
    super();

    this.columns = [ //Define Table Columns
      { title:"Recipes", field:"name", widthGrow:1},
      { title:"Lines of Code", field: "codeSize",
        formatter: this.lineOfCodeFormatter.bind(this)},
      { title:"Premirror"},
      { title:"SharedState", field:"cacheAvailability", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Recipe Violations", field:"recipeViolations", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Comment", field:"comments", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Code Violations", field:"codeViolations", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Complexity", field:"complexity", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Duplications", field:"duplications", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Unit Tests", field:"test", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Coverage", field:"coverage", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
      { title:"Mutation Tests", field:"mutationTest", hozAlign:"left",
        formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1}
    ];
  }

  createRenderRoot() {
    return this;
  }

  render() {
    return html`<div id="recipes-table"></div>`;
  }

  updated() {
    this.tabulatorTable = new Tabulator(
      this.recipesTable, {
      rowClick: this._handleRecipeClicked.bind(this),
      pagination: "remote",
      paginationSize: 10,
      ajaxRequestFunc: this._handleAjaxRequest.bind(this),
      ajaxSorting: true,
      layout: "fitColumns", //fit columns to width of table (optional)
      columns: this.columns
    });
  }

  private lineOfCodeFormatter(cell, formatterParams, onRendered) {
    return `<div>${cell.getValue().lines.toLocaleString()}</div>`
  }

  private qualifierCellformatter(cell, formatterParams, onRendered) {

    var available = false
    var numerator = "N/A"
    var denominator = "N/A"
    var ratio = 0
    
    if (!!cell.getValue()) {
      available = cell.getValue().available;
      numerator = cell.getValue().numerator;
      denominator = cell.getValue().denominator;
      ratio = cell.getValue().ratio * 100;
    }

    if (available) {
      return `
      <div class="progress">
        <div class="progress-bar"
          role="progressbar" style="width: ${ratio}%">
          ${ratio.toFixed(2)}% 
        </div>
      </div>
      `
    } else {
      return `<div>--</div>`
    }
  }

  private compareCounter(a, b, aRow, bRow, column, dir, sorterParams) {
    return a.ratio - b.ratio;
  }

  private _handleAjaxRequest(url, config, params) {
    var self = this;
    return new Promise(function (resolve, reject) {
      self.requestFilesFunc(params.page, params.size, function (model) {
        console.log(model.responseJSON);
        resolve(model.responseJSON);
      });
    });
  }

  private _handleRecipeClicked(e, row) {
    console.log(row.getData().name);
    window.location.href = row.getData().name;
  }

  setAjaxFunc(requestFilesFunc, requestFileDetailFunc = undefined) {
    this.requestFilesFunc = requestFilesFunc;
    this.requestFileDetailFunc = requestFileDetailFunc;

    // just triggering ajaxRequestFunc.
    // url('meta-shift') has no meaning, because we replace ajaxRequestFunc.
    this.tabulatorTable.setData("meta-shift");
  }
}