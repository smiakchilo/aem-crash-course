# 2.8 Components View. Markup language HTL (Sightly)

## Introduction

As you know, a web server supports either backend rendering of web pages, frontend rendering, or else both. So does AEM. We have already discussed that backend rendering has advantages in highly-loaded multi-device websites. The page markup is created mostly on the server and delivered to a user's device. To implement such, we need to have some invariable markup (the "structure" or else "template"), and also some variable markup and data that will be mixed in the structure upon every request. Such a technique is known as templating. AEM has its very own templating engine that you must learn, it is called Sightly or HTL.

### What is HTL (Sightly)?

**Hypertext Template Language (HTL)** or **Sightly** is specifically designed for AEM. For today it is the default template system for AEM. It takes the place of JSP (JavaServer Pages) as used in previous versions of AEM.
HTL uses an expression language to insert pieces of content into the rendered markup, and HTML5 data attributes to define statements over blocks of markup (like conditions or iterations). As HTL gets compiled into Java Servlets, the expressions and the HTL data attributes are evaluated entirely server-side and none remain visible in the resulting HTML.

## 2.8.1 Syntax

HTL comprises:
1. Expression Language. Used to insert pieces of content into the rendered markup. Sightly code is written using dollar sign ‘$’ and braces ‘{}’.${currentPage.title} where currentPage is a global object and title is a property.
2. Block statements. You can add a special data-attribute that starts with “data-sly-” and contains an HTL statement is quotes to any HTML element. The content in quotes can be either an HTL expression ( ${...} ) or a plain string, usually referring to an URL or a page name. For example, ```<div data-sly-include=”main.html”/>``` is an HTL block expression that is bound to an ordinary HTML tag (```<div>```) and contains the definitive data-sly-... attribute. In this very case, the attribute is data-sly-include. It instructs sightly to insert a portion of markup stored in main.html into the current ```<div>``` tag.

The expression syntax includes literals, variables, operators, and options.
Literals can be Boolean, Integers (including exponentiation, floating point numbers are not supported), Strings and Arrays.
Variables, much like in Java or JS,  can represent a primitive value or an object with properties. There are two ways to access object properties, with a dot notation, or with a bracket notation:
```html
${currentPage.title}
${currentPage['title']} or ${currentPage["title"]}
```

So called options can be added to every expression.
Everything after the @ is an option:
```html
${myVar @ optOne}
```

Each option can be used in several ways:
1. Option can work as a functions argument - it accepts the expression as an argument and processes it.
2. When the expression is located in a data-sly-* statement, they allow providing instructions or parameters to that statement.
On a plain expression (that is not located in a data-sly-* statement), the following options are possible: **format** (concatenates strings), **i18n** (translates strings), **join** (Defines the string delimiter to display between items of an array), **context** (Controls how the HTML escaping and XSS protection applies).
The **<sly>** HTML tag can be used to remove the current element, allowing only its children to be displayed.

## 2.8.2 Using Java/JS entity. Display Context

The great power of HTL is its ability to import and use entities from the Java backend, and also from JS. This way, we can declare an instance of a Sling model, or a reference to a Service, retrieve data from it, and insert right into the markup. Also, we can declare a reference to another HTL file and use it within the current HTL file. This all is done with a data-sly-use HTL block statement.
data-sly-use initializes a helper object (defined in JavaScript or Java) and exposes it through a variable.
To use a java class/js file we need to write a full name of a java class or path to the js file with name and extension if they are not in the same directory as a HTL file.
```html
<div data-sly-use.test="org.example.JavaClass">${test.foo}</div>
```
The identifier set by the **data-sly-use** block element is global to the script and can be used anywhere after its declaration (except for the situation when it is declared within data-sly-test). Any method can be called (even one that does not return anything). As long as it is an instance method (not a static one) and has no arguments. Mind that if your method is some getter (starts with "get..." or "is..."), these prefixes should be omitted.
When you are calling a Java class method from HTL, make sure it takes no arguments. But what if you actually need to pass some data from HTL to Java? Usually, this is done via @-options. If you are using a Sling Model through data-sly-use, these options will be retrievable from it as attributes of the request. Please mind that they will refer to the "whole" Sling model, not a concrete method.
```html
<div data-sly-use.test="${‘org.example.JavaClass’ @ param=’value’}"></div>
```
All Java objects can be used by **data-sly-use**.
To use a JS object we need to use function **use(function())** which signifies the usage of the Use API, where we need to initialize and return the object that we can use in HTL.
For example, our JS file text.js can look like this:
```javascript
use(function() {
    var text = {};
    text.title = ‘title’;
    text.description = ‘description’;
    return text;
});
```
And to get our object in HTL file, we will use data-sly-use like that:
```html
<div data-sly-use.text=”text.js”>
    <h1>Title: ${text.title}</h1>
    <p>Description: ${text.description}</p>
</div>
```

