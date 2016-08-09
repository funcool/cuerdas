# Changelog

## Version 1.0.0

Date: Unreleased

Important: this is a major release and includes many *breaking changes*
among other fixes and improvements. This is a complete list of changes:

- Rename old functions such as `dasherize`, `underscore`, and `classify` with
  more consistent with clojure naming: `kebab`, `snake` and `pascal`. The new
  `kebab` function is no longer deals with css selectors transformations, the
  `js-selector` and `css-selector` functions are added for this purpose.
  (Please, see more info in PR #33)
- Rename `parse-long` to `parse-int` in jvm impl (make the clj and cljs impl
  equivalent).
- Rename `parse-float` to `parse-double` in cljs impl (the second arity is
  removed and now clj and cljs impl are equivalent).
- Make `parse-number` cross-platform (and remove the second arity that used for
  specify the precision).
- Fix wrong behavior on `replace` function in cljs.
- Remove ireplace-first function (it was only on the cljs).
- Remove ireplace function (it was only on the cljs).
- Enable `escape-html` and `unescape-html` for clj.
- Fix `replace` impl on cljs and make it consistent with clj version.
- Add string interpolation support.


## Version 0.8.0

Date: 2016-06-25

- Fix NPE on `format` function.
- Improve string interpolation on `format` function making it more flexible.


## Version 0.7.2

Date: 2016-04-12

- Fix null pointer exception on format function without arguments (clj only).


## Version 0.7.1

Date: 2015-12-03

- Fix unexpected behavior of prune.
- Update cljs compiler version.
- Minor cosmetic fixes to code.


## Version 0.7.0

Date: 2015-11-30

- Start using reader conditionals (clojure >= 1.7 required now)
- starts-with? and ends-with? and contains? return true for empty sub-string
- General bugfixing
- Add varios new functions such as `to-bool`, `words`, `alpha?`, among others
  (thanks to @jelz).


## Version 0.6.0

Date: 2015-08-01

- Update cljs compiler to 1.7.28
- Start using cljs own compiler facilities instead of lein-cljsbuild.
- Remove cljx dependency and start using reader conditionals for tests.
- Unify the source under one directory (instead of have two: cljs and clj).


## Version 0.5.0

Date: 2015-06-21

- Add substr-between function (thanks to @podviaznikov)
- Add some additional type hints on clojure code.


## Version 0.4.0

Date: 2015-03-30

- Changed the groupId of the package to funcool.


## Version 0.3.2

Date: 2015-03-30

- Add unlines function (thanks to @pepegar)
- Replace speclj with clojure.test.
- Update dependencies.


## Version 0.3.1

Date: 2015-03-14

- Remove clojure and clojurescript from required dependencies.
- Add some additional testcases.

## Version 0.3.0

Date: 2015-01-13

- Fixed bug on contains? function (thanks @podviaznikov for report)
- Fixed bug on dasherize function (thanks to @NoamB for improved version)
- Add strip-suffix and strip-prefix functions.


## Version 0.2.0

Date: 2015-01-05

- Remove Apache Commons Lang dependency (clj).
- Add proper nil handling for almost all functions.
- Add strip, rstrip and lstrip aliases for trim, rtrim and ltrim respectively.
- Rename endswith? and startswith? with ends-with? and starts-with?
  respectively,   but conserve the previously funcnames as aliases.
- Add the ability of arbitrary replacements to strip-tags function.


## Version 0.1.0

Date: 2014-12-23

- Initial version
