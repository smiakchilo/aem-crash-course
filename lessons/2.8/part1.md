# 2.8 Components View. HTL markup language 

## Introduction

As you know, a web server supports either backend rendering of web pages, frontend rendering, or both. So does AEM. We have already discussed that backend rendering has advantages in highly-loaded multi-device websites. The page markup is created mostly on the server and delivered to a user's device. To implement such an approach, we need to have some static markup (the "structure" or else "template"), and also some variable markup and data that will be mixed in the structure upon every request. Such a technique is known as templating. AEM has its very own templating engine that you must learn, it is called HTL.

### What is HTL Sightly?

**HTML Template Language (HTL)**, previously called **Sightly** is specifically designed for AEM. Today it is the default template system for AEM. It takes the place of JSP (JavaServer Pages) that was used in previous versions of AEM.
HTL uses an expression language to insert pieces of content into the rendered markup, and HTML5 data attributes to define statements over blocks of markup (like conditions or iterations). As HTL gets compiled into Java Servlets, the expressions and the HTL data attributes are evaluated entirely server-side and none remain visible in the resulting HTML.

## Syntax

HTL consists of :
1. Expression Language. Used to insert pieces of content into the rendered markup. HTL code is written using the dollar sign ```‘$’``` and braces ```‘{}’```.
```${currentPage.title}``` where currentPage is a global object and title is a property.
2. Block statements. You can add a special data attribute that starts with ```“data-sly-”``` and contains an HTL statement in quotes to any HTML element. The content in quotes can be either an HTL expression (```${...}```) or a plain string, usually referring to a URL or a page name. For example, ```<div data-sly-include=”main.html”/>``` is an HTL block expression that is bound to an ordinary HTML tag (```<div>```) and contains a data-sly-... attribute. In this very case, the attribute is data-sly-include. It instructs HTL to insert a portion of markup stored in main.html into the current ```<div>``` tag.

The expression syntax includes literals, variables, operators, and options.
Literals can be Booleans, Integers (including exponentiation, floating point numbers are not supported), Strings and Arrays.
Variables, much like in Java or JS, can represent a primitive value or an object with properties. There are two ways to access object properties: with a dot notation or with a bracket notation:
```html
${currentPage.title}
${currentPage['title']} or ${currentPage["title"]}
```

So-called options can be added to every expression.
Everything after the @ is an option:
```html
${myVar @ optOne}
```

Each option can be used in several ways:
1. Option can work as anargument that is used in Java\JS objects (see **`data-sly-use`** directive below)
2. When the expression is located in a data-sly-* statement, they allow providing instructions or parameters to that statement.
In a plain expression (that is not located in a data-sly-* statement), the following options are possible: **format** (concatenates strings), **i18n** (translates strings), **join** (defines the string delimiter to display between items of an array), **context** (controls how the HTML escaping and XSS protection apply).
There is a special **`<sly>`** HTML tag that can be used to remove the current element, allowing only its children to be displayed.

## Using Java/JS entities. Display Context