### Display context
To protect against cross-site scripting (XSS) vulnerabilities, HTL automatically recognises the context within which an output string is to be displayed within the final HTML output, and escapes that string appropriately. For example, if the expression appears in place that would produce a text node once rendered, then it is said to be in a text context. If it is found within the value of an attribute, then it is said to be in an attribute context, and so forth.
Usually, the context is "recognized" by the HTL engine automatically, and you don't need to specify it. But sometimes you need to override the default with the context option:
```html
<div>${properties.richText @ context='html'}</div>
```
Available contexts:
| Context | When to use | What it does |
| --- | --- | --- |
| attribute | Default for attribute values | Encodes all HTML special characters. |
| attributeName | Default for data-sly-attribute when setting attribute names | Validates the attribute name, outputs nothing if validation fails. |
| elementName | Default for data-sly-element. Allows only the following element names: section, nav, article, aside, h1, h2, h3, h4, h5, h6, header, footer, address, main, p, pre, blockquote, ol, li, dl, dt, dd, figure, figcaption, div, a, em, strong, small, s, cite, q, dfn, abbr, data, time, code, var, samp, kbd, sub, sup, i, b, u, mark, ruby, rt, rp, bdi, bdo, span, br, wbr, ins, del, table, caption, colgroup, col, tbody, thead, tfoot, tr, td, th. | Validates the element name, outputs nothing if validation fails. |
| html | To safely output markup | Filters HTML in order to remove dangerous tags. |
| number | To display numbers | Validates that the passed value is a number, outputs nothing if validation fails. |
| scriptComment | Within JavaScript comments | Validates the JavaScript comment, outputs nothing if validation fails. |
| scriptString | Within JavsScript strings | Encodes characters that would break out of the string. |
| scriptToken | For JavaScript identifiers, literal numbers, or literal strings | Validates the JavaScript token, outputs nothing if validation fails. |
| styleComment | Within CSS comments | Validates the CSS comment, outputs nothing if validation fails. |
| styleString | Within CSS strings | Encodes characters that would break out of the string. |
| styleToken | For CSS identifiers, numbers, dimensions, strings, hex colours or functions. | Validates the CSS token, outputs nothing if validation fails. |
| text | Default for content inside HTML Text Nodes | Encodes all HTML special characters. |
| unsafe | When all the other contexts are too restrictive | **Disables escaping and XSS protection completely.** |
| uri | To display links and paths; default for the action, cite, data, formaction, href, manifest, poster and src attribute values | Validates the URI and outputs nothing if validation fails. |

## 2.8.3 HTL Global Objects

Without having to specify anything, HTL provides access to some global objects. Any argumentless methods of objects can be invoked by HTL.
These objects provide convenient access to commonly used information. Their content can be accessed with the dot notation.
The most useful variables:
- **properties** - List of properties of the current Resource. Backed by org.apache.sling.api.resource.ValueMap
- **pageProperties** - List of page properties of the current Page. Backed by org.apache.sling.api.resource.ValueMap
- **inheritedPageProperties** - List of inherited page properties of the current Page. Backed by org.apache.sling.api.resource.ValueMap
- **currentPage** - com.day.cq.wcm.api.Page
- **request** - org.apache.sling.api.SlingHttpServletRequest
- **resource** - org.apache.sling.api.resource.Resource
- **wcmmode** - com.adobe.cq.sightly.SightlyWCMMode

