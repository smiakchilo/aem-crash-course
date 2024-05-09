# 5. Back-end of an AEM Component. Sling Models

We have just learned what an AEM component is. Let us just remind that it usually consists of these three parts: 
- The Java code that prepares and manages the data to display. Occasionally, this can be not a Java class but a JavaScript object (so-called JS use-object). But eventually, it also compiles into a Java class;
- The presentation itself. Nowadays it is usually backed by an HTML file with script insertions. This format is named *HTL*, or else *Sightly*. But sometimes you can see a JSP file instead;
- And the authoring part: the dialog and sometimes the in-place editing, set up via XML.

Today’s story is about the Java part.

## Sling Models Essentials 

### What is a Sling Model?

A Java class behind an AEM component is meant to be just a container of info that the component displays. No tricky logic - just properties and their getters. Such classes are commonly named "Plain old Java objects" – POJOs. The same objects are sometimes called "Data transportation objects" – DTOs. 

In previous AEM versions, component classes used to extend a single abstract superclass by the name of `WcmUsePojo`. You can still meet such in a legacy codebase quite often. But using a `WcmUsePojo` descendant is not quite convenient. Sometimes you really need to derive your class from some other ancestor. Also, with `WcmUsePojo`, you have to write down a bunch of boilerplate code for proper initialization. We added such a class in our sample project for your reference.

Later, Apache Sling came up with a better solution – Sling models. Now it is the most welcomed candidate for an AEM component backend.

A Sling Model is a POJO class or else an interface that bears data. The source of this data is usually:

- an HTTP request with its attributes, parameters, headers, etc.; 
- or a stored JCR resource (a similarity of a "database entry" in the AEM world) with its properties and, possibly, child resources;
- or both.

A request or a resource can contain any data. There is no precise structure. On the other hand, a Sling model, being a Java class, is strictly structured. It only contains the fields that it declares (or inherits). 

That is why a model is "less" than the modeled entity. It is a projection – something that reminds of a flat shadow cast by a 3D object on some surface. 
```mermaid
graph TD
    B([Sling Model 1])
    A{HTTP Request<br>or Resource}
    C([Sling Model 2])
    D([Sling Model 3])
    B-->A
    A-->B
    A-->C
    C-->A
    A-->D
    D-->A
```

A request or a resource can be accompanied by multiple models, each possessing a subset of properties. In this respect, they say of a model as an *adaptor*. A model adapts a request, a resource, or another entity to some view that has the need in a particular subset of data fields. 

