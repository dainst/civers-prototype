# Technical documentation

## Service architecture

Services:

- Scraper
- Citator
    - provides Widget
- DOI Registrar
- Widget Host
    - embeds Widget

The Citator and the DOI Registrar each have their own database to
keep track of registered resources (DOI, URL, storage paths, dates, etc.).

Additionally there is one file storage, which is shared amongst the 
Scraper and the Citator.

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
