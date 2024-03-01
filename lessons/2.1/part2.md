## Installing and starting AEM

### AEM versions. We are exploring an on-premise version

There are different versions of AEM. Not only they change as time passes – which is obvious, – but also differ in the way the platform itself is distributed.

As of the year 2024, AEM has manifested the move towards the _SaaS_ model. The solution which is now actively promoted is _AEM as a Cloud Service_ which resides in Adobe Cloud. However, many businesses still stick to a more traditional distribution. They install AEM on their own server equipment (this is known as "on-premise deployment"), or else use an interim model known as _AEM Managed Services_.

In this course, we refer to the most traditional approach and consider AEM as an application that we can handle in a server or a developer's machine. The modern version of such is **AEM 6.5**.

And please don't worry if you suppose you may be assigned to an AEM as a Cloud Service position – the main programming principles and the stack itself will remain exactly the same.

### System requirements

AEM is available as a single JAR file that you put to some folder and run (say hello to SpringBoot, again!))

Caution! You need to possess decent system resources, comparable to running a modern computer game. Allocate about 50Gb of disk space, and have at least 8Gb of RAM. 16GB RAM is very welcome if you are going to run IntelliJ IDEA or a similar IDE plus a web browser at the same time – which is definitely the case.

And, of course, you need Java installed. You can have different JVMs on your machine, but the one specified in system settings must be Java 11 (8 is possible, however, not recommended. 11+ versions will not work).

### Starting AEM for the first time. A look at the folder structure

Now we will run the _cq-author-4502.jar_ file. You will do it every time starting AEM. Startup takes decent time. AEM is never starting fast, and especially for the first time.

As it unrolls, you notice that a folder by the name of "_**crx-quickstart**_" is created. All the AEM stuff will live there. This folder is created once: in subsequent runs it will just be used. But if some day you find you AEM is corrupted, doesn't work properly, and there are no obvious fixes, you can just delete _crx-quickstart_ and start again. This will work like a "hard reset" to your mobile phone.

The most interesting subfolders of _crx-quickstart_ are:

- _**bin**_ – it contains pre-formed batch files for starting/restarting/stopping AEM in Windows and Linux;
- _**conf**_ – there are some Sling settings;
- _**logs**_ – it contains AEM logs as it promises;
- _**launchpad**_ – it contains the pre-installed bundles in the felix subfolder. Every bundle is a JAR file, and it is placed into a numbered subfolder of its own;
- and _**repository**_ – this is exactly the database.

Here is a little comment on the data storage.

JCR, as a standard, describes how data is accessed (via nodes, properties, etc.) It does not define in what way data is stored. This is determined by the NodeStore. There are different node stores. You can even store AEM data under a remote relational database! But most often, the file storage is used (the so-called "Segment Microkernel"). It comprises thousands of small and big binary files. They all live inside the "repository" folder.

### Cluster architecture of AEM. Author, Publish, and Dispatcher

Another thing must be learned just from the look at the files. It is the mode of AEM instance that is running.

You remember from the previous part that AEM suits the requirements of large corporate sites. These boast near-100% uptime and cannot stand a server being down due to a programming failure or being slow due to a lengthy maintenance routine. They also support both the "authoring" and the "customer" sub-sites, each consuming resources.

That's why AEM is cluster-oriented right from the start. It means that a typical AEM environment has at least one authoring instance, one or more publish instances, and also a dispatcher.

_**Authoring instance**_ is where creating and modifying content takes place. Though it is a fully functional AEM, usually only the special staff uses it: merely, the content authors, editors, designers, and as well developers.

As content is ready, it goes published – that is, copied to one or more _**publish instances**_. If publishes are many, they are expected to have exact copies of the same content and work as a cluster.

Again, a publish is a fully-functional AEM instance. But its features are somewhat deliberately reduced, and some are just locked for security. Opposite to the authoring instances, tech guys rarely directly use, or even access publishes, but the ordinary site visitors are directed right there.

The visits are distributed among publishes with the _**dispatcher**_. It plays as a load balancer and router. Also, it caches the ready HTML pages and other assets. Due to that, users often receive the ready content from the cache, without even bothering an AEM server.

Dispatcher, however, is not really an AEM instance – it is a far simpler web server (an Apache _httpd_ application or an _IIS_ application with plugins). It just works in a company with AEM.

From what we've been talking about, it is understood that there isn't such thing as "just AEM". Any AEM we run performs as either author, or publish. On developers' machines most often it is author. That's because the author is completely functional and has none of the features locked. However, at some point you may want to run a publish as well. And you can actually have author and publish running in parallel in the same computer if you've got enough RAM.

