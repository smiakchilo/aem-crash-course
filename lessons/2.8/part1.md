# 2.8 Components View. Markup language HTL (Sightly)

## Introduction

As you know, a web server supports either backend rendering of web pages, frontend rendering, or else both. So does AEM. We have already discussed that backend rendering has advantages in highly-loaded multi-device websites. The page markup is created mostly on the server and delivered to a user's device. To implement such, we need to have some invariable markup (the "structure" or else "template"), and also some variable markup and data that will be mixed in the structure upon every request. Such a technique is known as templating. AEM has its very own templating engine that you must learn, it is called Sightly or HTL.

### What is HTL (Sightly)?

Hypertext Template Language (HTL) or Sightly is specifically designed for AEM. For today it is the default template system for AEM. It takes the place of JSP (JavaServer Pages) as used in previous versions of AEM.
HTL uses an expression language to insert pieces of content into the rendered markup, and HTML5 data attributes to define statements over blocks of markup (like conditions or iterations). As HTL gets compiled into Java Servlets, the expressions and the HTL data attributes are evaluated entirely server-side and none remain visible in the resulting HTML.

## 2.8.1 Syntax

HTL comprises:
1. Expression Language. Used to insert pieces of content into the rendered markup. Sightly code is written using dollar sign ‘$’ and braces ‘{}’.${currentPage.title} where currentPage is a global object and title is a property.
2. Block statements. You can add a special data-attribute that starts with “data-sly-” and contains an HTL statement is quotes to any HTML element. The content in quotes can be either an HTL expression ( ${...} ) or a plain string, usually referring to an URL or a page name. For example,  <div data-sly-include=”main.html”/> is an HTL block expression that is bound to an ordinary HTML tag (<div>), and contains the definitive data-sly-... attribute. In this very case, the attribute is data-sly-include. It instructs sightly to insert a portion of markup stored in main.html into the current <div> tag.

The expression syntax includes literals, variables, operators, and options.
Literals can be Boolean, Integers (including exponentiation, floating point numbers are not supported), Strings and Arrays.
Variables, much like in Java or JS,  can represent a primitive value or an object with properties. There are two ways to access object properties, with a dot notation, or with a bracket notation:
${currentPage.title}
${currentPage['title']} or ${currentPage["title"]}
So called options can be added to every expression.
Everything after the @ is an option:
${myVar @ optOne}
Each option can be used in several ways:
1. Option can work as a functions argument - it accepts the expression as an argument and processes it.
2. When the expression is located in a data-sly-* statement, they allow providing instructions or parameters to that statement.
On a plain expression (that is not located in a data-sly-* statement), the following options are possible: format (concatenates strings), i18n (translates strings), join (Defines the string delimiter to display between items of an array), context (Controls how the HTML escaping and XSS protection applies).
The <sly> HTML tag can be used to remove the current element, allowing only its children to be displayed.