## 2.8.4 HTL Block Statements

HTL block statements are custom data attributes added directly to existing HTML.
Elements with data-sly- attributes can have a closing tag or be self-closing. Attributes can have values (which can be static strings or expressions), or simply be boolean attributes (without a value).
Several block statements can be set on the same element.
All evaluated data-sly-* attributes are removed from the generated markup.
A block statement can also be followed by an identifier:

### Available Block Statements

There are a number of block statements available. When used on the same element, the following priority list defines how block statements are evaluated:
1. data-sly-template
2. data-sly-set, data-sly-test, data-sly-use
3. data-sly-call
4. data-sly-text
5. data-sly-element, data-sly-include, data-sly-resource
6. data-sly-unwrap
7. data-sly-list, data-sly-repeat
8. data-sly-attribute
When two block statements have the same priority, their evaluation order is from left to right.

### Test

**data-sly-test** conditionally removes the host element and its content. A value of false removes the element, a value of true retains the element.
```html
<p data-sly-test.editOrDesign="${wcmmode.edit || wcmmode.design}">This text will be displayed only if “wcmmode.edit || wcmmode.design will” be evaluated to TRUE</p>
```

### Set

**data-sly-set** defines a new identifier with a pre-defined value. The identifier set by the data-sly-set block element is global to the script and can be used anywhere after its declaration.
```html
<span data-sly-set.profile="${user.profile}">Hello, ${profile.firstName} ${profile.lastName}!</span>
<a class="profile-link" href="${profile.url}">Edit your profile</a>
```

### List

**data-sly-list** repeats the content of the host element for each enumerable property in the provided object.
The following default variables are available within the scope of the list:
- item: The current item in the iteration.
- itemList: Object holding the following properties:
- index: zero-based counter ( 0..length-1).
- count: one-based counter ( 1..length).
- first: true if the current item is the first item.
- middle: true if the current item is neither the first nor the last item.
- last: true if the current item is the last item.
- odd: true if index is odd.
- even: true if index is even.
Defining an identifier on the data-sly-list statement allows you to rename the itemList and item variables. item will become variable and itemList will become variable List.
```html
<ul data-sly-list.child="${currentPage.listChildren}">
    <li>${child.title}</li>
</ul>
```
You can use data-sly-list to iterate not only collections, but also Maps (and JS objects that are essentially maps as well). When iterating over Map objects, the item variable contains the key of each map item:
```html
<dl data-sly-list="${myMap}">
    <dt>key: ${item}</dt>
    <dd>value: ${myMap[item]}</dd>
</dl>
```

### Repeat

With data-sly-repeat you can repeat an element multiple times based on the list that is specified.
This works the same way as data-sly-list, except that you do not need a container element.
```html
<p data-sly-repeat="${resource.listChildren}">${item.text}</p>
```


### Attribute

**data-sly-attribute** adds attributes to the host element. The attribute is not added if the expression result is false.
```html
<tag class="className" data-sly-attribute.class="${myVar}"></tag> <!--/* This will overwrite the content of the class attribute */-->
<tag data-sly-attribute.data-values="${myValues}"></tag>          <!--/* This will create a data-values attribute */-->
```

### Unwrap

**data-sly-unwrap** removes the host element from the generated markup while retaining its content. This allows the exclusion of elements that are required as part of HTL presentation logic but are not desired in the actual output.
data-sly-unwrap can be used to hide the element itself, only showing its content.
```html
<!--/* This will only show "Foo" (without a <div> around) if the test is true: */-->
<div data-sly-test="${myTest}" data-sly-unwrap>Foo</div>

<!--/* This would show a <div> around "Foo" only if the test is false: */-->
<div data-sly-unwrap="${myTest}">Foo</div>
```
However, this statement should be used sparingly. In general, it is better to keep the HTL markup as close as possible to the intended output markup. In other words, when adding HTL block statements, try as much as possible to simply annotate the existing HTML, without introducing new elements.  One use case is when to use data-sly-unwrap it with conditional statements. For e.g., if a parent element needs to present only in certain scenarios, then using it in the following w,ay is much cleaner than writing multiple data-sly-test conditions.

