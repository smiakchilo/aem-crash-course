# 2.1. AEM as a Client-Server Platform

## Introduction

### What is AEM and who needs it?

We are starting to learn AEM – the Adobe Experience Manager. This platform isn't well known among developers, and it needs some introduction.

AEM is basically a content management system (CMS) – a client-server application that enables users to collaborate on creating, storing, and publishing content. Such a system usually comprises an HTTP server, a database, a RESTful framework, and a caching request dispatcher. There are hundreds of CMS-es on the market, starting with the well-known _Wordpress_, _Drupal_, _Joomla_, _Wix_, _1C Bitrix_, _Salesforce_, etc. You would find a CMS for virtually any programming language and any web server platform.

In this respect, AEM is no different. It allows editing and publishing news, articles, photos, videos, goods for sale, and more.

What is special about AEM, is that it is very business-oriented. It is a CMS that tries to play as a CRM system. It includes tools for customer relationship management:

- presents services and goods that the business offers, and also the corporate values,
- attracts and manages visitors, analyzes their behavior and demands,
- prepares and conducts marketing campaigns.

That's why AEM is so welcomed by large businesses. At the same time, it's barely present in the small business and non-commercial segment. Owning an AEM-powered website is expensive. But it is also prestigious. Websites of Apple, Samsung, Hewlett-Packard, Nvidia, AT&T, Verizon, Mastercard, Wall Street Journal, HBO, NatGeo – this is where you find AEM.

### AEM and its ecosystem

AEM started in early 2000s. In those days it was developed by the Swiss company _Day.com_ and was known as _Communique_ (or just "_CQ_"). To the day, you can find a lot of stuff within AEM that is named "cq this" or "cq that". However, in 2010 this business was purchased by Adobe, and that's when merely Adobe Experience Manager was born. Now it is also integrated into a greater ecosystem known by the name of "Adobe Experience Cloud". This ecosystem also includes _Adobe Target_, _Adobe Campaign_, _Adobe Marketo_ and a lot more.

But let's get back to AEM itself.

### What are the major parts of AEM as a platform?

AEM is a Java-based CMS. Its backend is mostly Java 11, as of year 2024. In AEM we develop and configure services and servlets, have database connectivity and data access objects (or "models"), scheduled jobs and workflows. This makes AEM not that much different from other Java-based application servers, such as Spring, JBoss or GlassFish. However, there are some features that make AEM development stand out.

AEM is based on six major technologies.

#### 1) OSGi – Felix

The first one is _**OSGi**_. OSGi stands for "Open-Source Gateway initiative". This is the standard of modular application server – the one that can consist of many relatively independent units (so called "bundles").

Every bundle is effectively a JAR file that comprises some functionality and contains a particular manifest. The manifest describes the relations of the current bundle to other JARs within the application server, and what exact facilities it exposes.

Different bundles combine together like puzzle pieces. They associate based on their manifests: which one relates to whichever else, which one needs facilities of another. But the bundles do not melt together into a monolith. Bundles can be plugged in and out of the application server (kind of "hot-swapped") while it continues running.

The brand name of the OSGi-standard application server inside AEM is _**Apache Felix**_. Interestingly, this is not the only one on the market. You can find more OSGi frameworks out there: the Apache Karaf, Equinox, Eclipse Gemini and more. There are flavors of Spring itself that support OSGi.

#### 2) JCR – Jackrabbit Oak

The second technology is **JCR**. This reads as "Java Content Repository". JCR contains an API for accessing data repositories in a peculiar manner. Not so many people know it – although it's been in Java core for ages. JCR makes use of concepts such as "node", "property", "property value", "session" and so on. This is generally about how to access data in any storage, be it a relational database or some non-SQL document-oriented repo, or just a set of files.

Same as OSGi, JCR is a standard, not a concrete application or library.

And again, same as with Apache Felix, AEM contains an implementation of the standard, by the name of Apache Jackrabbit Oak (this is the play or words: **J** a **C** k **R** abbit is conceived to be fast as, well... rabbit!).

