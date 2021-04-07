function renderTrendChart(chartDivId, buildAction) {
  var trendChart = echarts.init(document.getElementById(chartDivId));

  buildAction.getTrendChartModel(function(model) {
    var option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: model.responseJSON['legend']
      },
      xAxis: {
        type: 'category',
        data: model.responseJSON['builds']
      },
      yAxis: {
        type: 'value'
      },
      series: model.responseJSON['series']
  };

    trendChart.setOption(option);
  });
}