### Text

**data-sly-text** replaces the content of its host element with the specified text.
```html
<p>${properties.jcr:description}</p>
```
Is equivalent to
```html
<p data-sly-text="${properties.jcr:description}">Lorem ipsum</p>
```
Both will display the value of jcr:description as paragraph text. The advantage of the second method is that it allows the unobtrusive annotation of HTML while keeping the static placeholder content from the original designer.

### Element

**data-sly-element** replaces the element name of the host element.
```html
<div data-sly-element="${'h1'}">Blah</div>
<!--/* outputs: */-->
<h1>Blah</h1>
```

### Include

**data-sly-include** replaces the content of the host element with the markup generated by the indicated HTML template file (HTL, JSP, ESP etc.) when it is processed by its corresponding template engine. The rendering context of the included file will not include the current HTL context (that of the including file).
```html
<div data-sly-include="template.html"></div>
<div data-sly-include="template.jsp"></div>
```
The following options can be added to the expression:
- appendPath - appends its content to the passed path;
```html
<div data-sly-include="${'partials' @ appendPath='template.html'}"></div>
<!--/* will include partials/template.html */-->
```
- prependPath - prepends its content to the passed path.
```html
<div data-sly-include="${'template.html' @ prependPath='partials'}"></div>
<!--/* will include partials/template.html */-->
```

## 2.8.5 Format