How I determine whether I run an author or a publish? There are different ways, but the easiest one is just the name of the JAR file. That's why it says "cq-author-4502.jar" – it is the author instance (or, to say it clear, it _unrolls as the author instance upon the first run_) and works on port 4502.

If I wanted to switch to a publish instance, or else just start a publish in parallel, I would rename (or, accordingly, copy) the "cq-author-4502.jar" into "cq-publish-4503".

Please mind the two things:

1. 4502 and 4503 are the standard ports for an author and publish respectively. Although it is possible to provide any other sensible number, changing them is not recommended.
2. You cannot switch the role of instance from author to publish and vice versa after it has been unpacked and run. If you absolutely need it, remove the "crx-quickstart" folder (effectively erasing all the database), rename the JAR file and then start again.

### AEM run modes

"Author" and "publish" are two modes of AEM. They have the special name – "installation run modes". There are more run modes, and you can define your very own. All the run modes apart from the installation ones are known as "customized run modes".

Take a run mode as an application profile. You can run your, e.g., Chrome with different user profiles. They will have different settings, then. The same is about customized run modes. They can have any name, and within an AEM instance you can set up different properties for them defining how services and other system components will behave.

That's exactly how they alternate behavior for author and publish instances, restrict some features for publishers.

Same principles apply when in a commercial project there are different AEM environments. Usually there's an environment for development and testing, another one for staging changes, and, obviously, the production. So it's quite customary to have a run mode named "_dev_" or "_qa_" for the testing environment, also the one named "_stage_", and the one named "_prod_".

Run modes combine. So, an authoring instance in testing environment will have both the run modes "author" + "qa", and so on.

You, again, can define any number of run modes for a server. There are several ways to do this. The simplest one in just run your AEM with a command like _java -jar cq-author-4502.jar -r localhost,test,dummy_. "Localhost", "test", and "dummy" will be the custom run modes, and "author" is the installation run mode. Another command which will produce the same effect is _java -jar cq-author-4502.jar -Dsling.run.modes=localhost,test,dummy_.

### More command-line switches

Usually AEM is run with more command line switches than that.

As a developer, you will probably want to debug AEM as an application server for your IDE. For that purpose you need to declare the debug mode and specify the _agentlib_ key like the following: _-agentlib:jdwp=transport=dt\_socket,server=y,suspend=n,address=\*:10240._ The most important part here is the _address_: it actually manifests the debugging port. A good IDE such as IntelliJ IDEA will tell you the expected value of the "agentlib" param when you set up the debugging profile.

More switches relate to the memory management. This is -Xms for the initial heap size and -Xmx for max heap size. If you instance experiences an OutOfMemory state from time to time, try increasing the _-Xmx_ in particular. Nowadays, the sizes are order of gigabytes for sure. It can be kind of 6 or 8 gigabytes for the _-Xmx_ param.

## First look at the running instance

Finally, our AEM instance is all dressed up and ready to meet us. We start with the login window. Default credentials are _admin/admin_.

![AEM_start_screen.png](img%2FAEM_start_screen.png)

### What we may find in the administrative interface

Now we're into the administrative pages. Or rather, into the modern-looking rendition of these pages named the "Touch UI". You will learn about another rendition in the next lesson.

There are two subdivisions of administrative pages. You can switch between them with two buttons to the left. The upper one is for manipulating content. The lower one is for changing settings, doing maintenance, monitoring, etc.

![tools_and_nav.png](img%2Ftools_and_nav.png)

Pay attention to the "_**Sites**_" button. This is the central part. It opens a tree of web resources already present in the instance (yes – this is technically a tree, however, looking quite fancy). If you click a folder, it expands in the next column. If you want to do something to a particular resource, check the icon of it and select a toolbar option. You can, for instance, change the properties of a selected page – title, description, tags, the featured image, etc. Or you can edit the page's content, or else create another one – we will get back to it later.

![sites-console.png](img%2Fsites-console.png)

Beside the "Sites", there are "Assets". You can store and systematize images, or documents here. There's a simple image editor available.

![assets_console.png](img%2Fassets_console.png)

You can create fragments – not complete pages but some parts of them that can be reused in several places.

Also, you are able to create adaptive forms, like in Google Forms. Can set up a translation project, design a catalog of goods and services, and more.

The hammer icon leads to the "_**Tools**_" subdivision. It has a proper menu exposing several types of maintenance jobs and activities. There are quite a lot out of the box, and different AEM extensions add even more. Some of them you will learn in this course, others you will get to know better when assigned to a real-world project. But some will remain undiscovered for a very long time and maybe forever.

