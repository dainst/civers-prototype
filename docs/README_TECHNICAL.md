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

The *Comparator*'s role is to determine whether, on request of a DOI, a snapshot of a site is taken or an existing one is sufficient. The decision mechanism in our prototype is based on the date the resource has last changed. The idea is that sites provide such a date in a special markup field in the DOM, where it can be looked up by the *Scraper*. If the date is newer than one of the already archived versions of a given site, a new snapshot is taken and a new version is generated. If the date is older or equal to the date of the latest archived version, no new version gets created. If no date field is found in the markup, a new version gets created on every DOI request.

In any case the *Comparator*'s purpose is to limit the number of snapshots to take and versions to create. It is not a detail which is necessary to understand as a user of the system. The system always responds with a DOI when one is requested. This DOI is always linked to a version which is "up to date" at the point in time the DOI is requested (/ the citation is made).

### Identity of entities and design choices regarding DOIs

While we opted for a one to one correspondence between versions and DOIs, meaning that each new version of a given entity gets its own DOI, this must be considered as one possible design choice. Another would be to assign one DOI per entity.

An entity, it should be noted, is identified by a URL (excluding query params). For example `https://arachne.dainst.org/entity/123` constitutes one entity, persisting through time. Different versions of an entity are associated to one another based on this assumption.

Note that a more complex scenario arises if the system is expected to cater for domain moves (for the sake example, let's say `arachne.dainst.org` moves to `arachne.dainst.de`). 

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
<link href="archive/c47ed2dc/styles.css" rel="stylesheet"/>
```

The webscraping unit download that file and stores it locally under the given path, such that a webserver on the local server
can deliver it together with the `index.html` of the archived page.

### Image blobs

Sometimes images are delivered to the browser in the form of blobs. The `src` attributes of the `img` tags
then reference something like the following: `blob:https://arachne.dainst.org/98fa9127-0ae4-417e-b207-b9dc08fbcce6`.
These URLs cannot be used to download the fetch the images directly from the server, as they are not "static" artefacts.
With a tool like Selenium, however, one can execute JavaScript to convert the blobs in the context of the running page.
The result is then *base64* converted and is saved to a file on a storage site on the local machine. The `src` fields
of the `img` tags then get adjusted accordingly, to reference the local files.
