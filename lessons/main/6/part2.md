## HTL Block Statements

HTL block statements are custom data attributes added directly to the existing HTML.
Elements with `data-sly-*` attributes can have a closing tag or be self-closing. Attributes can have values (which can be static strings or expressions), or simply be boolean attributes (without a value).
Several block statements can be set on the same element.
All evaluated `data-sly-*` attributes are removed from the generated markup.
An identifier can also follow a block statement:
```html
<div data-sly-test.myTestVar="${myTest}">...</div>
```

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
<p data-sly-test.editOrDesign="${wcmmode.edit || wcmmode.design}">This text will be displayed only if “wcmmode.edit || wcmmode.design” is evaluated to TRUE</p>
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
- odd: true if the index is odd.
- even: true if the index is even.

Defining an identifier on the data-sly-list statement allows you to rename the itemList and item variables. item will become variable and itemList will become variableList.
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

With **data-sly-repeat** you can repeat an element multiple times based on the list that is specified.
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
**data-sly-attribute** can be used to add multiple attributes at once. The expression must evaluate to an object with key-value pairs. For example:
```html
<div data-sly-attribute="${myAttributes}"></div>
```
will add all the key-value pairs from myAttributes as attributes to the div element.

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

**data-sly-include** replaces the content of the host element with the markup generated by the specified HTML template file when processed by its corresponding template engine.
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

### Resource

**data-sly-resource** includes the result of rendering the indicated resource (form an absolute or a relative path). Here is a simple example:
```html
<article data-sly-resource="path/to/resource"></article>
```
**data-sly-resource** creates a new internal request via the sling engine. You can also modify the resource path, selectors, resourceType.
```html
```html
<section data-sly-resource="${'my/path' @ appendPath='appended/path'}"></section>
<!--/* Will include my/path/appended/path */-->
<section data-sly-resource="${'my/path' @ selectors='selector1.selector2'}"></section>
<!--/* Will include my/path.selector1.selector2 */-->
<section data-sly-resource="${'./path' @ resourceType='my/resource/type'}"></section>
<!--/* Will include the path with the overridden resource type */-->
```

### Template & Call

In HTL, templates define reusable blocks of markup.
Template blocks are similar to functions: in their declaration they can get parameters which can then be passed when calling them.

**data-sly-template** defines a template. The host element and its content are not be rendered by HTL.
The identifier set by the data-sly-template block element is global and available no matter if it's accessed before or after the template's definition. An identically named identifier created with the help of another block element can override the value of the identifier set by data-sly-template.

**data-sly-call** calls a template defined with data-sly-template. The content of the called template (optionally parameterized) replaces the content of the host element.
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

## Format

This option can be used to format dates and numbers, and also interpolate strings with inserted values (much like Java's String.format() method does). Here are some examples of how to use the format option:

```html
<div>${'Asset {0} out of {1}' @ format=[properties.current, properties.total]}</div>
<!--/* Asset 3 out of 5 */-->

<div>${'yyyy-MM-dd HH:mm:ss.SSSXXX' @ format=obj.date, timezone='UTC'}</div>
<!--/* 1918-12-01 00:00:00.000Z */-->

<div>${'#.###;-#.###' @ format=obj.number}</div>
<!--/* -3.14 */-->
```

## Localization

You can use I18n dictionaries in HTL in an easy and beautiful manner by just adding the i18n option. This option translates strings using the current language's dictionary. If no translation is found, the original string is used.

${'Page' @ i18n}

The hint option can be used to provide a comment for translators, specifying the context in which the text is used:

${'Page' @ i18n, hint='Translation Hint'}

The default source for the language is resource, meaning that the text gets translated to the same language as the content. This can be changed to user, meaning that the language is taken from the browser locale or from the locale of the logged-in user:

${'Page' @ i18n, source='user'}

Providing an explicit locale overrides the source settings:

${'Page' @ i18n, locale='en-US'}

To embed variables into a translated string, the format option can be used:

${'Page {0} of {1}' @ i18n, format=[current, total]

You can find a lot more information in the official [HTL Specification](https://github.com/adobe/htl-spec/blob/master/SPECIFICATION.md) or [AEM HTL Guide](https://experienceleague.adobe.com/docs/experience-manager-htl/content/getting-started.html?lang=en).

---

[To previous part](part1.md)

[To Contents](../../../README.md)