![tools.png](img%2Ftools.png)

That's alright – AEM is very comprehensive and still growing. It is packed with features that not every business actually needs. You cannot grasp AEM as a singular, monolithic thing. What is important is to keep leaning with every working day.

### Authoring pages. Main toolbar

Let's get back to the "Sites" and open a web page for editing. AEM comes with a sample website depicting a sports gear shop. It is product-oriented and multilanguage. Being multilingual and multi-region is a trademark feature of most AEM sites, and a substantial part of AEM logic is devoted to providing localization and translation facilities.

For now, let us open a default, non-translated page with the "Edit" button (default pages are always in English by tradition). This is the page in authoring mode, or else, "editmode". It's fine if the loading takes some time – AEM is supposed to be fast for big servers but is virtually never fast for ordinary computers, especially if there are some extensions and customizations.

When the page is finally loaded, it looks much like it will look for an ordinary visitor (however, this is not true to all the pages) and yet it has additional facilities.

The first thing that strikes our attention is the upper toolbar. You can open a side rail with the palette of embeddable media and components from here. Also, you can bring the menu of overall page actions, or which the first and probably most important is the page properties editing dialog. To the right is the switcher of display modes and the button to jump directly to the preview mode.

![edit_mode.png](img%2Fedit_mode.png)

### Pages' view modes

The state of the page we currently in is defined as some _view mode_ – namely, the edit mode. There are more view modes: in particular, there's preview mode and disabled mode. Some page components behave differently depending on the view mode. Remember I said that the page in edit mode would look the same for the ordinary visitor, but you wouldn't bet your money on it?)

Above all, different modes produce different amounts of additional servicing output into the page markup. Disabled mode produces none, and this is the default mode in a publish. Preview mode produces more, but it makes components "think" they are in publish. That's why we use the preview mode button to get a fast impression of how publish would look like. Honestly, you can do the same by manually setting the "disabled" mode, but this would take a bit more time.

### Pages are composed of components

If we look below the main toolbar, we'll see that the page is visibly divided into sections. When we move the mouse above, they are highlighted with a blue rectangle and display a floating toolbar. These sections are indeed components. Virtually every page is composed of them. And remember what we already mentioned once – the page itself is another component. Components are Lego bricks, and a page is a Lego baseplate you attach your bricks to. Although it looks different, it is just another Lego element of the same material as bricks, and is shipped with the rest of the bricks.

From the context toolbar, you can duplicate a component by copy-pasting it, or delete it for good. You can also add new components of different kinds by either pressing the "plus" button on the area which says "Drag components here", or – naturally – dragging a component from the left rail onto the said area.

![toolbar.png](img%2Ftoolbar.png)

Nearly every component has a dialog. You can bring it on with either a double click on the component itself or a click on the "wrench" button in the toolbar. A dialog is the place where the content is entered or modified. You need the dialog specifically when you have just added a new component. It is empty as of now and needs some information to be entered – or, as we say, "authored".

![dialog.png](img%2Fdialog.png)

Components can contain formatted text, images, video fragments, links, or even other components. Accordingly, authoring dialogs differ from rather straightforward to complex ones. Some dialogs even look and behave as "wizards" – they change or extend as you make some choices in them.

This reminds us that developing components is one of the most regular tasks in AEM. This task assumes that you pay equal attention to the view of the component, the data model that stands behind the view, and the authoring dialog (or an in-place editing interface) that authors will use to enter the data.

Every component is matched by its own node in the data storage. From the data storage standpoint, the page is usually presented as a parent node, and the components that are in the page – as a number of child nodes. Many components include secondary elements or literally child components of their own, which supposes that even more nodes are created under the first-level nodes. That is why every page is technically a node tree.

### A word on rollout and publishing

After the page is ready, it's often time to perform a distribution task – that is, to roll it out and / or publish it.

The _**rollout**_ task is for creating localized (translated) copies of the page according to the set of languages the site supports. Or, to speak of it more generally, to the set of audiences this site welcomes because rollout is usually but not exclusively about translating content. It may include any kind of adapting, re-formatting, re-rendering content to meet the culture or expectations of the audience.

The _**publishing**_ task performs what it says – transfers the content from the authoring instance to the publishes. It is matched by the reverse task – the revocation of content, known as unpublishing.

### Conclusion

And so, we have managed to install and run AEM, and had a very brief journey through the facilities of a typical AEM instance. We've seen the page editing interface and got quite a basic impression on what the authors of an AEM site do in their working hours. In the next lessons we will dive deeper in the data architecture of AEM and the technologies that empower it.

---

[To previous part](part1.md)

[To Contents](../../README.md)
