const {legacyPlugin} = require('@web/dev-server-legacy');
const {puppeteerLauncher} = require('@web/test-runner-puppeteer');
const {defaultReporter} = require('@web/test-runner');
const {junitReporter} = require('@web/test-runner-junit-reporter');

const browsers = {
  puppeteer: puppeteerLauncher({launchOptions: {
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  }, concurrency: 1}),
};

module.exports = {
  rootDir: '.',
  files: ['./test-dist/**/*.test.js'],
  nodeResolve: true,
  preserveSymlinks: true,
  browsers: Object.values(browsers),
  testFramework: {
    // https://mochajs.org/api/mocha
    config: {
      ui: 'tdd',
    },
  },
  reporters: [
    defaultReporter({reportTestResults: false, reportTestProgress: true}),
    junitReporter({
      outputPath: '../../../target/frontend/test-results.xml',
      reportLogs: true,
    }),
  ],
  coverageConfig: {
    report: true,
    reportDir: '../../../target/frontend/coverage',
  },
  plugins: [
    // Detect browsers without modules (e.g. IE11) and transform to SystemJS
    // (https://modern-web.dev/docs/dev-server/plugins/legacy/).
    legacyPlugin({
      polyfills: {
        webcomponents: true,
        // Inject lit's polyfill-support module into test files, which is required
        // for interfacing with the webcomponents polyfills
        custom: [
          {
            name: 'lit-polyfill-support',
            path: 'node_modules/lit/polyfill-support.js',
            test:
              "!('attachShadow' in Element.prototype) || !('getRootNode' in Element.prototype) || window.ShadyDOM && window.ShadyDOM.force",
            module: false,
          },
        ],
      },
    }),
  ],
};