Adapting is a keystone of the "philosophy" of Sling. A lot of things are adapted to another lot. Later you will see calls to `adaptTo()` in abundance of places. No accident that the most prominent biannual summit of AEM developers is also named [AdaptTo](https://adapt.to/2021/en.html).

Let us talk about how Sling models are actually produced.

### Model classes and how they receive data

A Sling model is just a class annotated with `@Model`. Such a class contains a bunch of annotated members (fields or methods). Sling creates an instance of a `@Model`-annotated class, then looks at the class members’ annotations and assigns values to appropriate members. It uses reflection for that. Generally speaking, it works much the same as it does in _Spring_ or _Hibernate_.

<details>
<summary><em style="color:#aaa; font-weight: bold">Make sure that models are in the proper Java package (click to expand)</em></summary>

A model class must sit within a package which is exported via the bundle manifest. If your project uses _bnd-maven-plugin_ to compile the bundle manifest, you must make sure that every model is in a package that has a _package-info.java_ file with `@Version` inside.

> Our sample project uses this exact technique. See the _package-info.java_ file [here](/project/core/src/main/java/com/exadel/aem/core/models/package-info.java).

Otherwise, you project may be using _maven-bundle-plugin_. In such a case, make sure that the `<Sling-Model-Packages>` instruction contains you model package or any of its parents.
```xml
<plugin>
    <groupId>org.apache.felix</groupId>
    <artifactId>maven-bundle-plugin</artifactId>
    <configuration>
        <!-- ... -->
        <instructions>
            <Sling-Model-Packages>com.exadel.aem.models</Sling-Model-Packages>
        </instructions>
    </configuration>
</plugin>
```
If your model lives in a package not recognized by the plugin, no error will be thrown. Yet the model’s fields will stay null.
</details>

A model class must be public and have a default no-argument public constructor (= no explicit constructor at all). A model class can be nested in another class but event so must be `public static`. A model class can extend any other class, be it a model itself or not, and implement any interface. If you are extending another model, be sure to duplicate the `@Model` annotation as it is not "inherited".

### @Model annotation. What it has to do with adaptables

Now let us get to the `@Model` annotation:
```java

@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class}, 
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MyModel {
    // ...
}
```
`@Model` must specify its `adaptables` property. It tells about the entities this model represents. 

The usual adaptables for a model are `SlingHttpServletRequest` and `Resource`. Why them? This is determined by the way a request is processed by AEM as an HTTP server.

Below is a look at this process.

<details>
<summary><em style="color:#aaa; font-weight: bold">How a Sling model fits in the HTTP request/response flow? (click to expand)</em></summary>

A Sling model is usually a part of an AEM component. A component lives in a page and displays a part of the page’s data. It is initialized when a user requests a page alongside other components that are bound to other parts of the same page’s data.

That being said, when a user navigates in browser to e.g. http://aem.server.com/content/home.html, an HTTP request is issued. In the AEM server, the request is being wrapped in an object of type `SlingHttpServletRequest`. Also an object of type `SlingHttpServletResponse` is created. The former contains properties of the user request: from query parameters to cookies and posted payload if there’s any. The latter is used to write out the server’s output.

The _Request_ and the _Response_ objects will go through a series of filters and ultimately land in some HTTP Servlet. You definitely know this principle from the learning of Java HTTP server and _Apache Tomcat_.

Unless the request is some specific one, the servlet will be the so-called _Default Get Servlet_ designed to serve requests for ordinary pages.

What does the _Default Servlet_ do with the request's URL? It looks for the node at path _/content/home_ in the content storage – the JCR. If there is such, and it has its `sling:resourceType` property, the servlet gives control to the component represented by the `sling:resourceType` value. Virtually, the servlet "asks" the component to render the page. 

The component is given the pair of `SlingHttpServletRequest` and `SlingHttpServletResponse` objects that the Default Servlet has received just before.

```mermaid
flowchart TD
    subgraph s1[" "]
        direction LR
        A[User requests<br>http://aem.server/content/home.html]-->B[Server receives the request]
    end
    subgraph s2[" "]
        direction LR
        C["Original"<br>request]-->D1[SlingHttpServletRequest]
        C-->D2[SlingHttpServletResponse]
        D1-->|...Filters|E[Default Get Servlet]
        D2-->|...Filters|E
    end
    subgraph s3[" "]
        direction LR
        F[Default Get Servlet...]-->G(..considers node /content/home,<br>wraps in Resource)
        G-->|Request+Resource<br>Response|H(...finds appropriate<br>Page component)
        H-->|Request+Resource<br>Response|I[Page component<br>renders /content/home]
    end
    s1-->s2-->s3
```

Inside this request, there’s a reference to the node that has been requested: _/content/home_. The node is decorated with a `Resource` object. The `Resource` is a higher-level abstraction over the node: a sort of "Node Plus".

Of all the facilities that the `Resource` provides, the most notable is `ValueMap` – the collection of the node's properties as a key-value map. Since the resource is a part of a resource tree and may have descendants, there can also be a collection of child resources, plus the _parent_ resource.

Such a component as that was "asked" to process _/content/home.html_ is a page component. It matches a page resource. A page resource has children. Each of them is usually processed by a component of its own. So, there is a tree of resources and, accordingly, a tree of components. 

The page component asks each of its child components to render a part of the page. Every child component is given `SlingHttpServletRequest` and `SlingHttpServletResponse`. Each of the request objects passed to the "children's" level encapsulates a resource. This time it is not the page resource but merely a child resource inside the page.
```mermaid
flowchart TD
    A["/content/home<br>(Page resource)"]
    A-->Ac(Page component)-->Am((Page<br>model))
    subgraph C[" "]
    direction LR
        c1["/content/home/jcr:content/child1<br>(Child resource)"]-->c1c(Child component)-->c1m((Model))
        c2["/content/home/jcr:content/child2<br>(Child resource)"]-->c2c(Child component)-->c2m((Model))
        cN["/content/home/jcr:content/childN<br>(Child resource)"]-->cNc(Child component)-->cNm((Model))
    end
    A-->C
```

Child components with help of their bundled Sling models produce parts of the page. The parts build up to the whole content of the page (the page component itself adds its share). The content is written into `SlingHttpServletResponse`, then returns to the _Servlet_. The servlet outputs it back to the user.

```mermaid
flowchart TD
    subgraph s1[" "]
        direction LR
        G[Page component]
        G-->|Request+Page resource,<br>Response|G1((Page<br>model))
        G-->H[Child component 1]-->|Request+Child resource,<br>Response|H1((Child<br>component<br>model 1))
        G-->I[Child component 2]-->|Request+Child Resource,<br>Response|I1((Child<br>component<br>model 2))
        G-->J[Child component N]-->|Request+Child resource,<br>Response|J1((Child<br>component<br>model N))
    end
    subgraph s2[" "]
        direction LR
        P[Page component]
        K((Page<br>model))-->|Response|P
        L((Child<br>component<br>model 1))-->L1[Child<br>component 1]-->|Response|P
        M((Child<br>component<br>model 2))-->M1[Child<br>component 2]-->|Response|P
        N((Child<br>component<br>model N))-->N1[Child<br>component N]-->|Response|P
        P-->|Response|Q[To servlet<br>To browser]
    end
    s1-->s2
```
</details>

The Sling model we create for a component is the one that lives within the request/response flow. Therefore one can retrieve and inject a request attribute or a query parameter, or maybe a request header. 

Also, a request encapsulates a resource. So you can inject the properties of the resource (or strictly speaking, of its `ValueMap`). Also, you can inject the children of the current resource. Adapting from a request gives you freedom of choice. That’s why they recommend adapting models from a `SlingHttpServletRequest.class` whenever possible.

Unfortunately, this is not always possible. Sometimes models are not created in the flow of processing an HTTP user request. There can be a scheduled job or a spin-off routine that reads JCR nodes and retrieves them as resources but has nothing to do with HTTP stuff. Even in your component-bound model, you may have such a spin-off task that would retrieve more resources and adapt them to secondary models ignorant of the HTTP request.

In such cases, a model has to be adapted from just `Resource.class`. Understandably, there won’t be a possibility to inject anything bound to a request. 

Some models are designed to work in both scopes: when there’s a request and when there’s none. Such can be adapted from both `{SlingHttpServletRequest.class, Resource.class}` like in the sample above.

Now, look at the `defaultInjectionStrategy` property. It refers to how the Sling engine will handle the situation when it cannot inject the required data. We usually specify _OPTIONAL_, which means no exception will be thrown. The unsatisfied field or method will silently remain with just `null` or an initial primitive (`0` or `false`) as its value.

### Member-level injector annotations

More annotations are used in combination with class members. Look at the ["Diagnostic" model](/project/core/src/main/java/com/exadel/aem/core/models/Diagnostic.java) we provide in the sample project:
```java
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DiagnosticComponentModel {

    @ValueMapValue(name = "sling:resourceType", injectionStrategy=InjectionStrategy.OPTIONAL)
    private String resourceType;

    @ValueMapValue
    @Default(values = "This is a placeholder for the empty text")
    private String text;

    @ChildResource
    private Resource child1;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private ModelFactory modelFactory;

    @Inject
    @Via("resource")
    private int numericValue;

    // ... 
}
```
You see a scatter of Sling injector annotations here. Actually, they could use a lot more. See, for example, [this cheat sheet](https://sourcedcode.com/blog/aem/aem-sling-model-injectors-annotations-cheat-sheet-reference-guide). 

Many annotations have somewhat similar effect. There are ways to do virtually the same thing with different ones. Above all, the Sling framework encourages developers to create annotations of their own and provide custom injection logic. So, in a project you’ll work on, there might be even greater variety. But the ones above are probably the most usual injector annotations.

One of the most widely used annotations is **@ValueMapValue**. It injects the value of a property of the current resource. This is what an AEM component does most of the time – displays properties of resources.

The name of the property is derived from the name of the annotated class member. The notation 
```
@ValueMapValue
private String text;
``` 
...means that Sling will be looking for a property named `text`.

However, many properties in JCR have names that are not suitable for Java class members. E.g., `jcr:title` or `sling:resourceType`. For this case we specify the `name` attribute of `@ValueMapValue.` Else, we can use the `@Named` annotation.

```java
@Model(adaptables = Resource.class)
public class MyModel {
    @ValueMapValue(name = "jcr:title")
    private String title;
}
```

Apart from this, by overriding a name, we can traverse across the resource tree. Then we can dig specific properties out of the resource we have reached. E.g., `@ValueMapValue(name = "child1/granchild1/title")` will descend two resources down from the current one and will try to find the value of the `title` property there.

<details>
<summary><em style="color:#aaa; font-weight: bold">What are the data types to be injected (click to expand)</em></summary>

Generally speaking, the types of class members used for injection must match the types of data stored in JCR. In real life, we usually deal with `boolean`, `int`, `long`, `double`, `String`, plus arrays of those types. Dates are presented by `Calendar` or `Date`.

`@ValueMapValue` injector can do some type coercion. If your value in JCR is of a numeric type, it can be injected into a String-typed field. Similarly, a string will be injected into an int-typed field if actually represents a number. But don’t push it too far: try to design your Sling model to match the JCR types as closely as possible.

What if type coercion fails, or else the required property is missing from the resource? Class members with a reference type will stay `null` while the primitives will have their initial values. There’s a way however to provide a meaningful fallback value. This is achieved with the @Default annotation.

```java
@Model(adaptables = Resource.class)
public class MyModel {
    @ValueMapValue
    @Default(values = "This is a placeholder for the empty text")
    private String text;
}
```
</details>

The **@ChildResource** annotation injects the child resource of the current resource as it promises. Surely you understand that the "current resource" is the underlying resource of the request in a request-adapted model or the very adapted resource if the model has `adaptables = Resource.class`.

What child resource to inject if there are many? This is, again, defined by the name of the annotated class member. In the code snippet above, Sling would be searching for the resource with the name of `child1`. Same as with `@ValueMapValue`, this could be altered with the `name` property of `@ChildResource`, or else with the additional `@Named` annotation.

```java
@Model(adaptables = Resource.class)
public class MyModel {
    @ChildResource(name = "child-1")
    private Resource child1;

    @ChildResource
    @Named("child-2")
    private Resource child2;
}
```

**@Self** is used to inject the very object that is being adapted by the current model. If the model has `adaptables = SlingHttpServletRequest.class`, you will receive the object of type `SlingHttpServletRequest` in the class member annotated with `@Self`. Surely this will work only if the class member itself is of type `SlingHttpServletRequest`. 

```java
@Model(adaptables = SlingHttpServletRequest.class)
public class MyModel1 {
    @Self
    private SlingHttpServletRequest request; // works OK
    
    @Self
    private Resource resource; // does not work. "Self" is not actually a Resource 
}
```

Similarly, if the model is adapted from `Resource.class`, you can get the current resource injected with `@Self` into a class member of type `Resource`.
```java
@Model(adaptables = Resource.class)
public class MyModel2 {
    @Self
    private SlingHttpServletRequest request; // does not work
    
    @Self
    private Resource resource; // works OK 
}
```
You may wonder why self-injection can be needed at all. There are indeed a number of cases. You may wish to "extract" from the request or resource some specific value for which there is no appropriate injector annotation. Or you may want to call a method of the request or resource that requires arguments. Or else pass the request or resource itself to some routine as an argument.

<details>
<summary><em style="color:#aaa; font-weight: bold">Injecting secondary Sling models with @ChildResource or @Self (click to expand)</em></summary>
Another thing to be mentioned is that both `@ChildResource` and `@Self` can be used to inject other Sling models. This is a very useful feature to avoid huge multi-data models and keep them logically simple and atomic. Imagine that you have in JCR resources that contain some text, image, and also several links. Each link, in its own turn, has an _URL_, a title, and a flag telling whether to open in a separate window.

A "naive" approach would be to design a model like this:
```java
@Model(adaptables = SlingHttpServletRequest.class)
public class TextImageLinks {
    @ValueMapValue
    private String text;
    
    @ValueMapValue
    private String image;
    
    @ValueMapValue
    private String link0Url;
    
    @ValueMapValue
    private String link0Title;
    
    @ValueMapValue
    private boolean link0NewWindow;

    @ValueMapValue
    private String link1Url;

    @ValueMapValue
    private String link1Title;

    @ValueMapValue
    private boolean link1NewWindow;
    
    // ...

    @ValueMapValue
    private String link5Url;

    @ValueMapValue
    private String link5Title;

    @ValueMapValue
    private boolean link5NewWindow;
}
```

We cut off getters and the rest of the stuff for brevity. But even without it, the code looks ugly. It repeats itself.

The _ValueMap_ of a resource behind the component would look as clumsy:
```mermaid
classDiagram
    class myResource {
        text
        image
        link0Url
        link0Title
        link0NewWindow
        link1Url
        link1Title
        link1NewWindow
        ...
        link5Url
        link5Title
        link5NewWindow
    }
```

The "link URL - link title - link new window" sequences are just crying for being extracted to separate child resources by the names of _link0... link1... linkN_. 

```mermaid
classDiagram
    class myResource {
        text
        image
    }
    class link0 {
        url
        title
        newWindow
    }
    class link1 {
        url
        title
        newWindow
    }
    class link5 {
        url
        title
        newWindow
    }
    myResource-->link0
    myResource-->link1
    myResource-->link5
```

... And if we reactor the resource this way we will definitely want to have a child model used like the following:
```java
@Model(adaptables = SlingHttpServletRequest.class)
public class TextImage {
    
    @ValueMapValue
    private String text;

    @ValueMapValue
    private String image;
    
    @ChildResource
    Link link0;

    @ChildResource
    Link link1;
    
    // ...
    
    @ChildResource
    Link link5;
}

@Model(adaptables = Resoruce.class)
public class Link {

    @ValueMapValue
    private String url;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private boolean newWindow;
}
```

<small>(Note that the adaptable for the upper model is `SlingHttpSrletRequest` while the adaptable for the lower one is `Resource`. This is not by chance. The request object that Sling considers "within" `TextImage` is one, while child resources are many. Remember that the request object encapsulates a resource already. If Sling tried to transfer this request to child resources, ambiguity would arise. So, the request adaptable is not quite applicable there. But we can address this problem - see more on this in the last chapter of this lesson).</small>

What if we had just one link? Would we bother moving it to a separate child resource? Probably not. All in all, a more branched node tree is harder to comprehend.

But still, logically speaking, an entity describing an illustrated text is not the same thing as an entity describing a link. Therefore, the resource could stay one, but it would be reasonable to have two models, anyway. And this is the case when `@Self` comes in handy.

```java
@Model(adaptables = SlingHttpServletRequest.class)
public class TextImage {
    
    @ValueMapValue
    private String text;

    @ValueMapValue
    private String image;
    
    @Self
    Link link;
}

@Model(adaptables = SlingHttpServletRequest.class)
public class Link {

    @ValueMapValue
    private String url;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private boolean newWindow;
}
```
```mermaid
flowchart LR
    subgraph myResource
        subgraph r0[ ]
            direction TB
            text
            image
        end
        subgraph r1[ ]
            direction TB
            url
            title
            newWindow
        end
    end
    m0([TextImage model])
    m1([Link model])
    r0.->m0
    r1.->m1
    classDef resourcePart fill:#fff, stroke:none;
    class r0 resourcePart
    class r1 resourcePart
```

<small>This time, we are OK with the adaptable of the lower model being `SlinhHttpServletRequest`. That's because the request and the resource stay the same in both models. Therefore, no ambiguity.</small>

.And now, back to the injector annotations.
</details>

The **@SlingObject** annotation can also be used to inject a request object into the request-adapted model. Besides, they use it for injecting a resource instance into a resource-adapted model. 

However, `@SlingObject` is more generic. For example, in a request-adapted model it can inject the underlying resource – the one to which the request was actually sent. This exact case is presented in the code snippet above. It can also inject the response object, the resource resolver, the script helper. See more [here](https://sling.apache.org/apidocs/sling8/org/apache/sling/models/annotations/injectorspecific/SlingObject.html).

Sometimes there’s a need to have in your model a reference to a server-wide singleton which is usually called a _service_. Vaguely speaking, services in AEM play the same role and are handled in the same manner as services in Spring (we will speak about services in one of the next lessons). To retrieve an instance of such, we use **@OSGiService** annotation. This way, we can retrieve, e.g., an instance of ModelFactory. We will have a talk on this service later on.
```
@OSGiService
private ModelFactory modelFactory;
```

In a multitude of tutorials, you will see that they use **@Inject**. This is a non-specific injector annotation. Technically, this isn’t even a Sling annotation, but a part of Java extended core. _Sling_ makes use of it when it decides from the type of the annotated member what would be the best value to inject. That’s why using `@Inject` is somehow similar to using the keyword `var` in legacy JavaScript. 

<small>You must have noticed that in our _Diagnostic_ model we use `@Inject` in pair with another annotation - `@Via("resource")`. That's because `@Inject` alone is too generic for Sling to decide from which entity to take value.</small> 

There is a strong recommendation to never use `@Inject` but instead a more specific annotation for the sake of clarity and performance.

> Real-world samples of Sling models can be found [in our reference project](/project/core/src/main/java/com/exadel/aem/core/models).

### @PostConstruct. Debugging a Sling model

Injecting values in Sling models is fun and relieves of so many lines of boilerplate code. There is, however, a downside. The injection is performed by special injector services and is done under the hood. You will have a hard time finding out why some value was assigned to some class member or why it was `null` just as you expected otherwise. 

Moreover, sometimes you have trickier cases that the injectors just fail to cover. To say, not just retrieve a resource by the name `child1`, but to have `child1` if only `foo == "bar"` and inject `child2` if not.

For such cases, there is the post-construct method. This is any zero-argument instance method with the `@PostConstruct` annotation on it. The name of the method does not matter, nor does the visibility level (it is OK to be private). 

A post-construct method is automatically executed after the injection is done. This distinguishes a post-construct method from the constructor (the latter is executed first and before any injection). Within post-construct, the injected values can be handled or manually re-assigned, or modified in any way. 

```java
@Model(adaptables = Resource.class)
public class MyModel {

    @SlingObject
    private Resource resource;

    public MyModel() {
        assert resource == null;
    }
 
    @PostConstruct
    private void init() {
        assert resource != null;
        String path = resource.getPath();
        // ...
    }
}
```

It is considered good practice to avoid making a post-construct method too complicated. Models should be kept as straightforward and close to DTOs in structure as possible. If you need some heavy logic in post-construct, it may mean that the design of your model is not perfect.

If a model class extends another model class and both have `@PostConstruct`-annotated methods, both will execute beginning from the ancestral one. But these methods should not have the same name. Otherwise, only the method from the child class will be executed.

A post-construct method is a good candidate for putting a debug breakpoint if you want to see how the Sling model is initialized. But if you wonder why this particular value is injected and not another, you might need to find a way to debug the corresponding injector service. Still fairly possible, but let us speak about it on another occasion.

> Take a look at the implementation of `@PostConstruct` method [in the sample project model](/project/core/src/main/java/com/exadel/aem/core/models/Album.java).

### Interface-based models and other injection cases

You must have noticed that many times we spoke of injecting into "class members", not simply "fields". That’s mainly because you can base your model on not only a class but also an interface. Indeed, you can create an interface like

```java
@Model(
    adaptables = SlingHttpServletRequest.class, 
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public interface MyInterfaceModel {
    @Self
    SlingHttpServletRequest getRequest();

    @SlingObject
    Resource getResource();

    @ValueMapValue
    String getDescription();
}
```
…and it will work exactly the same as if it were a DTO class. Under the hood, Sling will create an anonymous class instance implementing the interface and then will store values in it. Interestingly, Sling is smart enough to even derive proper value names from names of interface methods (_"getDescription"_ => _"description"_).

Some say, using interfaces for models is more of the best practice than using classes. This is not only for _"I"_ in _SOLID_. An interface is the only Java entity that supports a kind of multiple inheritance. Think of a model `ModelAB.class` that technically consists of pieces of information that are also presented in `ModelA.class` and `ModelB.class`. All three models can be used when appropriate. It’s quite neat to have them as mere interfaces.

Besides, modern versions of Sling support the possibility to inject into constructor arguments like in the following example:
```java
@Model(adaptables = Resource.class)
public class MyModel { 
    @Inject
    public MyModel(@Named("propertyName") String propertyName) {
        // constructor code
    }
}
```
You can find a lot more examples in the official [Apache Sling doc on models](https://sling.apache.org/documentation/bundles/models.html).

---

[Continue reading](part2.md)

[To Contents](../../../README.md)
