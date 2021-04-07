function renderRecipesTable(tableDivId, buildAction) {
  function qualifierCellformatter(cell, formatterParams, onRendered) {

    var numerator = "N/A"
    var denominator = "N/A"
    var ratio = 0
    var qualified = "bg-warning"
    
    if (!!cell.getValue()) {
      numerator = cell.getValue().numerator;
      denominator = cell.getValue().denominator;
      ratio = cell.getValue().ratio * 100;
      qualified = cell.getValue().qualified ? "bg-success" : "bg-danger";
    }

    return `
    <div>N:${numerator} D:${denominator}</div>
    <span class="float-left">${ratio}% </span>
    <div class="progress">
      <div class="progress-bar ${qualified}"
        role="progressbar" style="width: ${ratio}%">
      </div>
    </div>
    `
  }

  function compareCounter(a, b, aRow, bRow, column, dir, sorterParams) {
    return a.ratio - b.ratio;
  }

  var table = new Tabulator(tableDivId,
    {
      //height:500, // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
      layout:"fitColumns", //fit columns to width of table (optional)
      columns:[ //Define Table Columns
        { title:"Recipes", field:"name", widthGrow:1},
        { title: "Build Performance",
          columns: [
            { title:"Cache", field:"cache", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Recipe Violation", field:"recipeViolation", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1}
          ],
        },
        { title: "Code Quality",
          columns: [
            { title:"Comment", field:"comment", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Code Violation", field:"codeViolation", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Complexity", field:"complexity", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Duplication", field:"duplication", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Test", field:"test", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Coverage", field:"coverage", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1},
            { title:"Mutation Test", field:"mutationTest", hozAlign:"left",
              formatter: qualifierCellformatter, sorter: compareCounter, widthGrow:1}
          ]
        }
      ]
    });

  buildAction.getRecipesTableModel(function (model) {
    table.setData(model.responseJSON);
    table.setSort([{column:"name", dir:"asc"}])
  });
}