Jackrabbit, again, is not unique to AEM. You will find it under the hood of such other CMS-es as Hippo, Magnolia, OpenKM and more.

You won't find a relational database in AEM. All the data is organized in a tree of nodes. Every node can have arbitrary number of properties. It resembles a no-SQL DB such as MongoDB, but even more it resembles a virtual file system. Every piece of data managed is like a "file" living in some "folder" that is stored within another "folder" – up to the very root.

#### 3) ORM and REST – Apache Sling

The third technology is named **Apache Sling**. Sling is a magical thing. It plays as both the ORM framework and the RESTful API. Basically it is the way to read and write any kind of data within Jackrabbit (JCR) via an HTTP request. The path of the request directly maps to the place of the corresponding data in the JCR tree. This way you can read a "node" (literally, a "record") from the data store together with all its properties through a GET request as a JSON entity. Or else you can create, modify or delete such a record with a POST request that has its form-data payload.

Mapping data to web entities is just one trick of Sling. The other one is that it can map a "node" (or "record") to a Java class instance with use of several annotations. We call such classes "Sling models".

Sling is the true heart of AEM. Most of the practices AEM developers follow are actually done this way and not another because of the Sling and how it works. And if you start working with another Sling-powered CMS (for instance, the eponymous Sling CMS), you will find that it looks and feels much like AEM. Or vice versa)

#### 4) Templating – Sightly/HTL

The fourth is the templating language. Web pages in AEM are often backend-rendered, and thoroughly cached.

This corresponds to the requirements of big businesses. They need their content to be delivered very fast to huge audiences that use very different devices: from desktops to smartwatches. Some devices are not really performant, and sometimes connection speed is poor. But the delivery must stay perfect. That's why AEM sites are rarely SPAs and are usually not too much dynamic. We don't rely on the user's browser and how it chews on some tricky Javascript. We prefer to render most of the content on the server and deliver it ready.

It means we don't outload just JSONs. For many a time we render good old HTML. Therefore, we need a templating technique – some way to combine static markup with the dynamic part that comes from the database and from Java logic. For this sake, there's JSP. It has been around for ages, and you can actually do JSPs in AEM if you like it. But who actually does? So, instead of old-fashioned JSP, they offered a newer and fancier alternative. This is also an Apache project, known as _**Sightly**_ or, more commonly, _**HTL**_. HTL helps to construct separate logical and visual modules – so-called "components", -- that stick together to form up web pages. Every page is a composition of components (besides a page is a component itself). And components are usually templated with HTL.

#### 5) Web server -- Jetty

The fifth part is the web server itself – the engine that serves content over HTTP. You've probably learned Apache Tomcat and dealt with it if you ever developed a Spring project. In AEM, another web server is used – _**Eclipse Jetty**_. It is not much different from Tomcat and follows the same standard as it comes to servlets, filters, requests, request dispatching, etc. So if you know Tomcat, you can claim you know Jetty as well.

#### 6) Standard UI framework – Adobe Granite+Coral

You have certainly noticed that among the six pillars of AEM – and we have already mentioned five, - there hasn't yet been a single one which is "AEM-specific" and not an open-source. So, what finally distinguishes AEM from a toll-free open-source project? This is basically the sixth technology – the _**Adobe Granite**_.

Granite is a UI/UX framework that contains a huge set of standardized components for building standard-looking pages.

You may wonder – who needs standard-looking ones if every site that sells anything strives to look and feel unique?

And here goes an interesting twist.

Within a typical AEM installation, there are at least two sets of pages, kind of two different user experience spaces: the one for the content authors and the one for the visitors.

Most of the CMS-es have their "admin pages" – places in which authors create, review, modify content. In AEM, the notion of "admin page" climbed to a higher ground. The set of admin pages is very elaborate and is packed with quite complex instruments. Often the "backstage site" of an AEM installation is no smaller in scale and complexity than the division of the site that an ordinary user visits.

