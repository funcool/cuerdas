# Changelog

## Version 2022.06.13-401

- Handle `nils` on concat macro.


## Version 2022.03.27-397

- Add `concat` macro, for faster string concatenation on CLJS.
- Add `ffmt` macro, a simplier and faster string formating
  macro. Alternative to the `istr` (like ES6 template strings).

## Version 2022.01.14-391

- Fix bug in starts-with? and ends-with? predictates.

## Version 2021.05.29-0

IMPORTANT NOTE:

- This release removes the third party XRegExp dependency (making this
  library to have 0 external dependencies). This may affect
  environments that does not suport the unicode flag.


## Version 2021.05.09-0

- Reaply shadow-cljs compatibility fixes that have been accidentally
  reverted in previous release. (sorry for the inconvenience)


## Version 2021.05.02-0

- Update xregexp to 5.0.2
- Update deps and docs.


## Version 2020.03.27

- Use the latest xregexp version (4.3.0 from npm).
- Make it compatible with shadow-cljs.


## Version 2020.03.26

- Version numbering change to more simple one, based on date.
- Remove `parse-*` functions they behave inconsistently and cuerdas is
  a string manipulation library (not parsing library).
- Remove already marked deprecated functions.
- Remove `caseless=` and `locale-caseless=`
- Remove `lower-locale` and `upper-locale` (no cross platform support).
- Add `index-of` and `last-index-of` (safer versions of clojure.string/*).
- Remove support for `java.lang.Character` (all arguments should be
  strings now, this doesn't have much added value but removing
  improves performance).
- Make all predicates return a boolean type (instead of logical boolean).


## Version 2.2.1

Date: 2019-11-14

- Remove implicit clojurescript compiler dependency.


## Version 2.2.0

Date: 2019-03-31

- Add again the xregexp bundle (revert previous change)
- Update xregexp to 4.2.0.


## Version 2.1.0

Date: 2019-01-09

- Fix `parse-int` precision issues.
- Remove regexp bundle (provide it as npm dep).


## Version 2.0.6

Date: 2018-07-22

- Fix unexpected behavior of `capital` with empty strings.

## Version 2.0.5

Date: 2018-01-08

- Fix unexpected exception on `pad` function when the provided string
  length is larger than the `:length` parameter (cljs only).
- Fix unexpected behavior with regexes with advanced compilation.
- Fix `parse-int` and `parse-double` unexpected behavior on clj and cljs.


## Version 2.0.4

Date: 2017-10-06

- Fix compatibility issues with the latest version of cljs compiler.


## Version 2.0.3

Date: 2017-02-06

- Fix many reflection warnings.


## Version 2.0.2

Date: 2016-12-25

- Allow pass single chars to varios functions that accepts strings.


## Version 2.0.1

Date: 2016-11-15

- Fix `js-selector` wrong behavior.


## Version 2.0.0

Date: 2016-10-23

- The `blank?` predicate now properly returns `nil` if `nil` value is provided.
  (BREAKING CHANGE).
- Add missing `underscored` deprecated alias.
- Fix some bugs in `ends-with?` predicate.
- Improve NPE handling in many functions.
- Fix wrong impl in `locale-lower` and `locale-upper` functions.
- Fix inconsistencies on `caseless=` and `locale-caseless=` functions.
- Depreacate `startswith?` and `endswith?` predicate aliases.
- Rename `<<` interpolation macro to `istr` (an alias for the old name
  is maintained for backward compatibility)
- Improve unicode support for `title` and similar functions.
- Add `empty-or-nil?` predicate.


## Version 1.0.2

Date: 2016-09-17

- Return boolean type instead logical boolean from `blank?`, `alpha?`, `alnum?`
  `digits?`, `word?`, `letters?` and `numeric?` predicates.


## Version 1.0.1

Date: 2016-08-30

- Fix compatibility issue with clojure 1.7 due to the internal
  usage of clojure.string/includes? that has been introduced in 1.8.
- Mark deprecated the `contains?` predicate because it is identical to
  `includes?`.


## Version 1.0.0

Date: 2016-08-29

Important: this is a major release and includes many *breaking changes*
among other fixes and improvements. A proper backward compatibility aliases
are maintained but not all conserves the exactly same behavior.

This is a complete list of changes:

- Rename `alpha-numeric?` to `alnums?`.
- Rename existing `numeric?` to `digits?`.
- Rename old functions such as `dasherize`, `underscore`, `classify`, `titleize`,
  `humanize` with more consistent with clojure naming: `kebab`, `snake`, `pascal`,
  `title` and `human`. Backward compatibility aliases are maintained.
  BREAKING CHANGE: `-moz-transition` <-> `MozTransition` transformations
  and similar stuff related to css/js are now handled by new functions:
  `js-selector` and `css-selector`. (Please, see more info in PR #33).
- Rename `parse-long` to `parse-int` in jvm impl (make the clj and cljs impl
  equivalent).
- Rename `parse-float` to `parse-double` in cljs impl (the second arity is
  removed and now clj and cljs impl are equivalent).
- Rename `capitalize` to `capital`.
- Make `parse-number` cross-platform (and remove the second arity that used for
  specify the precision).
- Make `words` function unicode aware.
- Make `blank?` predicate unicode aware.
- Make `clean` function unicode aware.
- Make `collapse-whitespace` function unicode aware.
- Improve performance of `capital` function.
- Fix inconsistency issue in `words` function in terms of nil safety.
- Fix wrong behavior of `replace` function in cljs.
- Remove ireplace-first function (it was only on the cljs).
- Remove ireplace function (it was only on the cljs).
- Add `escape-html` and `unescape-html` for clj.
- Add `word?` predicate (unicode aware).
- Add `letters?` predicate (unicode aware).
- Add `numeric?` predicate.
- Add `locale-upper` and `locale-lower` functions.
- Add `caseless=` and `locale-caseless=` functions.
- Add `fmt` alias to `format` function.
- Add `uslug` function (unicode friendly version of `slug`).
- Add string interpolation support (`<<` macro)
- Add string unindentation support (`<<-` function)
- Add enhaced support for regular expressions thanks to xregexp (cljs only).


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
