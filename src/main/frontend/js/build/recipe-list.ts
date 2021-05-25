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
      { title: "Build Performance",
        columns: [
          { title:"Cache", field:"cacheAvailability", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Recipe Violation", field:"recipeViolations", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1}
        ],
      },
      { title: "Code Quality",
        columns: [
          { title:"Comment", field:"comments", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Code Violation", field:"codeViolations", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Complexity", field:"complexity", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Duplication", field:"duplications", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Test", field:"test", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Coverage", field:"coverage", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1},
          { title:"Mutation Test", field:"mutationTest", hozAlign:"left",
            formatter: this.qualifierCellformatter.bind(this), sorter: this.compareCounter.bind(this), widthGrow:1}
        ]
      }
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

  private qualifierCellformatter(cell, formatterParams, onRendered) {

    var available = false
    var numerator = "N/A"
    var denominator = "N/A"
    var ratio = 0
    var qualified = "bg-na"
    
    if (!!cell.getValue()) {
      available = cell.getValue().available;
      numerator = cell.getValue().numerator;
      denominator = cell.getValue().denominator;
      ratio = cell.getValue().ratio * 100;
      qualified = cell.getValue().qualified ? "bg-pass" : "bg-fail";
    }

    if (available) {
      return `
      <div>N:${numerator} D:${denominator}</div>
      <span class="float-left">${ratio.toFixed(2)}% </span>
      <div class="progress">
        <div class="progress-bar ${qualified}"
          role="progressbar" style="width: ${ratio}%">
        </div>
      </div>
      `
    } else {
      return `<div>N/A</div>
      <span class="float-left">N/A</span>`
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

    // just trigging ajaxRequestFunc.  
    // url('meta-shift') has no meaning, because we replace ajaxRequestFunc.
    this.tabulatorTable.setData("meta-shift");
  }
}