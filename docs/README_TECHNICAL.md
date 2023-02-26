# Technical documentation

## Service architecture

Services and modules:

- DOI Registrar
- Citator
    - Comparator
    - Scraper
    - Resource Archive
- Widget Host

The *Citator* and the *DOI Registrar* each have their own database to
keep track of registered resources (DOI, URL, storage paths, dates, etc.).

The *Scraper* is implemented as a different Docker service, also logically it is part of 
the *Citator*. See below for more details on webscraping.

The *Comparator* is another logical module, which in our case is just comprised of a few lines of code
within the *Scraper* module. It is responsible vor versioning (see below).

There is one file storage, which is shared amongst the 
*Scraper* and the *Citator*. From the user perspective, it is a service accessible
via the *Citator*'s website. We call it the *Resource Archive* component.

### APIs

Note that apart from the three user interfaces there exist multiple backend service **APIs**.
The *DOI Registrar* has an API, the *Citator* has one API to request archival of arbitrary websites and one API which provides the *Widget*. Finally the *Scraper* itself provides an API which makes the scraping code directly accessible (omitting DOI generation).

## Versioning 

The *Comparator*'s role is to determine whether, on request of a DOI, a snapshot of a site is taken or an existing one is sufficient. The **decision mechanism** in our prototype is based on the date the resource has last changed. The idea is that sites provide such a date in a special markup field in the DOM, where it can be looked up by the *Scraper*. If the date is newer than one of the already archived versions of a given site, a new snapshot is taken and a new version is generated. If the date is older or equal to the date of the latest archived version, no new version gets created. If no date field is found in the markup, a new version gets created on every DOI request.

In any case the *Comparator*'s purpose is to limit the number of snapshots to take and versions to create. It is not a detail which is necessary to understand as a user of the system. The system always responds with a DOI when one is requested. This DOI is always linked to a version which is "up to date" at the point in time the DOI is requested (/ the citation is made).

### Identity of entities and design choices regarding DOIs

While we opted for a one to one correspondence between versions and DOIs, meaning that each new version of a given entity gets its own DOI, this must be considered as one possible design choice. Another would be to assign one DOI per entity.

An entity, it should be noted, is identified by a URL (although this is not implemented, excluding query params here could be a good idea). For example `https://arachne.dainst.org/entity/123` constitutes one entity, persisting through time. Different versions of an entity are associated to one another based on this assumption.

Note that a more complex scenario arises if the system is expected to cater for domain moves (for the sake example, let's say `arachne.dainst.org` moves to `arachne.dainst.de`). 

### Comparing versions

While the decision mechanism in the prototype is based on a date field comparison, delegating responsibility for maintaining and marking the date field accordingly, it is conceivable that other heuristics could be employed for automatically inferring if a site has changed. Usually this has to be based on some metadate taken from the HTML. Another option would be to infer it by comparison of the HTML. The simplest method here would be to check if the HTML stayed the same. A complex heuristic would be to check if the HTML is similar. It is not clear, however, if this can be made to lead to reliable results.

## Metadata extraction

What is also exemplified with the "Last updated" date which is used in the *Comparator's* decision making process regarding versioning, is the **extraction of metadata**. This really can include any field of interest. What is crucial is that there needs
to be some sort of contract defined between the websites which are "supported" in this regard and the *Citator* itself. The *Citator* of course needs to know where the metadata are to be found. In our case, this was achieved by annotating a DOM element with a special id (`civers-last-updated`).

## Webscraping

Modern webpages are often dynamically generated. That is the HTML the server delivers
to the users' webbrowsers may be (very) different from the HTML which makes up the site
as the users see it when it is finally rendered in their browsers. 
This is made possible by JavaScript manipulation of the HTML, which happens inside the users' webbrowsers. 
In extreme forms, the so-called single page applications (SPA), the entire page is constructed in this manner.
For a general approach to webscraping this means that one must take this kind of dynamic buildup of pages
into consideration.

To save a webpage, we use [Selenium](https://www.selenium.dev), a software which can "remote control" web browsers, to build up
a website dynamically in the abovementioned sense. Selenium can control different browsers. We use a headless 
(without a simulated graphical interface) version of Chrome here.

### Stylesheets

After the page is fetched and and `index.html` is saved to a storage site on the local machine, the next step
is to fetch referenced CSS stylesheets, and make the `index.html` reference them instead of those on the remote server.
This is important because they affect in a major way how sites appear, because they not only control colors, but also positioning
of HTML elements. And to archive a site means not being dependent in any way on remote artifacts, of course.

Note for example that, given a generated DOI of `c47ed2dc`, in an archived entity from [archne.dainst.org](https://arachne.dainst.org)
a reference to a stylesheet like

```
<link href="styles.css" rel="stylesheet"/>
```

gets rewritten as

```
<link href="/archive/c47ed2dc/styles.css" rel="stylesheet"/>
```

The webscraping unit download that file and stores it locally under the given path, such that a webserver on the local server
can deliver it together with the `index.html` of the archived page.

### Images

Images per se are handled exactly like stylesheets. The artifacts get downloaded and linked accordingly.
However, there is a special case, namely were images are not artifacts that can be downloaded from the server, but are stored as temporary blobs inside the browser.

#### Image blobs

Sometimes images are delivered to the browser in the form of blobs. The `src` attributes of the `img` tags
then reference something like the following: `blob:https://arachne.dainst.org/98fa9127-0ae4-417e-b207-b9dc08fbcce6`.
These URLs cannot be used to download the fetch the images directly from the server, as they are not "static" artefacts.
With a tool like Selenium, however, one can execute JavaScript to convert the blobs in the context of the running page.
The result is then *base64* converted and is saved to a file on a storage site on the local machine. The `src` fields
of the `img` tags then get adjusted accordingly, to reference the local files.

### Pop-Ups

Many sites nowadays present popups, where users must make decisions regarding to browser privacy. Usually this means choosing between some options of accepting or not accepting cookies. With the help of *Selenium*, which let's one interact with the browser in an automated fashion, it is possible to click one's way such that a page can be seen "as is" (i.e. without a popup obstructing the view). If this is or is not difficult to do in a general manner remains to be seen.9

### HTML cleaning

Although it is not implemented in the prototype, one should consider if the HTML can be cleaned. We can advise against modifying classes and ids because any such change potentially can break the layout of the page, because they may be referenced from the CSS. However one probably can remove JavaScript scripts without impunity. Also one can see if one can deactive buttons and other input fields. The archived should preserver its look, but not it's feel, so to speak. There is no necessity for it to be interactive (and we do not want suggeset interactivity where there is none) and we also want to prevent it making any requests by accident (although those may fail anyways).
