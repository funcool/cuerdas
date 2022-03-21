# User Guide

## Introduction

The missing clojure(script) string manipulation library.

```
La guitarra,
hace llorar a los sueños.
El sollozo de las almas
perdidas,
se escapa por su boca
redonda.
Y como la tarántula
teje una gran estrella
para cazar suspiros,
que flotan en su negro
aljibe de madera.
```

## Install

Add the following dependency to your project.clj file:

```clojure
funcool/cuerdas {:mvn/version "RELEASE"}
```

## Quick start

```clojure
(ns my.namespace
  (:require [cuerdas.core :as str]))
```

```clojure
(str/strip-tags "<p>just <b>some</b> text</p>")
;; => "just some text"

(str/strip-tags "<p>just <b>some</b> text</p>" ["p"])
;; => "just <b>some</b> text"
```

**NOTE**: this library only intends to work with strings and always
being null-safe. So practically all functions that expectes a `string`
as argument and receives a `nil` will return `nil`.


## Self-host ClojureScript

```
lumo -c $(clojure -Srepro -Sdeps '{:deps {funcool/cuerdas {:mvn/version "X.Y.Z"}}}' -Spath)
```

```clojure
cljs.user=> (require '[cuerdas.core :as str])
;; => nil
cljs.user=> (str/collapse-whitespace " foo bar    ")
;; => "foo bar"
```

## Reference

**NOTE**: this section expalins only a limited set of functions, for
complete overview look at the namespace functions reference.

### <<-

Unindent lines. Either strip preceding whitespace automatically or
with a user supplied regex.

```clojure
(str/<<- "first line

            second line (indented)

          another line")
```

yields the string

```clojure
first line

  second line (indented)

another line
```


### istr

String interpolation macro. Enables easy string formating allowing
symbol substitutions and simple expression evaluation.
At the moment not compatible with self-host ClojureScript.

```clojure
(def value 30)

(str/istr "value = ~{value}")
;; => "value = 30"

(str/istr "value = ~(inc value)")
;; => "value = 31"
```

The `istr` macro is variadic and allows arbitrary number of arguments
that will be concatenated on the final return value:

```clojure
(str/istr "the value "
          "is ~{value}")
;; => "the value is 30"
```

### alnum?

Checks if a string contains only alphanumeric characters.

```clojure
(str/alnum? nil)
;; => false

(str/alnum? "")
;; => false

(str/alnum? "Test123")
;; => true
```


### alpha?

Checks if a string contains only alpha characters.

```clojure
(str/alpha? nil)
;; => false

(str/alpha? " ")
;; => false

(str/alpha? "Test")
;; => true
```


### blank?

Check if the string is empty or contains only whitespaces.

```clojure
(str/blank? "foobar")
;; => false

(str/blank? "   ")
;; => true

(str/blank? "")
;; => true

(str/blank? nil)
;; => false
```


### camel

Convert a string or keyword to a camelCased string.

```clojure
(str/camel "foo bar")
;; => "fooBar"

(str/camel :foo_barBaz)
;; => "fooBarBaz"

(str/camel nil)
;; => nil
```


### capital

Uppercases the first character of a string.


```clojure
(str/capital "foo")
;; => "Foo"

(str/capital nil)
;; => nil
```

### chars

Returns a seq of char strings from string.

```clojure
(str/chars "bar")
;; => ["b" "a" "r"]

(str/chars nil)
;; => nil
```


### clean

Trim and replace multiple spaces with a single space.

```clojure
(str/clean "  a   b   ")
;; => "a b"

(str/clean nil)
;; => nil
```


### collapse-whitespace

Converts any adjacent whitespace characters to a single space.

```clojure
(str/collapse-whitespace "a\n\nb")
;; => "a b"

(str/collapse-whitespace nil)
;; => nil
```


### css-selector

Convert a JavaScript style selector to CSS style selector


```clojure
(str/css-selector "PrependedWithDash")
;; => "-prepended-with-dash"

(str/css-selector "noPrependedWithDash")
;; => "no-prepended-with-dash"

(str/css-selector nil)
;; => nil
```


### digits?

Checks if a string contains only digits.


```clojure
(str/digits? nil)
;; => false

(str/digits? "1.1")
;; => false

(str/digits? "210")
;; => true
```


### empty?

Check if the string is empty.


```clojure
(str/empty? "foobar")
;; => false

(str/empty? "")
;; => true

(str/empty? " ")
;; => false

(str/empty? nil)
;; => false
```


### empty-or-nil?

Check if the string is empty or is nil.


