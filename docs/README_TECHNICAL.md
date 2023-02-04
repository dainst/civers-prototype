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
In extreme forms, the so-called single page applications, the entire page is constructed in this manner.
For a general approach to webscraping this means that one must take this kind of dynamic buildup of pages
into consideration.

To save a webpage, we use [Selenium](https://www.selenium.dev), a software which can "remote control" web browsers, to build up
a website dynamically in the abovementioned sense. Selenium can control different browsers. We use a headless 
(without a simulated graphical interface) version of Chrome here.

After the page is fetched and and `index.html` is saved to a storage site on the local machine, the next step
is to fetch referenced CSS stylesheets, and make the `index.html` reference them instead of those on the remote server.
This is important because they affect in a major way how sites appear, because they not only control colors, but also positioning
of HTML elements. And to archive a site means not being dependent in any way on remote artifacts, of course.
