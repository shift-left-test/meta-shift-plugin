
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const path = require('path')
const rewrite = require('express-urlrewrite')

module.exports = {
  entry: './src/front/js/index.js',
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader'],
      },
      {
        test: /\.(ts|tsx)$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.(css|scss)$/,
        use: [
          'style-loader',
          'css-loader',
          'sass-loader'
        ]
      },
      {
        test: /\.ttf$/,
        use: [
          'file-loader'
        ]
      }
    ]
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },
  output: {
    path: path.resolve(__dirname, 'src/main/webapp/js'),
    filename: 'bundle.js',
    library: 'metashift'
  },
  plugins: [
    new MonacoWebpackPlugin()
  ],
  devServer: {
    before: function (app, server, compiler) {
      app.use(rewrite(/\/jenkins\/static\/.*\/plugin\/metashift\/js\/(.*)/, '/metashift/js/$1'));
    },
    proxy: {
      '/jenkins': {
        target: 'http://localhost:8080'
      }
    },
    contentBase: path.join(__dirname, 'src/main/webapp/js'),
    publicPath: '/metashift/js/',
    hot: true,
    compress: true,
    host: '0.0.0.0',
    port: 9000
  },
  mode: "development"
}