This option can be used to format dates and numbers, and also interpolate strings with inserted values (much like Java's String.format() method does). A formatting pattern string must be supplied in the expression and the format option will contain the value(s) to be used. Type of formatting will be decided based on:
1. the type option, if present (accepted values are string, date and number)
2. placeholders (eg: {0}) in the pattern, triggers string formatting
3. type of format option object, when the type is a Date or a Number
4. default, fallback to string formatting

### Strings

```html
<div>${'Asset {0}' @ format=properties.assetName}</div>
<!--/* Asset Night Sky */-->

<div>${'Asset {0} out of {1}' @ format=[properties.current, properties.total]}</div>
<!--/* Asset 3 out of 5 */-->
```

### Dates

Examples for date 1918-12-01 00:00:00Z
```html
<div>${'yyyy-MM-dd HH:mm:ss.SSSXXX' @ format=obj.date, timezone='UTC'}</div>
<!--/* 1918-12-01 00:00:00.000Z */-->

<div>${'dd MMMM \'\'yy hh:mm a; \'day in year\': D; \'week in year\': w' @ format=obj.date, timezone='UTC'}</div>
<!--/* 01 December '18 12:00 AM; day in year: 335; week in year: 49 */-->
```

### Numbers

The following characters are supported in pattern:
- 0 - digit, shows as 0 if absent
- \# - digit, does not show if absent
- . - decimal separator
- - minus sign
- , - grouping separator
- E - separator between mantissa and exponent
- ; - sub-pattern boundary
- % - multiply by 100 and show as percentage 

Characters can be escaped in prefix or suffix using single quotes. Single quotes are escaped as two in a row.
Examples for number -3.14:
```html
<div>${'#.###;-#.###' @ format=obj.number}</div>
<!--/* -3.14 */-->

<div>${'#.000E00' @ format=obj.number}</div>
<!--/* -.314E01 */-->
```

## 2.8.6 Rendering nested resource

**data-sly-resource** includes the result of rendering the indicated resource. Includes a rendered resource from the same server, using an absolute or relative path.
A simple resource include:
```html
<article data-sly-resource="path/to/resource"></article>
```
**data-sly-resource** creates a new internal request via the sling engine
The following options can be added to the expression:
- appendPath - appends its content to the passed path;
```html
<section data-sly-resource="${'my/path' @ appendPath='appended/path'}"></section>
<!--/* Will include my/path/appended/path */-->
```
- prependPath - prepends its content to the passed path;
```html
<section data-sly-resource="${'my/path' @ prependPath='prepended/path'}"></section>
<!--/* Will include prepended/path/my/path */-->
```
- selectors - replaces all selectors from the original request with the selectors passed in a selector string or a selector array before including the passed path;
```html
<section data-sly-resource="${'my/path' @ selectors='selector1.selector2'}"></section>
<section data-sly-resource="${'my/path' @ selectors=['selector1', 'selector2']}"></section>
```
- addSelectors - adds the selectors from the passed selector string or selector array to the original request before including the passed path;
```html
<section data-sly-resource="${'my/path' @ addSelectors='selector1.selector2'}"></section>
<section data-sly-resource="${'my/path' @ addSelectors=['selector1', 'selector2']}"></section>
```
- removeSelectors - removes the selectors found in the passed selector string or selector array from the original request before including the passed path; when the option doesn't have a value, all the selectors will be removed from the original request;
```html
<section data-sly-resource="${'my/path' @ removeSelectors='selector1.selector2'}"></section>
<section data-sly-resource="${'my/path' @ removeSelectors=['selector1', 'selector2']}"></section>
<section data-sly-resource="${'my/path' @ removeSelectors}"></section>
```
- resourceType - forces the rendering of the passed path with a script mapped to the overridden resource type;
```html
<section data-sly-resource="${'./path' @ resourceType='my/resource/type'}"></section>
```

## 2.8.7 Reusing markup fragments

In Sightly, Templates tend to define reusable and potentially recursive methods that can be defined locally or in separate files.
Template blocks can be used like function calls: in their declaration, they can get parameters, which can then be passed when calling them.
**data-sly-template** defines a template. The host element and its content are not output by HTL.
The identifier set by the data-sly-template block element is global and available no matter if it's accessed before or after the template's definition. An identically named identifier created with the help of another block element can override the value of the identifier set by data-sly-template.
**data-sly-call** calls a template defined with data-sly-template. The content of the called template (optionally parameterized) replaces the content of the host element of the call.
```html
<template data-sly-template.two="${@ title, resource='The resource of the parent node'}"> <!--/* The template element and its content are not output by HTL. */-->
<h1>${title}</h1>
<p>Parent: ${resource.name}</p>
</template>
<div data-sly-call="${two @ title=properties.jcr:title, resource=resource.parent}"></div>
```
When templates are located in a separate file, they can be loaded with data-sly-use:
```html
<div data-sly-use.lib="templateLib.html" data-sly-call="${lib.one}"></div>
<div data-sly-call="${lib.two @ title=properties.jcr:title, resource=resource.parent}"></div>
```
When some parameters are missing in a template call, that parameter would be initialised to an empty string within the template.

## 2.8.8 Manipulating with URI

The URI manipulation options work for expressions that are outside of block statements as well as for data-sly-text and data-sly-attribute. URI manipulation can be performed by adding any of the following options to an expression:
- **scheme** - allows adding or removing the scheme part of the URI.
```html
<div>${‘example.com/path/page.html’ @ scheme=’http’}</div>
<!--/* http://example.com/path/page.html */-->
```
- **domain** - allows adding or replacing the host and port (domain) for a URI.
```html
<div>${‘http://www.example.com/path/page.html’ @ domain=’www.example.org’}</div>
<!--/* http://www.example.org/path/page.html */-->
```
- **path** - modifies the path that identifies the resource.
```html
<div>${‘http://example.com/this/one.selector.html/ suffix?key=value#fragment’ @ path=’that/two’}</div>
<!--/* http://example.com/that/two.selector.html/ suffix?key=value#fragment */-->
```
- **prependPath** - prepends its content to the path that identifies a resource.
```html
<div>${'path' @ prependPath='..'}</div>
<!--/* ../path */-->
```
- **appendPath** - appends its content to the path that identifies a resource.
```html
<div>${'path' @ appendPath='/add'}</div>
<!--/* path/add */-->
```
- **selectors** - modifies or removes the selectors from a URI.
```html
<div>${'path/page.woo.foo.html' @ selectors='foo.bar'}</div>
<!--/* path/page.foo.bar.html */-->
```
- **addSelectors** - adds the provided selectors (selectors string or selectors array) to the URI.
```html
<div>${'path/page.woo.foo.html' @ addSelectors='foo.bar'}</div>
<!--/* path/page.woo.foo.foo.bar.html */-->
```
- **removeSelectors** - removes the provided selectors (selectors string or selectors array) from the URI.
```html
<div>${'path/page.woo.foo.html' @ removeSelectors='foo.bar'}</div>
<!--/* path/page.woo.html */-->
```
- **extension** - adds, modifies, or removes the extension from a URI.
```html
<div>${‘path/page.json’ @ extension=’html’}</div>
<!--/* path/page.html */-->
```
- **suffix** - adds, modifies, or removes the suffix part from a URI.
```html
<div>${‘path/page.html/some/suffix’ @ suffix=’my/suffix’}</div>
<!--/* path/page.html/my/suffix */-->
```
- **prependSuffix** - prepends its content to the existing suffix.
```html
<div>${'path/page.html/suffix' @ prependSuffix='prepended'}</div>
<!--/* path/page.html/prepended/suffix */-->
```
- **appendSuffix** - appends its content to the existing suffix.
```html
<div>${'path/page.html/suffix' @ appendSuffix='appended'}</div>
<!--/* path/page.html/suffix/appended */-->
```
- **query** - adds, replaces or removes the query segment of a URI, depending on the contents of its map value.
```html
<!--
    assuming that jsuse.query evaluates to:
    
    {
      "query": {
        "q" : "htl",
        "array" : [1, 2, 3]
      }
    }
-->
<div>${'http://www.example.org/search' @ query=jsuse.query, context='uri'}</div>
<!--/* http://www.example.org/search?q=htl&array=1&array=2&array=3 */-->
```
- **addQuery** - adds or extends the query segment of a URI with the contents of its map value.
```html
<!--
    assuming that jsuse.query evaluates to:
    
    {
      "query": {
        "q" : "htl",
        "array" : [1, 2, 3]
      }
    }
-->
<div>${'http://www.example.org/search?s=1' @ addQuery=jsuse.query, context='uri'}</div>
<!--/* http://www.example.org/search?s=1&q=htl&array=1&array=2&array=3  */-->
```
- **removeQuery** - removes the identified parameters from an existing query segment of a URI; its value can be a string or a string array.
```html
<!--
    assuming that jsuse.query evaluates to:
    
    {
      "query": {
        "q" : "htl",
        "array" : [1, 2, 3]
      }
    }
-->
<div>${'http://www.example.org/search?s=1&q=htl' @ removeQuery='q', context='uri'}</div>
<!--/* http://www.example.org/search?s=1  */-->
```
- **fragment** - adds, modifies, or replaces the fragment segment of a URI.
```html
<div>${'path/page' @ fragment='fragment'}</div>
<!--/* path/page#fragment  */-->
```

## 2.8.9 Localising

Sightly localise terms provided in dictionaries in an easy and beautiful manner by just adding the context i18n. This option internationalises strings, using the current dictionary. If no translation is found, the original string is used.

${'Page' @ i18n}

The hint option can be used to provide a comment for translators, specifying the context in which the text is used:

${'Page' @ i18n, hint='Translation Hint'}

The default source for the language is resource, meaning that the text gets translated to the same language as the content. This can be changed to user, meaning that the language is taken from the browser locale or from the locale of the logged-in user:

${'Page' @ i18n, source='user'}

Providing an explicit locale overrides the source settings:

${'Page' @ i18n, locale='en-US'}

To embed variables into a translated string, the format option can be used:

${'Page {0} of {1}' @ i18n, format=[current, total]}