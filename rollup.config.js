import {babel} from '@rollup/plugin-babel';
import {nodeResolve} from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import replace from '@rollup/plugin-replace';

const plugins = [
  replace({
    'process.env.NODE_ENV': JSON.stringify('production'),
    preventAssignment: true
  }),

  babel({
    exclude: 'node_modules/**',
    sourceMap: false,
    babelHelpers: 'bundled'
  }),

  nodeResolve({
    mainFields: ['module', 'main'],
    // preferBuiltins: false,
    browser: true
  }),

  commonjs({
    include: 'node_modules/**',  // Default: undefined
    // if true then uses of `global` won't be dealt with by this plugin
    ignoreGlobal: false,  // Default: false
    sourceMap: false,  // Default: true
  }),
  // globals(),
];

export default [{
  input: "./assets/xregexp/xregexp.js",
  output: {
    file: './assets/xregexp/xregexp.bundle.js',
    compact: true,
    format: 'iife',
    indent: true,
    name: "XRegExp",
    exports: "default"
  },
  plugins: plugins
}];
