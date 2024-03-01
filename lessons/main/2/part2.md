## What is CRXDE

![image21.png](img%2Fimage21.png)

**CRXDE** stands for **C** ontent **R** epository **eX** treme/ **D** evelopment **E** nvironment Lite.

CRX is Adobe System's implementation of the JCR standard. Basically JCR is a database that looks like a file system, containing a tree of nodes with associated properties. Each node can have one primary Node type that defines the characteristics of the node. Each node can have zero or more child nodes and zero or more properties. Properties cannot have children but do have values. The values of properties are where the actual pieces of data are stored. These can be of different types — strings, dates, numbers, binaries, and so forth.

With CRXDE Lite, you can create and edit files (like .html, .jsp), folders, templates, components, dialogs, nodes, properties and bundles.

This is a "low-level" mechanism. As such, it has little protection. So be careful: with CRX/DE you can break almost everything with a careless move.

Developers use CRX/DE to do fine alternations to the content. Other AEM users are encouraged to use other tools (e.g., the sites console). Access to CRX/DE can be limited in non-local instances due to security considerations.

### Getting Started with CRXDE Lite

Let's observe the user interface.
At the top of the screen we can see the **Top switcher bar**. It allows you to quickly switch between CRXDE Lite and Package Manager.

![image3.png](img%2Fimage3.png)

On the left is the **Explorer panel** which displays a tree of all the nodes in the repository. It looks like a folder structure on your computer.

![image31.png](img%2Fimage31.png)

We can manage nodes using the top bar

![image25.png](img%2Fimage25.png)

or by right-clicking on a node.

![image5.png](img%2Fimage5.png)

If we click on any node we can see its properties in the **Properties Tab**.

You can add new property, delete or edit existing ones.
![image8.png](img%2Fimage8.png)

At the top of the screen, you can also see the **Node path widget.** It displays the path to the currently selected node. You can also use it to jump to a node, by entering the path by hand, or pasting it from somewhere else, and hitting Enter.

![image7.png](img%2Fimage7.png)

The large area is the **Edit panel**.

Home tab: lets you search content and/or documentation and access developer resources (documentation, developer blog, knowledge base).

![image1.png](img%2Fimage1.png)

If you double-click a file in the Explorer pane to display its content, like for example a .html or a .jsp file. You can then modify it here and save the changes.

![image45.png](img%2Fimage45.png)

For more information about CRXDE Lite, you can visit the [official documentation](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/developer-tools/crxde.html%3Flang%3Dde).

### Queries in CRXDE

With CRXDE you can also execute queries and see results immediately. Supported languages:

• SQL-2 - this is the most common language in our projects, syntax similar to SQL and adapted to the tree structure of the repository.

• XPath

• SQL (deprecated)

To proceed to the execution of the query, select Query from the Tools menu.

![image37.png](img%2Fimage37.png)

Then select the type of query, enter the query and click Execute.

![image12.png](img%2Fimage12.png)

When we are executing a query, we should remember that the more details in the query we specify, the faster and more accurate the result will be.

Learn more about the SQL-2 query language here:
[https://jackrabbit.apache.org/oak/docs/query/grammar-sql2.html](https://jackrabbit.apache.org/oak/docs/query/grammar-sql2.html)

### Query examples:

Search pages by node name.

```sql
SELECT * FROM [cq:Page] AS page WHERE ISDESCENDANTNODE(page, '/content/we-retail') AND NAME() = 'arctic-surfing-in-lofoten'
```

Search all nodes with specific name that contains specific component

```sql
SELECT parent.* FROM [nt:unstructured] AS parent INNER JOIN [nt:unstructured] AS child ON ISDESCENDANTNODE(child,parent) WHERE ISDESCENDANTNODE(parent, '/content/we-retail') AND child.[sling:resourceType] = 'weretail/components/content/heroimage' AND parent.[jcr:path] LIKE '%root'
```

Search for pages with a specific template

```sql
SELECT parent.* FROM [cq:Page] AS parent INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,parent) WHERE ISDESCENDANTNODE(parent, '/content/we-retail') AND child.[cq:template] = '/conf/we-retail/settings/wcm/templates/hero-page'
```

Search for all nodes where property contains a specific value

```sql
SELECT * FROM [nt:unstructured] AS node WHERE ISDESCENDANTNODE(node, '/content/we-retail') AND node.[text] LIKE '%Store Location%'
```

Other commonly used queries: https://gist.github.com/floriankraft/8b3720464318cd5cd9e2

## Where logs are stored and how to read them. Sling Log Tailer.

AEM logs are stored in the crx-quickstart/logs directory. The most important log files are:

- error.log - contains error messages and exceptions.
- access.log - contains information about access operations on the AEM instance.
- request.log - contains information about requests to the AEM instance.

Developers can also define their own log files to output log messages from specific parts of the code.

Viewing log files is a common task for developers. The easiest way to access AEM's error.log file is to use the Sling Log Tailer.

http://localhost:4502/system/console/slinglog/tailer.txt?tail=10000&grep=\*&name=%2Flogs%2Ferror.log

Or from Web Console: Sling tab → Log Support. The Appender tab provides links to the various log files.

This endpoint produces static output, so you will be viewing the end of the log file at the time of the request. More log statements might have been added after your request, so you would need to refresh the browser window to see them.

You will need to provide request parameters to specify what log contents you want to see:

_name_: URL encoded relative path to the desired log file. Example: %2Flogs%2Ferror.log

_tail_: Number of lines to display, starting at the end of the file.

_grep_: Option to filter out the results. You can enter a search value as a parameter and only results containing that value will be shown.

---

[To previous part](part1.md)

[To Contents](../../../README.md)