But, again, this "backstage site" does not need to look unique. It is the backstreet and not the high street – the workshop area, the factory of content. And it is largely built with use of Adobe Granite.

Granite deals with formal definitions of different page components (mostly in XML format) and converts them into actual web presentations applying some visual styles and interactivity. Granite is closely related to the library of widgets known as Adobe Coral. Sometimes people even mix them up. But there's still some difference. Granite is a rendering engine, and a standard for building web page parts in general. While Coral is just a library of visual components, and their styles.

### Understanding AEM in a whole – a recap

Guess this all was quite a load of information. Let us try to summarize it by answering in short, what AEM is?

1. It is a content-management system with some CRM features;
2. It is Java-based;
3. It comprises an application management framework (OSGi-standard _Apache Felix_) consisting of "bundles" which are JAR files with specific manifests;
4. Among the many hundred bundles shipped with AEM are:
- the web server – _Jetty_;
- the database-like storage drivers and API known as _Jackrabbit Oak_;
- the peculiar REST/ORM engine _Apache Sling_;
- and the templating engine for HTML rendering known as _Sightly_, or else _HTL_.
5. As a website host, AEM consists of at least two "spaces". These are:
- the pages and other resources profiled for ordinary users,
- and the "authoring" pages – those for creating and manipulating content that ordinary users or customers see. The authoring pages are mostly created with and driven by _Adobe Granite_ with the _Coral UI_ library;
6. Pages of both the "authoring" space and the "customer" space are composed largely in the same manner. Both contain so-called _components_. Components are like "Lego bricks" for creating pages. Pages consist of components, and every piece of information that is displayed in a page is just there because it was entered into some component and is exposed through it.

### Now, what should I learn in AEM, and what will I do?

This generally gives as a clue to what a developer's job in AEM looks like.

First, developers create components that build up to site pages.

A component usually contains:

- a back-end part (a Java class or sometimes a JSP, or else a server-processed JS script),
- a "view" (an HTML markup or a Sightly/HTL file that mixes up HTML with embedded programming logic),
- and the authoring part – the interface to add or modify data.

Components are the thing that novice developers mostly deal with.

Also, a developer may need to create and modify page templates. This is generally related to configuring, not programming, but it is an important part. Templates define what page components are suitable for what pages, what initial data is displayed in a component before someone approached to edit it, and many similar things.

Then, a developer can be assigned to create a service or a servlet. Services and servlets act behind the scene of components and usually encapsulate different data manipulations. They affect the way components are displayed as well.

On higher ground, a developer will tackle scheduled jobs and workflows – processes that affect the functioning of the site overall, its health and structure.

And yet further, a developer may need to put a hand to dispatching tasks: how site resources are mapped to different HTTP requests, what requests are rejected or forwarded, what content is cached in what way, etc.

### A word on the front-end part

With describing all of this, we haven't actually spoken about what a front-end person would do in AEM. All of the activities mentioned are performed fully or in part by back-enders. But as you obviously understand UI is no less important. Let us close this gap to make the story complete.

First, a UI guy contributes in creating HTML/Sightly/HTL templates.

Second, it cooperates in creating the authoring dialogs for the components.

And third, they are definitely responsible for how the site will look like in browsers. There is usually a wider range of options, and the whole of the contemporary front-end stack is available.

Generally speaking, AEM from the standpoint of a front-ender is very much like any other web server:

- we use JS and TypeScript, can use React or Angular,
- we employ LESS or SASS;
- we engage elements of the SPA approach where applicable (but, again, not massively so far. However the most modern AEM versions claim they can work completely as SPA);
- and also do a lot of integrations with 3rd-party web services like authentication, social networking, chats, marketplaces, user tracking, personalization, etc.

That'll be it with the introductory part. More detail will come in due time. Now let us move to installing and setting up AEM.

---

[Continue reading](part2.md)

[To Contents](../../README.md)

