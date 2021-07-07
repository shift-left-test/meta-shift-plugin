const path = require('path')
const rewrite = require('express-urlrewrite')
const glob = require('glob')

let entry = __dirname + '/ts/index.ts';
let outputPath = path.resolve(__dirname + '/../webapp/js');
let filename = 'bundle.js';
let tsloader = ['ts-loader'];

if (process.env.TESTBUILD) {
  entry = glob.sync(__dirname + '/ts/**/*.test.ts');
  outputPath = path.resolve(__dirname + '/test-dist/');
  filename = 'bundle.test.js';
  tsloader = ['@jsdevtools/coverage-istanbul-loader',
  'ts-loader'];
}

module.exports = {
  entry: entry,
  output: {
    path: outputPath,
    filename: filename,
    clean: true,
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader'],
      },
      {
        test: /\.(ts|tsx)$/,
        use: tsloader,
        exclude: /node_modules/,
      },
      {
        test: /\.(css|scss)$/,
        use: [
          'style-loader',
          'css-loader',
          'sass-loader',
        ],
      },
      {
        test: /\.(png|svg|jpg|jpeg|gif)$/i,
        use: [
          'file-loader',
        ],
      },
      {
        test: /\.(woff|woff2|eot|ttf|otf)$/i,
        use: [{
          loader: 'file-loader',
          options: {
            name: '[name].[ext]',
            outputPath: 'fonts/',
          },
        }],
      },
    ],
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
    fallback: {
      crypto: require.resolve('crypto-browserify'),
      stream: require.resolve('stream-browserify'),
    },
  },
  plugins: [
  ],
  devServer: {
    before: function(app, server, compiler) {
      app.use(rewrite(/\/jenkins\/static\/.*\/plugin\/metashift\/js\/(.*)/, '/metashift/js/$1'));
    },
    proxy: {
      '/jenkins': {
        target: 'http://localhost:8080',
      },
    },
    contentBase: path.join(__dirname, '../webapp/js'),
    publicPath: '/metashift/js/',
    hot: true,
    compress: true,
    host: '0.0.0.0',
    port: 9000,
  },
  mode: 'development',
};
