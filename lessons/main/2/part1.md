# 2. Developer Methods Of Working With AEM. CRXDE

## Composition of the AEM project: packages and bundles. Bundle as part of a package.

### What is the OSGI bundle?

The fundamental element of the AEM technology stack is OSGI.

General definition tells us that the OSGi framework is a set of specifications that define a dynamic component system for Java. These specifications enable a development model where applications are (dynamically) composed of many reusable components. These components can be developed by different parties and can be deployed independently.

We will dive deeper into the OSGI bundles in lesson 2.11, but for now, let's just say that the OSGI bundle is a JAR file with specific metadata in the META-INF/MANIFEST.MF file.

### Difference between a bundle and package

A package is a ZIP file holding repository content in the form of a file-system serialization. This provides an easy-to-use-and-edit representation of files and folders.

In other words, the package is a ZIP file with additional metadata which contains html, xml and other resources. AEM packages are used to move content between instances, for example, from development to production. Also, all development artifacts, including bundles, are packed into packages and deployed to the AEM instance.

### Package settings

One of the most important meta files of a content package is the filter.xml which is present in the META-INF/vault directory. The filter.xml is used to load and initialize the WorkspaceFilter. The workspace filter defines what parts of the JCR repository are imported or exported during the respective package management operations.

The filter.xml consists of a set of filter elements, each with a mandatory root attribute and an optional list of include and exclude child elements.

![image27.png](img%2Fimage27.png)

You can find more information about the filter.xml settings here: [here](https://helpx.adobe.com/experience-manager/6-5/sites/developing/using/package-manager.html)

In AEM packages are deployed via the package manager.

## Package manager

Package Manager is a console for creating, installing, and managing content packages. 
For example, we can use it to install new content on our instance, move content between instances, or back up repository content.

### Create and download Package

1. To create package, first we need open Package Manager [http://localhost:4502/crx/packmgr/index.jsp)

You can also navigate to the package manager from the AEM's start screen. In the left rail, select Tools → Deployment → Packages.

![image6.png](img%2Fimage6.png)

2. Click on the "Create package" button.

![image4.jpg](img%2Fimage4.jpg)

3. In the New Package dialog box, specify Package name, Version (optional) and Group → OK

![image19.jpg](img%2Fimage19.jpg)

4. Now we have a package, but it is not built yet. First, we need to add filters to it. Click Edit on the newly created package.

![image30.jpg](img%2Fimage30.jpg)

5. Filters specify what content you want to put in this package. In the Edit Package dialog box, select the Filters tab, and click Add Filter. For the Root path, browse and select the path to the content which you want to put into the package and click Done, and then click Save. 

!Important! Do not try to include the entire repository in one package. The resulting package will be too large and difficult to manage. It is better to create several smaller packages, each containing a specific set of content.

![image46.jpg](img%2Fimage46.jpg)

6. Select Build to build the package, and then click Build in the confirmation dialog box.

![image47.jpg](img%2Fimage47.jpg)![image15.jpg](img%2Fimage15.jpg)

7. The package is now ready for download. Click on the Download to download the package.

![image41.jpg](img%2Fimage41.jpg)

### Upload and Install Package

For demonstration, we'll use a package with samples which is available by link; you can download it for this exercise: [https://drive.google.com/file/d/1D6yFV37hpSsDfoS3XlK9YIGFMVJ-lRkC/view?usp=sharing](https://drive.google.com/file/d/1D6yFV37hpSsDfoS3XlK9YIGFMVJ-lRkC/view?usp=sharing)

1. Click Upload Package.

![image34.png](img%2Fimage34.png)

2. In the Upload Package dialog box, click Browse and select the package with content that you want to download. Click OK.

![image39.jpg](img%2Fimage39.jpg)

3. After the package was uploaded, click Install.

![image48.png](img%2Fimage48.png)

4. In the Install Package dialog box, click Install.

![image33.png](img%2Fimage33.png)

5. Check the Activity Log. You can see the content that was added from the package.

![image16.png](img%2Fimage16.png)

6. You can check your content in CRXDE.

![image43.png](img%2Fimage43.png)

### Delete package

1. Choose the package which you are going to delete. If you want to also delete the content changes assosiated with this package, click on More and select Uninstall → click Uninstall in the confirmation dialog box. This will revert the content changes made by the package. If you only want to remove the package itself, skip this step.

![image49.jpg](img%2Fimage49.jpg)

2. Click on More and select Delete → click Delete. It will remove the package.

![image35.jpg](img%2Fimage35.jpg)

### Transfer content from one instance to another

If you want to copy content from one instance to another you need to go to the instance from which you need to download content → Create package with resources that you need → Download this package → Go to instance where you want to have this content → Upload and Install package.

---

[Continue reading](part2.md)

[To Contents](../../../README.md)