```clojure
(str/empty-or-nil? "foobar")
;; => false

(str/empty-or-nil? nil)
;; => true

(str/empty? "")
;; => true

(str/empty? " ")
;; => false
```


### ends-with?

Check if the string ends with suffix.


```clojure
(str/ends-with? "foobar" "bar")
;; => true

(str/ends-with? "foobar" nil)
;; => false

(str/ends-with? nil "bar")
;; => false
```


### format

Simple string formatting function.

The string formating works in two main modes: indexed and associative.

The indexed mode is the most simple and consists in using `%s` tokens in the string
indicating the position where interpolation should be done and an arbitrary number
of non associate arguments. Format will replace all `%s` occurrences with the
provided values in ordered mode:

```clojure
(str/format "hello %s and %s" "yen" "ciri")
;; => "hello yen and ciri"
```

If you don't provide enough values, the `%s` tokens won't be changed:

```clojure
(str/format "hello %s and %s" "yen")
;; "hello yen and %s"
```

There are also the associative mode that consists in passing only one associative
argument (map or vector) and use named interpolation tokens:

```clojure
(str/format "hello %(name)s" {:name "yen"})
;; => "hello yen"
```

A part of the `%()s` syntax, the `$something` can be used:

```clojure
(str/format "hello $name" {:name "yen"})
;; => "hello yen"
```

And you can access to indexed positions of an vector using `$0`, `$1`, `$N` syntax:

```clojure
(str/format "hello $0" ["yen"])
;; => "hello yen"
```

You can use `str/fmt` as shorter alias to `str/format` function.


### human

Convert a string or keyword to a human friendly string
(lower case and spaces).


```clojure
(str/human "JustNiceForReading")
;; => "just nice for reading"

(str/human :great-for-csv-headers)
;; => "great for csv headers"

(str/human nil)
;; => nil
```


### includes?

Determines whether a string includes a substring.


```clojure
(str/includes? "foobar" "bar")
;; => true

(str/includes? "foobar" nil)
;; => false

(str/includes? nil nil)
;; => false
```


### join

Join strings together with given separator.

```clojure
(str/join ["foo" "bar"])
;; => "foobar"

(str/join "," ["foo" "bar"])
;; => "foo,bar"
```


### js-selector

Convert a CSS style selector to JavaScript style selector.

```clojure
(str/js-selector "-pascal-case-me")
;; => "PascalCaseMe"

(str/js-selector "camel-case-me")
;; => "camelCaseMe"

(str/js-selector nil)
;; => nil
```


### kebab

Convert a string or keyword into a kebab-cased-string.

```clojure
(str/kebab "Favorite BBQ food")
;; => "favorite-bbq-food"

(str/kebab :favorite-bbq-food)
;; => "favorite-bbq-food"

(str/kebab nil)
;; => nil
```


### keyword

A more helpful and forgiving version of `clojure.core/keyword`.

```clojure
(str/keyword "just_doIt Right")
;; => :just-do-it-right

(str/keyword "foo" "auto namespace me")
;; => :foo/auto-namespace-me

;; and assuming the user namespace
(str/keyword *ns* "auto namespace me")
;; => :user/auto-namespace-me

(str/keyword nil)
;; => nil
```


### letters?

This is an unicode aware version of `alpha?`.

```clojure
(str/letters? nil)
;; => false

(str/letters? " ")
;; => false

(str/letters? "Test")
;; => true

(str/letters? "Русский")
;; => true
```


### lines

Return a list of the lines in the string.

```clojure
(str/lines "foo\nbar")
;; => ["foo" "bar"]

(str/lines nil)
;; => nil
```

### lower

Convert a string to all lower-case in a locale independent manner:


```clojure
(str/lower "FOO")
;; => "foo"

(str/lower nil)
;; => nil
```

For locale awareness, use `locale-lower` alternative function.


### ltrim

Removes whitespace or specified characters from
left side of string.

```clojure
(str/ltrim " foo ")
;; => "foo "

(str/ltrim "-foo-", "-")
;; => "foo-"

(str/ltrim nil)
;; => nil
```


### numeric?

Checks if a string contains only numeric characters.

```clojure
(str/numeric? nil)
;; => false

(str/numeric? "1.1")
;; => true

(str/numeric? "2e10")
;; => true
```


### pad

Pads the string with characters until the total string length is equal to
the passed length parameter.

By default, pads on the left with the space char.