The great power of HTL is its ability to import and use entities from the Java backend, and also from JS. This way, we can declare an instance of a Sling model, or a reference to a Service, retrieve data from it, and insert it right into the markup. Also, we can declare a reference to another HTL file and use it within the current HTL file. This all is done with a `data-sly-use` HTL block statement.
`data-sly-use` initializes a helper object (defined in JavaScript or Java) and exposes it through a variable.
To use a java class/js file we need to write the full name of the java class or the path to the js file with the name and extension if they are not in the same directory as the HTL file.
```html
<div data-sly-use.test="org.example.JavaClass">${test.foo}</div>
```
The identifier set by the `data-sly-use` block element is global to the script and can be used anywhere after its declaration (except for the situation when it is declared within `data-sly-test`). Any method can be called (even one that does not return anything). As long as it is an instance method (not a static one) and has no arguments. Mind that if your method is a getter (starts with "get..." or "is..."), these prefixes can be omitted.
When you are calling a Java class method from HTL, make sure it takes no arguments. But what if you actually need to pass some data from HTL to Java? Usually, this is done via @-options. If you are using a Sling Model through data-sly-use, these options will be retrievable from it as attributes of the request. Please mind that they will refer to the "whole" Sling model, not a concrete method.
```html
<div data-sly-use.test="${‘org.example.JavaClass’ @ param=’value’}"></div>
```
Only certain Java classes can be used with **data-sly-use** directive: Sling models, classes that extend WcmUsePojo, OSGI Services, etc (see a full list [here](https://sling.apache.org/documentation/bundles/scripting/scripting-htl.html#:~:text=support%20for%20loading%20Java%20objects%20such%20as%3A)).
JS objects are used less often than Java objects. There are several reasons for this. Firstly, these js objects are run on the server and hard to debug. Secondly, AEM does not support the latest ECMA Script specification. Thirdly, JS Use-objects are slower than Java Use-objects. But if you still want to use JS objects, you can do it like this:
```javascript
use(function() {
    var text = {};
    text.title = ‘title’;
    text.description = ‘description’;
    return text;
});
```
And to get our object in the HTL file, we will use **`data-sly-use`** directive:
```html
<div data-sly-use.text=”text.js”>
    <h1>Title: ${text.title}</h1>
    <p>Description: ${text.description}</p>
</div>
```

### Display context
To protect against cross-site scripting (XSS) vulnerabilities, HTL automatically recognises the context within which an output string is to be displayed within the final HTML output, and escapes that string appropriately. For example, if the expression appears in a place that would produce a text node once rendered, then it is said to be in a text context. If it is found within the value of an attribute, then it is said to be in an attribute context, and so forth.
Usually, the context is "recognized" by the HTL engine automatically, and you don't need to specify it. But sometimes you need to override the default with the context option:
```html
<div>${properties.richText @ context='html'}</div>
```
Available contexts:

| Context | When to use | What it does                                                                                                                                                             |
| --- | --- |--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| attribute | Default for attribute values | Encodes all HTML special characters.                                                                                                                                     |
| attributeName | Default for data-sly-attribute when setting attribute names | Validates the attribute name, outputs nothing if validation fails.                                                                                                       |
| elementName | Default for data-sly-element. Allows only the following element names: section, nav, article, aside, h1, h2, h3, h4, h5, h6, header, footer, address, main, p, pre, blockquote, ol, li, dl, dt, dd, figure, figcaption, div, a, em, strong, small, s, cite, q, dfn, abbr, data, time, code, var, samp, kbd, sub, sup, i, b, u, mark, ruby, rt, rp, bdi, bdo, span, br, wbr, ins, del, table, caption, colgroup, col, tbody, thead, tfoot, tr, td, th. | Validates the element name, outputs nothing if validation fails.                                                                                                         |
| html | To safely output markup | Filters HTML in order to remove dangerous tags.                                                                                                                          |
| number | To display numbers | Validates that the passed value is a number, outputs nothing if validation fails.                                                                                        |
| scriptComment | Within JavaScript comments | Validates the JavaScript comment, outputs nothing if validation fails.                                                                                                   |
| scriptString | Within JavsScript strings | Encodes characters that would break out of the string.                                                                                                                   |
| scriptToken | For JavaScript identifiers, literal numbers, or literal strings | Validates the JavaScript token, outputs nothing if validation fails.                                                                                                     |
| styleComment | Within CSS comments | Validates the CSS comment, outputs nothing if validation fails.                                                                                                          |
| styleString | Within CSS strings | Encodes characters that would break out of the string.                                                                                                                   |
| styleToken | For CSS identifiers, numbers, dimensions, strings, hex colours or functions. | Validates the CSS token, outputs nothing if validation fails.                                                                                                            |
| text | Default for content inside HTML Text Nodes | Encodes all HTML special characters.                                                                                                                                     |
| unsafe | When all the other contexts are too restrictive | Disables escaping and XSS protection completely. **Should be used only in rare cases when there is no opportunity to use other contexts or tweak the [XSS configuration](https://experienceleague.adobe.com/docs/experience-manager-65/content/implementing/developing/introduction/security.html?lang=en#access-to-cloud-service-information)** |
| uri | To display links and paths; default for the action, cite, data, formaction, href, manifest, poster and src attribute values | Validates the URI and outputs nothing if validation fails.                                                                                                               |

## HTL Global Objects

Without having to specify anything, HTL provides access to a number of global objects. Any argumentless methods of these objects can be invoked by HTL.
These objects provide convenient access to commonly used information. Their content can be accessed with the dot notation.
The most useful variables:
- **properties** - List of properties of the current Resource. Backed by org.apache.sling.api.resource.ValueMap
- **pageProperties** - List of page properties of the current Page. Backed by org.apache.sling.api.resource.ValueMap
- **inheritedPageProperties** - List of inherited page properties of the current Page. Backed by org.apache.sling.api.resource.ValueMap
- **currentPage** - com.day.cq.wcm.api.Page
- **request** - org.apache.sling.api.SlingHttpServletRequest
- **resource** - org.apache.sling.api.resource.Resource
- **wcmmode** - com.adobe.cq.sightly.SightlyWCMMode

---

[Continue reading](part2.md)

[To Contents](../../README.md)