```clojure
(str/pad "1" {:length 8})
;; => "       1"

(str/pad nil {:length 8})
;; => nil

(str/pad "1" {:length 8 :padding "0"})
;; => "00000001"

(str/pad "1" {:length 8 :padding "0" :type :right})
;; => "10000000"

(str/pad "1" {:length 8 :padding "0" :type :both})
;; => "00001000"
```


### pascal

Convert a string or keyword into a PascalCasedString
(aka, UpperCamelCase and ClassCase).

```clojure
(str/pascal "my name is epeli")
;; => "MyNameIsEpeli"

(str/pascal :some-record)
;; => "SomeRecord"

(str/pascal nil)
;; => nil
```


### phrase

Convert a potentially mixed string or keyword into a
capitalized, spaced string

```clojure
(str/phrase "  capitalize dash-CamelCase_underscore trim  ")
;; => "Capitalize dash camel case underscore trim"

(str/phrase :nobody-uses-keywords-this-long-but-it-still-works)
;; => "Nobody uses keywords this long but it still works"

(str/phrase nil)
;; => nil
```


### prune

Truncates a string to certain width and adds "..." if necessary. Making
sure that the pruned string does not exceed the original length and avoid
half-chopped words when truncating.


```clojure
(str/prune "Hello World" 5)
;; => "Hello..."

(str/prune "Hello World" 8)
;; => "Hello..."

(str/prune "Hello World" 11 " (...)")
;; => "Hello (...)"

(str/prune nil 5)
;; => nil
```


### quote

Quote a string.

```clojure
(str/quote "a")
;; => "\"a\""

(str/quote nil)
;; => nil
```


### repeat

Repeats string N times.

```clojure
(str/repeat "a" 3)
;; => "aaa"

(str/repeat nil 3)
;; => nil
```


### replace

Replaces all instances of match with replacement in s.


```clojure
(str/replace "aa bb aa" "aa" "kk")
;; => "kk bb kk"

(str/replace "aa bb aa" #"aa" "kk")
;; => "kk bb kk"

(str/replace nil #"aa" "kk")
;; => nil
```


### replace-first

Replaces first instance of match with replacement in s.

```clojure
(str/replace-first "aa bb aa" "aa" "kk")
;; => "kk bb aa"

(str/replace-first "aa bb aa" #"aa" "kk")
;; => "kk bb aa"

(str/replace-first nil #"aa" "kk")
;; => nil
```


### reverse

Return string reverted


```clojure
(str/reverse "bar")
;; => "rab"

(str/reverse nil)
;; => nil
```


### rtrim

Removes whitespace or specified characters from
right side of string.

```clojure
(str/rtrim " foo ")
;; => " foo"

(str/rtrim "-foo-", "-")
;; => "-foo"

(str/rtrim nil)
;; => nil
```


### slice

Extracts a section of a string and returns a new string.

```clojure
(str/slice "123" 1)
;; => "23"

(str/slice "1234" 1 3)
;; => "23"

(str/slice nil 1 3)
;; => nil
```


### slug

Transforms string or keyword into URL slug.


```clojure
(str/slug "Un �l�phant � l'or�e du bois")
;; => "un-elephant-a-loree-du-bois"

(str/slug nil)
;; => nil
```

Traditionally, slug is consisted in ascii characters, but in modern ages,
the URL and domain names already supports unicode characters. The `uslug` is
more modern version of slug function that respects the unicode characters.


### snake

Convert a string or keyword to a snake_cased_string.

```clojure
(str/snake "Slither-sliter Slither")
;; => "slither_slither_slither"

(str/snake :slither-slither)
;; => "slither_slither"

(str/snake nil)
;; => nil
```


### split

Splits a string on a separator a limited number of times.
The separator can be a string or RegExp instance.

```clojure
(str/split "1 2 3")
;; => ["1" "2" "3"]

(str/split "1 2 3" " ")
;; => ["1" "2" "3"])

(str/split "1 2 3" #"\s")
;; => ["1" "2" "3"]

(str/split "1 2 3" #"\s" 2)
;; => ["1" "2 3"]

(str/split nil)
;; => nil
```


### starts-with?

Check if the string starts with prefix.

```clojure
(str/starts-with? "foobar" "foo")
;; => true

(str/starts-with? "foobar" nil)
;; => false

(str/starts-with? nil "foo")
;; => false
```

### index-of

Return index of value (string or char) in s, optionally searching
forward from from-index. Return nil if value not found.

```clojure
(str/index-of "foobar" "foo")
;; => 0

(str/index-of "foobar" nil)
;; => nil
```

### last-index-of

Return last index of value (string or char) in s, optionally searching
backward from from-index. Return nil if value not found.

```clojure
(str/last-index-of "foobar" "foo")
;; => 0

(str/last-index-of "foobar" nil)
;; => nil
```


### strip-newlines

Takes a string and replaces newlines with a space. Multiple lines are
replaced with a single space.

```clojure
(str/strip-newlines "a\n\nb")
;; => "a b"

(str/strip-newlines nil)
;; => nil
```


### strip-prefix

Remove prefix from string if it matches exactly or leave
the string untouched.

```clojure
(str/strip-prefix nil nil)
;; => nil

(str/strip-prefix "a" nil)
;; => "a"

(str/strip-prefix "-=a" "-=")
;; => "a"
```

### strip-suffix

Remove suffix from string if it matches exactly or leave
the string untouched.

```clojure
(str/strip-suffix nil nil)
;; => nil

(str/strip-suffix "a" nil)
;; => "a"

(str/strip-suffix "a=-" "=-")
;; => "a"
```


### strip-tags

Remove html tags from string.


```clojure
(str/strip-tags "<p>just <b>some</b> text</p>")
;; => "just some text"

(str/strip-tags "<p>just <b>some</b> text</p>" ["p"])
;; => "just <b>some</b> text"

(str/strip-tags nil)
;; => nil
```

It also allows arbitrary replacements:

```clojure
(str/strip-tags "<p>just<br>text</p>" {:br "\n"})
;; => "just\ntext"

(str/strip-tags "<p>just<br>text</p>" ["br"] {:br "\n"})
;; => "<p>just\ntext</p>"
```


### surround

Surround a string with another string.

```clojure
(str/surround "a" "-")
;; => "-a-"

(str/surround "a" "-^-")
;; => "-^-a-^-"

(str/surround nil "-^-")
;; => nil
```


### title

Convert a string or keyword into a space separated string
with each word capitalized.

```clojure
(str/title "a tale of two cities")
;; => "A Tale Of Two Cities"

(str/title :title-case)
;; => "Title Case"

(str/title nil)
;; => nil
```


### to-bool

Returns true for 1/on/true/yes string values (case-insensitive), false otherwise.


```clojure
(str/to-bool "hello")
;; => false

(str/to-bool "on")
;; => true
```


### trim

Removes whitespace or specified characters from
both ends of string.


```clojure
(str/trim " foo ")
;; => "foo"

(str/trim "-foo-", "-")
;; => "foo"

(str/trim nil)
;; => nil
```


### unlines

Joins a list of strings with a newline separator.  This operation is
the opposite of lines.


```clojure
(str/unlines ["foo" "nbar"])
;; => "foo\nbar"

(str/unlines nil)
;; => nil
```


### unquote

Unquote a string.


```clojure
(str/unquote "\"a\"")
;; => "a"

(str/unquote nil)
;; => nil
```


### unsurround

Unsurround a string surrounded by another.

```clojure
(str/unsurround "-a-" "-")
;; => "a"

(str/unsurround "-^-a-^-" "-^-")
;; => "a"

(str/unsurround nil "-")
;; => nil
```


### upper

Convert a string to all upper-case in a locale independent manner:

```clojure
(str/upper "foobar")
;; => "FOOBAR"

(str/upper nil)
;; => nil
```

For locale awareness, use `locale-upper` alternative function.


### word?

This is a unicode aware version of `alnum?`.

```clojure
(str/word? nil)
;; => false

(str/word? "")
;; => false

(str/word? "Русский222")
;; => true
```

### words

Returns a vector of the words in the string. Can be provided with a regular
expression that matches a single word (defaults to `[a-zA-Z0-9_-]+`).


```
(str/words nil)
;; => nil

(str/words "foo, bar")
;; => ["foo" "bar"]

(str/words "foo, bar." #"[^, ]+")
;; => ["foo" "bar."]
```


## Run tests

_cuerdas_ has targeted some parts of implementation for Clojure and
ClojureScript using Reader Conditionals.

.Run tests in the Clojure environment.
```
$ clj -A:dev ./tools.clj test
```

.Run tests in the ClojureScript environment.
```
$ clj -A:dev ./tools.clj test-cljs
```


## How to Contribute?

**cuerdas**' source is on https://github.com/funcool/cuerdas[github].

Unlike Clojure and other Clojure contrib libs, cuerdas does not have many
restrictions for contributions.

*Pull requests are welcome!*


## License

_cuerdas_ is licensed under BSD (2-Clause) license:

```
Copyright (c) 2015-2016 Andrey Antukh <niwi@niwi.nz>

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
``` 
